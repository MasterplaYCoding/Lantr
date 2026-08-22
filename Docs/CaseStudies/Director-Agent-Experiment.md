# Director agent experiment

The Director experiment asked a narrower question than the editor: can an unstructured brain dump become a typed narrative arc without giving a model permission to rewrite the entire presentation?

The prototype accepted either a `fill` request for a new arc or a `diff` request containing an existing arc. It sent the typed input to an Azure-hosted agent and expected an arc plus a confidence score.

## Useful result

The experiment made the agent boundary concrete. The editor does not need prose from a model; it needs a validated semantic proposal that can be inspected before it mutates product state.

## Original limitations

The first prototype was coupled to one developer's Azure CLI session and hard-coded project identifiers. It polled without a request context, had no body limits or offline boundary, and lived on an outdated application branch.

## Archive treatment

The preserved experiment is independently buildable. Configuration comes from the environment, credentials come from Azure CLI at runtime, HTTP sizes and timeouts are bounded, and the handler is tested with an offline fake Director. It remains an experiment: it is not reachable from the archived editor UI and no cloud service is required to build Lantr.
