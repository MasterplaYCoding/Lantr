# `.ltrn` document format

Lantr documents are ZIP archives with JSON indexes and optional image assets.

```text
presentation.ltrn
├── manifest.json
├── metadata.json
├── theme.json
├── arc/
│   └── blocks.json
├── arc/blocks/
│   └── block_001.json
├── slides/
│   └── slide_001.json
└── images/
    └── imported-image.png
```

Android uses the Storage Access Framework and content URIs. Desktop uses ordinary files, native associations, startup arguments, and a single-instance handoff so a document opened by the shell reaches the running app.

## Archive safety

The public snapshot applies limits before decoding an archive:

- at most 2,048 entries;
- at most 256 MiB of declared uncompressed content;
- at most 2 MiB per JSON entry;
- at most 64 MiB per image entry;
- no parent-directory components or Windows path separators in entry names.

These controls reduce accidental and hostile resource expansion. They are not a substitute for fuzzing or a formal file-format specification.

## Compatibility

The Android/Windows implementation was built around the shared `.ltrn` product concept. Cross-platform compatibility should be asserted with fixtures produced by both implementations before claiming full interchangeability.
