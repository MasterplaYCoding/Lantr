# Presentation model

```text
Presentation
└── Arc
    └── StorySection
        └── StoryBlock
            └── Slide
                └── ContentBlock
```

The hierarchy is the central product decision. A slide is not only a page in a flat deck: it belongs to a story block with a rhetorical purpose, which belongs to a section in the presentation's arc.

`PresentationViewModel` owns deck-level selection and theme operations. `ArcViewModel` owns narrative structure. `SlideViewModel` owns content-block edits and ordering. UI code should request mutations through the view model responsible for that level rather than reconstructing the hierarchy locally.

Content blocks currently have text and image implementations. The interface is deliberately smaller than the UI surrounding it; future block types should extend serialization, archive validation, rendering, preview behavior, and tests together.
