package main

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"strings"
	"time"

	"github.com/Azure/azure-sdk-for-go/sdk/azcore/policy"
	"github.com/Azure/azure-sdk-for-go/sdk/azidentity"
)

const (
	apiVersion        = "v1"
	maxResponseBytes  = 2 << 20
	defaultPollPeriod = 500 * time.Millisecond
)

type TokenProvider interface {
	Token(context.Context) (string, error)
}

type AzureCLITokenProvider struct {
	credential *azidentity.AzureCLICredential
}

func NewAzureCLITokenProvider() (*AzureCLITokenProvider, error) {
	credential, err := azidentity.NewAzureCLICredential(nil)
	if err != nil {
		return nil, fmt.Errorf("create Azure CLI credential: %w", err)
	}
	return &AzureCLITokenProvider{credential: credential}, nil
}

func (provider *AzureCLITokenProvider) Token(ctx context.Context) (string, error) {
	token, err := provider.credential.GetToken(ctx, policy.TokenRequestOptions{
		Scopes: []string{"https://ai.azure.com/.default"},
	})
	if err != nil {
		return "", fmt.Errorf("get Azure access token: %w", err)
	}
	return token.Token, nil
}

type DirectorClient struct {
	endpoint      *url.URL
	agentID       string
	httpClient    *http.Client
	tokenProvider TokenProvider
	pollPeriod    time.Duration
}

func NewDirectorClient(endpoint, agentID string, tokenProvider TokenProvider) (*DirectorClient, error) {
	parsedEndpoint, err := url.Parse(strings.TrimRight(endpoint, "/"))
	if err != nil || parsedEndpoint.Scheme != "https" || parsedEndpoint.Host == "" {
		return nil, errors.New("director endpoint must be an absolute HTTPS URL")
	}
	if agentID == "" {
		return nil, errors.New("director agent ID is required")
	}

	return &DirectorClient{
		endpoint:      parsedEndpoint,
		agentID:       agentID,
		httpClient:    &http.Client{Timeout: 45 * time.Second},
		tokenProvider: tokenProvider,
		pollPeriod:    defaultPollPeriod,
	}, nil
}

func (client *DirectorClient) Call(ctx context.Context, input DirectorInput) (*DirectorOutput, error) {
	if err := input.Validate(); err != nil {
		return nil, err
	}

	token, err := client.tokenProvider.Token(ctx)
	if err != nil {
		return nil, err
	}

	threadID, err := client.createThread(ctx, token)
	if err != nil {
		return nil, err
	}
	if err := client.addUserMessage(ctx, token, threadID, input); err != nil {
		return nil, err
	}
	runID, err := client.createRun(ctx, token, threadID)
	if err != nil {
		return nil, err
	}
	if err := client.waitForRun(ctx, token, threadID, runID); err != nil {
		return nil, err
	}

	rawOutput, err := client.lastAssistantMessage(ctx, token, threadID)
	if err != nil {
		return nil, err
	}

	var output DirectorOutput
	if err := json.Unmarshal([]byte(rawOutput), &output); err != nil {
		return nil, fmt.Errorf("decode director output: %w", err)
	}
	return &output, nil
}

func (client *DirectorClient) createThread(ctx context.Context, token string) (string, error) {
	var response struct {
		ID string `json:"id"`
	}
	if err := client.doJSON(ctx, token, http.MethodPost, "threads", map[string]any{}, &response); err != nil {
		return "", fmt.Errorf("create thread: %w", err)
	}
	return response.ID, nil
}

func (client *DirectorClient) addUserMessage(
	ctx context.Context,
	token string,
	threadID string,
	input DirectorInput,
) error {
	inputJSON, err := json.Marshal(input)
	if err != nil {
		return fmt.Errorf("encode director input: %w", err)
	}
	payload := map[string]any{
		"role": "user",
		"content": []map[string]string{{"type": "text", "text": string(inputJSON)}},
	}
	return client.doJSON(ctx, token, http.MethodPost, "threads/"+threadID+"/messages", payload, nil)
}

func (client *DirectorClient) createRun(ctx context.Context, token, threadID string) (string, error) {
	var response struct {
		ID string `json:"id"`
	}
	payload := map[string]string{"assistant_id": client.agentID}
	if err := client.doJSON(ctx, token, http.MethodPost, "threads/"+threadID+"/runs", payload, &response); err != nil {
		return "", fmt.Errorf("create run: %w", err)
	}
	return response.ID, nil
}

func (client *DirectorClient) waitForRun(ctx context.Context, token, threadID, runID string) error {
	path := "threads/" + threadID + "/runs/" + runID
	for {
		var response struct {
			Status string `json:"status"`
		}
		if err := client.doJSON(ctx, token, http.MethodGet, path, nil, &response); err != nil {
			return fmt.Errorf("read run state: %w", err)
		}
		switch response.Status {
		case "completed":
			return nil
		case "failed", "cancelled", "expired":
			return fmt.Errorf("director run ended with status %q", response.Status)
		}

		timer := time.NewTimer(client.pollPeriod)
		select {
		case <-ctx.Done():
			timer.Stop()
			return ctx.Err()
		case <-timer.C:
		}
	}
}

func (client *DirectorClient) lastAssistantMessage(ctx context.Context, token, threadID string) (string, error) {
	var response struct {
		Data []struct {
			Role    string `json:"role"`
			Content []struct {
				Type string `json:"type"`
				Text struct {
					Value string `json:"value"`
				} `json:"text"`
			} `json:"content"`
		} `json:"data"`
	}
	if err := client.doJSON(ctx, token, http.MethodGet, "threads/"+threadID+"/messages", nil, &response); err != nil {
		return "", err
	}
	for index := len(response.Data) - 1; index >= 0; index-- {
		message := response.Data[index]
		if message.Role == "assistant" && len(message.Content) > 0 && message.Content[0].Type == "text" {
			return message.Content[0].Text.Value, nil
		}
	}
	return "", errors.New("director returned no assistant message")
}

func (client *DirectorClient) doJSON(
	ctx context.Context,
	token string,
	method string,
	path string,
	payload any,
	output any,
) error {
	var body io.Reader
	if payload != nil {
		encoded, err := json.Marshal(payload)
		if err != nil {
			return err
		}
		body = bytes.NewReader(encoded)
	}

	requestURL := *client.endpoint
	requestURL.Path = strings.TrimRight(requestURL.Path, "/") + "/" + strings.TrimLeft(path, "/")
	query := requestURL.Query()
	query.Set("api-version", apiVersion)
	requestURL.RawQuery = query.Encode()

	request, err := http.NewRequestWithContext(ctx, method, requestURL.String(), body)
	if err != nil {
		return err
	}
	request.Header.Set("Authorization", "Bearer "+token)
	request.Header.Set("Content-Type", "application/json")

	response, err := client.httpClient.Do(request)
	if err != nil {
		return err
	}
	defer response.Body.Close()

	responseBody, err := io.ReadAll(io.LimitReader(response.Body, maxResponseBytes+1))
	if err != nil {
		return err
	}
	if len(responseBody) > maxResponseBytes {
		return errors.New("director response exceeded the size limit")
	}
	if response.StatusCode < 200 || response.StatusCode >= 300 {
		return fmt.Errorf("director API returned %s: %s", response.Status, strings.TrimSpace(string(responseBody)))
	}
	if output == nil || len(responseBody) == 0 {
		return nil
	}
	return json.Unmarshal(responseBody, output)
}
