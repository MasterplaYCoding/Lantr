# Drag-and-Drop Implementation Guide

## ✅ What's Been Implemented

### 1. Platform-Specific Drag-and-Drop System

**Location:** `composeApp/src/commonMain/kotlin/app/lantr/lantr/Views/DragAndDrop/`

#### Files Created:
- **`DragAndDrop.kt`** (commonMain) - Expect class and data models
- **`DragAndDropAndroid.kt`** (androidMain) - Android implementation using native APIs
- **`DragAndDropDesktop.kt`** (jvmMain) - Desktop implementation using gesture detection
- **`DropZone.kt`** (commonMain) - Reusable drop zone composable

### 2. ContentBlock Drag-and-Drop ✅ FULLY WORKING (FIXED 2026-02-17)

**Location:** `SlideCanvasContentView.kt`

**Features:**
- ✅ Long-press to start dragging a content block
- ✅ Visual feedback (semi-transparent, scaled while dragging)
- ✅ Drop zones appear between blocks when dragging (48dp hit area)
- ✅ Drop zone highlights when hovering (64dp expanded)
- ✅ Smooth animations
- ✅ Works on both Android and Desktop
- ✅ **FIXED:** Consistent behavior with different block sizes (removed overlapping drop targets)
- ✅ **FIXED:** Android drag state now properly resets after drop

**How to Use:**
1. Open a slide in edit mode
2. Long-press any content block (text or image)
3. Drag it up or down
4. Drop zones will highlight as you hover
5. Release to drop in the new position

**Recent Fixes:**
- Removed redundant Column drop modifier that caused large blocks to block smaller drop zones
- Added global drag state tracking for Android to properly reset UI after drop
- Increased drop zone hit areas (48dp dragging, 64dp highlighted) for easier targeting
- Drop zones now only activate when dragging (prevents accidental triggers)

### 3. Key Components

#### `DragData` Class
```kotlin
data class DragData(
    val type: DragType,        // CONTENT_BLOCK, SLIDE, STORY_BLOCK
    val id: String,            // UUID of the dragged item
    val sourceIndex: Int,      // Original position
    val payload: String        // Additional data
)
```

#### `rememberDragAndDropManager()`
Factory function to create platform-specific drag-and-drop manager:
```kotlin
val dragDropManager = rememberDragAndDropManager()
```

#### `DropZone` Composable
Visual indicator for where items can be dropped:
- 8dp normal spacing
- 24dp when dragging  (visible)
- 48dp when hovering (highlighted with blue line)

---

## 🎨 Visual Feedback

### Dragging State:
- **Opacity:** 50% (semi-transparent)
- **Scale:** 102% (slightly larger)
- **Shadow:** Elevated appearance

### Drop Zones:
- **Normal:** 8dp visible gap
- **Dragging:** 48dp visible gap (easier to hit on mobile)
- **Hovering:** 64dp highlighted gap with gold background

---

## 📝 How to Implement for Slides

The infrastructure is ready! To add slide drag-and-drop in the inspector:

### Step 1: Update `PresentationInspectorContentView.kt`

Add drag-and-drop state:
```kotlin
val dragDropManager = rememberDragAndDropManager()
var draggedSlideId by remember { mutableStateOf<UUID?>(null) }
var hoveredDropIndex by remember { mutableStateOf<Int?>(null) }
```

### Step 2: Wrap `SlidePreviewView` with drag modifier

```kotlin
Box(
    modifier = dragDropManager.createDragModifier(
        data = DragData(
            type = DragType.SLIDE,
            id = slide.id.toString(),
            sourceIndex = slideIndex,
            payload = storyBlock.id.toString() // Include story block ID
        ),
        onDragStart = { draggedSlideId = slide.id },
        onDragEnd = {
            draggedSlideId = null
            hoveredDropIndex = null
        }
    ).graphicsLayer {
        alpha = if (draggedSlideId == slide.id) 0.5f else 1f
        scaleX = if (draggedSlideId == slide.id) 1.02f else 1f
        scaleY = if (draggedSlideId == slide.id) 1.02f else 1f
    }
) {
    SlidePreviewView(...)
}
```

### Step 3: Add DropZones between slides

```kotlin
storyBlock.slides.forEachIndexed { slideIndex, slide ->
    // Drop zone before this slide
    DropZone(
        targetIndex = slideIndex,
        isDragging = draggedSlideId != null,
        isHighlighted = hoveredDropIndex == slideIndex,
        dragDropManager = dragDropManager,
        onDrop = { dragData ->
            // Call viewModel.moveSlide(from, to)
            viewModel.moveSlide(
                fromIndex = dragData.sourceIndex,
                toIndex = slideIndex
            )
            true
        },
        onDragEntered = { hoveredDropIndex = slideIndex },
        onDragExited = { hoveredDropIndex = null }
    )

    // The slide itself (see Step 2)
    SlidePreviewView(...)
}
```

