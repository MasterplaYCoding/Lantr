# Architecture index

The code is the source of truth. These documents explain the boundaries that make the editor understandable without requiring a tour through every composable.

- [Presentation model](Presentation-Model.md)
- [Document format and file handling](Document-Format.md)
- [Platform boundaries](Platform-Boundaries.md)
- [Drag and drop](Drag-and-Drop.md)

## System shape

Lantr has four broad layers:

1. Persisted presentation, arc, section, story-block, slide, content-block, and theme models.
2. Presentation-, arc-, and slide-level view models that own mutations and selection.
3. Compose views for creation, navigation, arc editing, slide editing, inspectors, and settings.
4. Android and desktop JVM boundaries for files, windows, images, drag and drop, and platform behavior.

The archive intentionally keeps the working editor recognizable instead of rewriting it into a fictional textbook architecture. Cleanup focuses on explicit boundaries, removal of dead code, bounded document parsing, and tests around behavior that matters.
