package app.lantr.lantr.Views.DragAndDrop

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Theme.LantrTheme

/**
 * Standalone drop zone — only visible when hovered.
 * Used at the end of the list to allow dropping after all blocks.
 *
 * During a drag, a transparent hit area (24dp) is kept so the cursor
 * can enter it and trigger the hover state.
 */
@Composable
fun DropZone(
    targetIndex: Int,
    isDragging: Boolean,
    isHighlighted: Boolean,
    onDrop: (DragData) -> Boolean,
    onDragEntered: () -> Unit = {},
    onDragExited: () -> Unit = {},
    dragDropManager: DragAndDropManager,
    modifier: Modifier = Modifier
) {
    val height by animateDpAsState(
        targetValue = when {
            isHighlighted -> 60.dp
            isDragging -> 24.dp  // transparent hit area only
            else -> 0.dp
        },
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                color = if (isHighlighted) LantrTheme.extendedColors.goldSemiTransparent else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .then(
                dragDropManager.createDropModifier(
                    onDrop = onDrop,
                    onDragEntered = onDragEntered,
                    onDragExited = onDragExited
                )
            )
    )
}
