package main

import (
	"log"
	"net/http"
	"os"
	"time"
)

func main() {
	endpoint := os.Getenv("LANTR_DIRECTOR_ENDPOINT")
	agentID := os.Getenv("LANTR_DIRECTOR_AGENT_ID")
	if endpoint == "" || agentID == "" {
		log.Fatal("LANTR_DIRECTOR_ENDPOINT and LANTR_DIRECTOR_AGENT_ID are required")
	}

	tokenProvider, err := NewAzureCLITokenProvider()
	if err != nil {
		log.Fatal(err)
	}
	director, err := NewDirectorClient(endpoint, agentID, tokenProvider)
	if err != nil {
		log.Fatal(err)
	}

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	server := &http.Server{
		Addr:              ":" + port,
		Handler:           directorHandler(director),
		ReadHeaderTimeout: 5 * time.Second,
		ReadTimeout:       15 * time.Second,
		WriteTimeout:      60 * time.Second,
		IdleTimeout:       60 * time.Second,
	}

	log.Printf("Lantr Director experiment listening on http://localhost:%s", port)
	log.Fatal(server.ListenAndServe())
}
