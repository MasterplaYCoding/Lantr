# Contributing

Lantr is a retired-product source archive. Focused fixes, documentation corrections, tests, and small compatibility updates are welcome. Reintroducing a hosted account system or committing service credentials is out of scope.

Before opening a pull request:

1. Keep Android and desktop JVM source sets compiling.
2. Add focused tests for changed model, document, or service behavior.
3. Run `./gradlew :composeApp:jvmTest` and the Android tasks relevant to the change.
4. Run `go test ./...` inside any modified Go experiment.
5. Do not add signing files, machine configuration, credentials, telemetry identifiers, or generated build output.
6. Run `git diff --check` and review every final path before committing.

Experiments belong under `Experiments/` and need a README describing the question, setup, result, limitations, and archive status.
