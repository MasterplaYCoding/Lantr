# Platform boundaries

Most editor code lives in `commonMain`; platform behavior is expressed through small `expect`/`actual` or coordinator boundaries.

## Android

- Activity lifecycle and external document intents.
- Storage Access Framework open/save flows.
- Content URI image import.
- Android drag-and-drop APIs.
- Window size and back handling.

## Desktop JVM

- Compose desktop windows and native distributions.
- `.ltrn` file association and command-line opening.
- Single-instance file handoff.
- Native file and image operations.
- Desktop drag gestures and theme integration.

Platform abstractions should describe a capability—open a document, load an image, hand off a file—not mirror an operating-system API in common code.
