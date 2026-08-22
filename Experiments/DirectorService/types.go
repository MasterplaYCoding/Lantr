package main

import "context"

type SuggestedArc struct {
	Sections []string `json:"sections"`
}

type Block struct {
	ID       string `json:"id"`
	Type     string `json:"type"`
	MainIdea string `json:"main_idea"`
}

type Section struct {
	ID     string  `json:"id"`
	Type   string  `json:"type"`
	Blocks []Block `json:"blocks"`
}

type Arc struct {
	Sections []Section `json:"sections"`
}

type DirectorInput struct {
	Mode         string        `json:"mode"`
	SuggestedArc *SuggestedArc `json:"suggested_arc,omitempty"`
	Arc          *Arc          `json:"arc,omitempty"`
	UserMessage  string        `json:"user_message"`
}

func (input DirectorInput) Validate() error {
	if input.UserMessage == "" {
		return errInvalidInput("user_message is required")
	}

	switch input.Mode {
	case "fill":
		if input.SuggestedArc == nil {
			return errInvalidInput("suggested_arc is required in fill mode")
		}
	case "diff":
		if input.Arc == nil {
			return errInvalidInput("arc is required in diff mode")
		}
	default:
		return errInvalidInput("mode must be fill or diff")
	}

	return nil
}

type DirectorOutput struct {
	Arc             Arc     `json:"arc"`
	ConfidenceScore float64 `json:"confidence_score"`
}

type Director interface {
	Call(context.Context, DirectorInput) (*DirectorOutput, error)
}

type invalidInputError string

func (err invalidInputError) Error() string { return string(err) }

func errInvalidInput(message string) error { return invalidInputError(message) }
