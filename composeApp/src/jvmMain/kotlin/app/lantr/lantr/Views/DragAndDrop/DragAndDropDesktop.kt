package app.lantr.lantr.Views.DragAndDrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import java.util.UUID
import androidx.compose.ui.layout.onSizeChanged

/**
 * Desktop implementation of DragAndDropManager using Compose gesture detection.
 *
 * Since JVM/Desktop doesn't have native drag-and-drop APIs like Android,
 * we use manual gesture detection with pointerInput and coordinate tracking.
 */
actual class DragAndDropManager actual constructor() {

    // Global state to track the current drag operation
    companion object {
        private var currentDragData: DragData? by mutableStateOf(null)
        private var currentMousePosition by mutableStateOf(Offset.Zero)
        private var currentDragPreview by mutableStateOf<(@Composable () -> Unit)?>(null)
        private var draggedElementPosition by mutableStateOf(Offset.Zero)
        private var grabOffset by mutableStateOf(Offset.Zero)
        private val dropTargets = mutableStateMapOf<String, DropTargetInfo>()

        internal fun isDragging() = currentDragData != null
        internal fun getCurrentDragData() = currentDragData
        internal fun getCurrentMousePosition() = currentMousePosition
        internal fun getDragPreview() = currentDragPreview
        internal fun getDraggedElementPosition() = draggedElementPosition

        internal fun registerDropTarget(id: String, info: DropTargetInfo) {
            dropTargets[id] = info
        }

        internal fun unregisterDropTarget(id: String) {
            dropTargets.remove(id)
        }

        internal fun findHoveredDropTarget(): DropTargetInfo? {
            return dropTargets.values.firstOrNull { target ->
                target.bounds?.let { bounds ->
                    val rect = Rect(
                        bounds.position.x,
                        bounds.position.y,
                        bounds.position.x + bounds.size.width,
                        bounds.position.y + bounds.size.height
                    )
                    rect.contains(currentMousePosition)
                } ?: false
            }
        }
    }

    /**
     * Creates a modifier that makes a composable draggable on desktop.
     * Uses long-press gesture detection.
     *
     * Uses Modifier.composed so that `remember` gives a stable MutableState reference
     * shared between onGloballyPositioned and the cached pointerInput coroutine,
     * avoiding stale closure issues when the block's position changes.
     */
    actual fun createDragModifier(
        data: DragData,
        onDragStart: () -> Unit,
        onDragEnd: () -> Unit,
        dragPreview: (@Composable () -> Unit)?
    ): Modifier = Modifier.composed {
        // Stable reference: both onGloballyPositioned and the pointerInput coroutine
        // point to this same MutableState, so the coroutine always reads the latest position.
        val elementPositionRef = remember { mutableStateOf(Offset.Zero) }

        Modifier
            .onGloballyPositioned { coordinates ->
                elementPositionRef.value = coordinates.positionInRoot()
            }
            .pointerInput(data.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        val pos = elementPositionRef.value
                        currentDragData = data
                        currentDragPreview = dragPreview
                        draggedElementPosition = pos
                        grabOffset = offset
                        currentMousePosition = pos + offset
                        onDragStart()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentMousePosition += dragAmount

                        val hoveredTarget = findHoveredDropTarget()
                        dropTargets.values.forEach { target ->
                            if (target == hoveredTarget) {
                                if (!target.isHovered) {
                                    target.isHovered = true
                                    target.onDragEntered()
                                }
                            } else {
                                if (target.isHovered) {
                                    target.isHovered = false
                                    target.onDragExited()
                                }
                            }
                        }
                    },
                    onDragEnd = {
                        // Use tracked isHovered state rather than re-detecting by position —
                        // currentMousePosition may be slightly stale at release time.
                        val hoveredTarget = dropTargets.values.firstOrNull { it.isHovered }
                        hoveredTarget?.let { target ->
                            currentDragData?.let { target.onDrop(it) }
                        }
                        dropTargets.values.forEach { target ->
                            if (target.isHovered) {
                                target.isHovered = false
                                target.onDragExited()
                            }
                        }
                        currentDragData = null
                        currentDragPreview = null
                        currentMousePosition = Offset.Zero
                        draggedElementPosition = Offset.Zero
                        grabOffset = Offset.Zero
                        onDragEnd()
                    },
                    onDragCancel = {
                        dropTargets.values.forEach { target ->
                            if (target.isHovered) {
                                target.isHovered = false
                                target.onDragExited()
                            }
                        }
                        currentDragData = null
                        currentDragPreview = null
                        currentMousePosition = Offset.Zero
                        draggedElementPosition = Offset.Zero
                        grabOffset = Offset.Zero
                        onDragEnd()
                    }
                )
            }
    }

    /**
     * Creates a modifier that makes a composable accept drops on desktop.
     */
    actual fun createDropModifier(
        onDrop: (DragData) -> Boolean,
        onDragEntered: () -> Unit,
        onDragExited: () -> Unit
    ): Modifier {
        return Modifier.composed {
            // Generate unique ID for this drop target
            val targetId = remember { UUID.randomUUID().toString() }

            // Clean up when composable is disposed
            DisposableEffect(targetId) {
                onDispose {
                    unregisterDropTarget(targetId)
                }
            }

            Modifier.onGloballyPositioned { coordinates ->
                // Preserve existing isHovered — onGloballyPositioned fires during animations
                // (e.g. drop indicator expanding), which would otherwise reset the hover state
                // mid-drag and cause the cleanup in onDragEnd to skip calling onDragExited.
                registerDropTarget(
                    targetId,
                    DropTargetInfo(
                        id = targetId,
                        bounds = DropTargetBounds(
                            position = coordinates.positionInRoot(),
                            size = coordinates.size
                        ),
                        onDrop = onDrop,
                        onDragEntered = onDragEntered,
                        onDragExited = onDragExited,
                        isHovered = dropTargets[targetId]?.isHovered ?: false
                    )
                )
            }
        }
    }

    @Composable
    actual fun currentDragData(): DragData? = getCurrentDragData()

    @Composable
    actual fun currentDragPosition(): Offset? = if (isDragging()) getCurrentMousePosition() else null

    /**
     * Composable overlay that shows the drag preview following the cursor.
     * This should be placed at the root level of your app (above all other content).
     */
    @Composable
    actual fun DragOverlay() {
        val preview = getDragPreview()
        val mousePos = getCurrentMousePosition()
        var previewSize by remember { mutableStateOf(IntSize.Zero) }
        var overlayRootOffset by remember { mutableStateOf(Offset.Zero) }

        if (preview != null && isDragging()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { overlayRootOffset = it.positionInRoot() }
                    .graphicsLayer {
                        // mousePos is in root coords; subtract this overlay's root offset
                        // so the translation is relative to this Box's own origin
                        translationX = mousePos.x - overlayRootOffset.x - previewSize.width / 2f
                        translationY = mousePos.y - overlayRootOffset.y
                        alpha = 0.95f
                    }
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.onSizeChanged { previewSize = it }
                ) {
                    preview()
                }
            }
        }
    }
}

/**
 * Information about a drop target's position and callbacks.
 */
internal data class DropTargetInfo(
    val id: String,
    var bounds: DropTargetBounds? = null,
    val onDrop: (DragData) -> Boolean,
    val onDragEntered: () -> Unit,
    val onDragExited: () -> Unit,
    var isHovered: Boolean
)

/**
 * Bounds of a drop target in root coordinates.
 */
internal data class DropTargetBounds(
    val position: Offset,
    val size: IntSize
)
