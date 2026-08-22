package app.lantr.lantr.Views.Presentations.Canvas.ContentBlocks.Images

import androidx.compose.foundation.background
import app.lantr.lantr.Models.ImageBlock
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.ViewModel.SlideViewModel
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun ImageContentBlockHandle(
    imageContentBlock: ImageBlock,
    slide: Slide,
    viewModel: SlideViewModel,
    onSlideUpdate: (Slide) -> Unit,
    modifier: Modifier = Modifier
) {
    var totalDragOffset by remember(imageContentBlock.id) { mutableFloatStateOf(0f) }
    var initialHeight by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .size(width = 40.dp, height = 26.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        initialHeight = imageContentBlock.imageHeight
                        totalDragOffset = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragOffset += dragAmount.y
                        handleDragChanged(
                            dragOffset = totalDragOffset,
                            initialHeight = initialHeight,
                            imageContentBlock = imageContentBlock,
                            slide = slide,
                            viewModel = viewModel,
                            onSlideUpdate = onSlideUpdate
                        )
                    },
                    onDragEnd = {
                        totalDragOffset = 0f
                        initialHeight = 0f
                    },
                    onDragCancel = {
                        totalDragOffset = 0f
                        initialHeight = 0f
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.UnfoldMore,
            contentDescription = "Resize image",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}

private fun handleDragChanged(
    dragOffset: Float,
    initialHeight: Float,
    imageContentBlock: ImageBlock,
    slide: Slide,
    viewModel: SlideViewModel,
    onSlideUpdate: (Slide) -> Unit
) {
    // Convert drag to base 1920x1080 scale for consistent storage
    // Calculate scale factor based on available canvas size vs 1920 baseline
    val canvasLayout = viewModel.canvasLayout.value
    val canvasLongEdge = max(
        canvasLayout?.availableSize?.width ?: 800f,
        canvasLayout?.availableSize?.height ?: 600f
    )
    val currentScale = canvasLongEdge / 1920f  // Simple scaling based on canvas size
    val baseHeightChange = dragOffset / max(0.4f, currentScale)  // Prevent extreme scaling

    val newHeight = max(50f, initialHeight + baseHeightChange)  // Min height of 50

    onSlideUpdate(
        viewModel.updateImageBlockHeight(
            slide = slide,
            blockId = imageContentBlock.id,
            height = newHeight
        )
    )
}
