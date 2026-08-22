package main

import (
	"encoding/json"
	"errors"
	"net/http"
)

const maxRequestBytes = 1 << 20

func directorHandler(director Director) http.Handler {
	return http.HandlerFunc(func(writer http.ResponseWriter, request *http.Request) {
		if request.Method != http.MethodPost {
			writer.Header().Set("Allow", http.MethodPost)
			http.Error(writer, "only POST is allowed", http.StatusMethodNotAllowed)
			return
		}

		request.Body = http.MaxBytesReader(writer, request.Body, maxRequestBytes)
		decoder := json.NewDecoder(request.Body)
		decoder.DisallowUnknownFields()

		var input DirectorInput
		if err := decoder.Decode(&input); err != nil {
			http.Error(writer, "invalid request", http.StatusBadRequest)
			return
		}
		if err := input.Validate(); err != nil {
			http.Error(writer, err.Error(), http.StatusBadRequest)
			return
		}

		output, err := director.Call(request.Context(), input)
		if err != nil {
			var invalidInput invalidInputError
			if errors.As(err, &invalidInput) {
				http.Error(writer, err.Error(), http.StatusBadRequest)
				return
			}
			http.Error(writer, "director request failed", http.StatusBadGateway)
			return
		}

		writer.Header().Set("Content-Type", "application/json")
		if err := json.NewEncoder(writer).Encode(output); err != nil {
			http.Error(writer, "encode response", http.StatusInternalServerError)
		}
	})
}
