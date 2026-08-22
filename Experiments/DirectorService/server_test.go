package main

import (
	"context"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

type fakeDirector struct {
	output *DirectorOutput
	err    error
}

func (fake fakeDirector) Call(context.Context, DirectorInput) (*DirectorOutput, error) {
	return fake.output, fake.err
}

func TestDirectorHandlerReturnsTypedOutput(t *testing.T) {
	handler := directorHandler(fakeDirector{output: &DirectorOutput{ConfidenceScore: 0.9}})
	request := httptest.NewRequest(
		http.MethodPost,
		"/director",
		strings.NewReader(`{"mode":"fill","suggested_arc":{"sections":["Hook"]},"user_message":"Start with a surprise"}`),
	)
	response := httptest.NewRecorder()

	handler.ServeHTTP(response, request)

	if response.Code != http.StatusOK {
		t.Fatalf("expected 200, got %d: %s", response.Code, response.Body.String())
	}
	var output DirectorOutput
	if err := json.NewDecoder(response.Body).Decode(&output); err != nil {
		t.Fatal(err)
	}
	if output.ConfidenceScore != 0.9 {
		t.Fatalf("unexpected confidence score: %f", output.ConfidenceScore)
	}
}

func TestDirectorHandlerRejectsUnknownFields(t *testing.T) {
	handler := directorHandler(fakeDirector{})
	request := httptest.NewRequest(
		http.MethodPost,
		"/director",
		strings.NewReader(`{"mode":"fill","user_message":"hello","unexpected":true}`),
	)
	response := httptest.NewRecorder()

	handler.ServeHTTP(response, request)

	if response.Code != http.StatusBadRequest {
		t.Fatalf("expected 400, got %d", response.Code)
	}
}

func TestDirectorHandlerRejectsGet(t *testing.T) {
	response := httptest.NewRecorder()
	directorHandler(fakeDirector{}).ServeHTTP(
		response,
		httptest.NewRequest(http.MethodGet, "/director", nil),
	)

	if response.Code != http.StatusMethodNotAllowed {
		t.Fatalf("expected 405, got %d", response.Code)
	}
}
