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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import java.util.UUID

actual class DragAndDropManager actual constructor() {

    companion object {
        private var currentDragData: DragData? by mutableStateOf(null)
        private var currentTouchPosition by mutableStateOf(Offset.Zero)
        private var currentDragPreview by mutableStateOf<(@Composable () -> Unit)?>(null)
        private var draggedElementPosition by mutableStateOf(Offset.Zero)
        private var grabOffset by mutableStateOf(Offset.Zero)
        private val dropTargets = mutableStateMapOf<String, DropTargetInfo>()

        internal fun isDragging() = currentDragData != null

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
                    rect.contains(currentTouchPosition)
                } ?: false
            }
        }
    }

    actual fun createDragModifier(
        data: DragData,
        onDragStart: () -> Unit,
        onDragEnd: () -> Unit,
        dragPreview: (@Composable () -> Unit)?
    ): Modifier = Modifier.composed {
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
                        currentTouchPosition = pos + offset
                        onDragStart()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentTouchPosition += dragAmount

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
                        // currentTouchPosition may be slightly stale at release time.
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
                        currentTouchPosition = Offset.Zero
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
                        currentTouchPosition = Offset.Zero
                        draggedElementPosition = Offset.Zero
                        grabOffset = Offset.Zero
                        onDragEnd()
                    }
                )
            }
    }

    actual fun createDropModifier(
        onDrop: (DragData) -> Boolean,
        onDragEntered: () -> Unit,
        onDragExited: () -> Unit
    ): Modifier {
        return Modifier.composed {
            val targetId = remember { UUID.randomUUID().toString() }

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
    actual fun currentDragData(): DragData? = currentDragData

    @Composable
    actual fun currentDragPosition(): Offset? = if (isDragging()) currentTouchPosition else null

    @Composable
    actual fun DragOverlay() {
        val preview = currentDragPreview
        val touchPos = currentTouchPosition
        var previewSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
        var overlayRootOffset by remember { mutableStateOf(Offset.Zero) }

        if (preview != null && isDragging()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { overlayRootOffset = it.positionInRoot() }
                    .graphicsLayer {
                        translationX = touchPos.x - overlayRootOffset.x - previewSize.width / 2f
                        translationY = touchPos.y - overlayRootOffset.y
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

internal data class DropTargetInfo(
    val id: String,
    var bounds: DropTargetBounds? = null,
    val onDrop: (DragData) -> Boolean,
    val onDragEntered: () -> Unit,
    val onDragExited: () -> Unit,
    var isHovered: Boolean
)

internal data class DropTargetBounds(
    val position: Offset,
    val size: IntSize
)
