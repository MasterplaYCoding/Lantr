# Director Service

An archived Lantr experiment that maps a raw idea into a typed narrative arc through an Azure AI agent.

## Status

Research prototype. It is not part of the editor runtime and contains no endpoint, agent identifier, or credential.

## Run

1. Install Go and Azure CLI.
2. Run `az login` with an account authorized for your Azure AI project.
3. Copy `.env.example` values into your shell environment.
4. Run `go test ./...`.
5. Run `go run .` and send the request in `example.http`.

Configuration:

- `LANTR_DIRECTOR_ENDPOINT` — absolute HTTPS Azure project endpoint.
- `LANTR_DIRECTOR_AGENT_ID` — agent/assistant identifier in that project.
- `PORT` — local HTTP port; defaults to `8080`.

## Contract

`fill` requires `suggested_arc`; `diff` requires an existing `arc`. Both require `user_message`. The service returns a typed arc and confidence score or a generic gateway error. Azure response bodies are never forwarded directly to callers.

## What this established

- semantic document operations are safer than unconstrained generated prose;
- the network/credential boundary should be injectable and independently testable;
- agent configuration belongs outside source;
- an experiment is easier to learn from when its failure modes remain visible.