---

## 🔧 Platform Differences

### Android
- Uses native `dragAndDropSource` and `dragAndDropTarget` modifiers
- Automatic long-press detection
- Native shadow/preview during drag
- Hardware-accelerated performance

### Desktop (JVM)
- Uses `detectDragGesturesAfterLongPress`
- Manual coordinate tracking for hover detection
- Custom visual feedback with `graphicsLayer`
- Global state management for drop targets

---

## 🧪 Testing

### Android:
1. Build: `./gradlew compileDebugKotlinAndroid`
2. Long-press a content block
3. Drag and observe drop zones
4. Release to drop

### Desktop:
1. Build: `./gradlew compileKotlinJvm`
2. Long-press a content block
3. Drag and observe drop zones
4. Release to drop

---

## 📊 Architecture

```
┌─────────────────────────────────────────┐
│   Common Code (Your Views)              │
│   - SlideCanvasContentView              │
│   - PresentationInspectorContentView    │
└──────────────┬──────────────────────────┘
               │ uses
               ▼
┌─────────────────────────────────────────┐
│   rememberDragAndDropManager()          │
│   + DragData models                     │
│   + DropZone composable                 │
└──────────────┬──────────────────────────┘
               │ returns
               ▼
      ┌────────┴────────┐
      │                 │
      ▼                 ▼
┌──────────┐    ┌──────────────┐
│ Android  │    │   Desktop    │
│ Native   │    │   Gestures   │
│ D&D APIs │    │ + Tracking   │
└──────────┘    └──────────────┘
```

---

## 💡 Tips & Best Practices

### 1. Reordering Logic
When dropping, adjust the target index based on direction:
```kotlin
val adjustedIndex = if (fromIndex < toIndex) toIndex - 1 else toIndex
```

### 2. Prevent Self-Drop
Check if source and target are the same:
```kotlin
if (fromIndex != toIndex) {
    // Perform reorder
}
```

### 3. Visual Feedback
Always provide immediate feedback:
- Change opacity when dragging starts
- Show drop zones immediately
- Highlight on hover
- Animate reordering

### 4. Cleanup
Always reset state on drag end:
```kotlin
onDragEnd = {
    draggedBlockId = null
    hoveredDropIndex = null
}
```

---

## 🚀 Next Steps

### To implement slide drag-and-drop:
1. Follow steps in "How to Implement for Slides" section above
2. Update `PresentationViewModel.moveSlide()` if needed
3. Handle cross-story-block drops (optional)
4. Test on both platforms

### Future Enhancements:
- ✨ Haptic feedback on mobile
- ✨ Drag preview with slide thumbnail
- ✨ Multi-select and bulk drag
- ✨ Drag from inspector to canvas
- ✨ Arc mode story block reordering

---

## ❓ Troubleshooting

### "Cannot infer type" error
Make sure you're using `rememberDragAndDropManager()` not `DragAndDropManager()`

### Drop zones not highlighting
Check that you're updating `hoveredDropIndex` in `onDragEntered` callback

### Drag not starting
Ensure you're using **long-press**, not a regular tap

### Android: Drag not working at all
FIXED: Android now uses global drag state tracking since `dragAndDropSource` doesn't provide `onDragEnd`. The `DragAndDropTarget.onEnded()` callback now properly resets state.

### Desktop: Large blocks blocking drop zones
FIXED: Removed redundant drop modifiers on block containers. Only DropZone composables should be drop targets.

### Android: "Overload resolution ambiguity"
We're using `dragAndDropSource(transferData = { ... })` with explicit parameter name

### Desktop: "@Composable invocations" error
Don't use `remember` or `DisposableEffect` inside `createDropModifier`

### Drop zones too small to hit
FIXED: Increased drop zone heights to 48dp (dragging) and 64dp (highlighted) for better touch targeting, especially on mobile.

---

## 📚 References

- Android Drag-and-Drop: https://developer.android.com/develop/ui/compose/touch-input/pointer-input/drag-drop
- Compose Gestures: https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures
- Your ViewModel Methods:
  - `SlideViewModel.reorderContentBlock()` - line 93
  - `PresentationViewModel.moveSlide()` - Available but check implementation

---

**Status:** ✅ ContentBlocks drag-and-drop fully working on both platforms!
**Next:** Implement slides drag-and-drop using the same pattern
