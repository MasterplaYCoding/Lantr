# Building Lantr with Compose Multiplatform

Lantr asked whether one small team could build a presentation editor for phones, tablets, and Windows without giving up the interaction patterns that make an editor feel native.

## The product bet

The app begins with audience, purpose, duration, and narrative structure. That decision produced two coupled editors: an arc canvas for the story and a slide canvas for the visual artifact. Persistence, selection, drag and drop, preview rendering, themes, and navigation all had to respect both.

## What worked

- A shared domain model kept Android and desktop documents aligned.
- Compose made the adaptive navigation, themes, inspectors, and most editor surfaces reusable.
- Explicit view models provided stable mutation points for a deeply nested document.
- Platform source sets kept file opening, windows, images, and drag behavior honest.

## Where the shared abstraction leaked

Desktop expects windows, native file associations, mouse affordances, and single-instance behavior. Android expects activities, content URIs, lifecycle-aware launchers, touch targets, and system document providers. The shared UI was valuable only when those differences stayed visible at explicit boundaries.

The early code also grew too horizontally. Toolbar, arc-canvas, and model catalog files accumulated unrelated responsibilities. The archive cleanup removes dead code, repairs package boundaries, and documents where further extraction should happen without pretending the original product was immaculate.

## What I would change

I would define the `.ltrn` codec as a shared tested component earlier, separate persisted domain data from display metadata, require focused tests for every document mutation, and keep experiment code outside the app from day one.

I would keep the narrative hierarchy, shared Compose UI, and explicit platform boundaries. Those choices express what was distinctive about the product.
