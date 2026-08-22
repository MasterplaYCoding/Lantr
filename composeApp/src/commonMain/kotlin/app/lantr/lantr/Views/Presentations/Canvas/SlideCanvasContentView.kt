package app.lantr.lantr.Views.Presentations.Canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.ViewModel.SlideViewModel
import app.lantr.lantr.Views.DragAndDrop.DragData
import app.lantr.lantr.Views.DragAndDrop.DragType
import app.lantr.lantr.Views.DragAndDrop.DropZone
import app.lantr.lantr.Views.DragAndDrop.rememberDragAndDropManager
import app.lantr.lantr.Views.Presentations.Canvas.Base.AutoLayoutCanvasCard
import app.lantr.lantr.Views.Presentations.Canvas.Base.CanvasConfiguration
import app.lantr.lantr.Views.Presentations.Canvas.Base.LocalCanvasLayout
import app.lantr.lantr.Views.Presentations.Canvas.ContentBlocks.ContentBlockView
import java.util.UUID

@Composable
fun SlideCanvasContentView(
    presentationId: String,
    slide: Slide,
    hasOverflow: Boolean,
    onSlideUpdate: (Slide) -> Unit,
    slideViewModel: SlideViewModel,
    modifier: Modifier = Modifier
) {
    val layout = LocalCanvasLayout.current
    val focusedBlockId by slideViewModel.focusedContentBlockId.collectAsState()

    // Drag-and-drop state
    val dragDropManager = rememberDragAndDropManager()
    var draggedBlockId by remember { mutableStateOf<UUID?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
            AutoLayoutCanvasCard(config = CanvasConfiguration.slide) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            // Unfocus when clicking on empty space
                            slideViewModel.unfocusContentBlock()
                        }
            ) {
                // Top margin with overflow indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(layout.margins.calculateTopPadding())
                        .padding(end = 3.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Slide 1",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    if (hasOverflow) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Content overflow",
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Content may overflow",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Red
                            )
                        }
                    }
                }

                // Main content area - Interleave drop zones and blocks as separate items
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // Each block is a "pack": the whole Column is the drop target.
                    // A visual indicator appears at the top of the pack only when hovered.
                    slide.blocks.forEachIndexed { index, block ->
                        item(key = block.id) {
                            var isHovered by remember { mutableStateOf(false) }
                            val indicatorHeight by animateDpAsState(
                                targetValue = if (isHovered) 60.dp else 0.dp,
                                animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        dragDropManager.createDropModifier(
                                            onDrop = { dragData ->
                                                val fromIndex = dragData.sourceIndex
                                                if (fromIndex != index) {
                                                    val blockId = UUID.fromString(dragData.id)
                                                    val adjustedIndex = if (fromIndex < index) index - 1 else index
                                                    val updatedSlide = slideViewModel.reorderContentBlock(
                                                        slide = slide,
                                                        blockId = blockId,
                                                        newIndex = adjustedIndex
                                                    )
                                                    onSlideUpdate(updatedSlide)
                                                }
                                                draggedBlockId = null
                                                isHovered = false
                                                true
                                            },
                                            onDragEntered = { isHovered = true },
                                            onDragExited = { isHovered = false }
                                        )
                                    )
                            ) {
                                // Drop indicator — only visible when this pack is hovered
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(indicatorHeight)
                                        .background(
                                            color = LantrTheme.extendedColors.goldSemiTransparent,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )

                                // Draggable content block
                                Box(
                                    modifier = dragDropManager.createDragModifier(
                                        data = DragData(
                                            type = DragType.CONTENT_BLOCK,
                                            id = block.id.toString(),
                                            sourceIndex = index,
                                            payload = ""
                                        ),
                                        onDragStart = { draggedBlockId = block.id },
                                        onDragEnd = { draggedBlockId = null },
                                        dragPreview = {
                                            Box(
                                                modifier = Modifier
                                                    .width(600.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color(0xffCB9D08),
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .shadow(elevation = 4.dp)
                                                    .padding(8.dp)
                                            ) {
                                                ContentBlockView(
                                                    presentationId = presentationId,
                                                    contentBlock = block,
                                                    slide = slide,
                                                    layout = layout,
                                                    isFocused = false,
                                                    currentBlockIndex = index,
                                                    onSlideUpdate = {},
                                                    onBlockFocus = {},
                                                    slideViewModel = slideViewModel,
                                                    enableClickable = false
                                                )
                                            }
                                        }
                                    )
                                        .graphicsLayer {
                                            alpha = if (draggedBlockId == block.id) 0.3f else 1f
                                            scaleX = if (draggedBlockId == block.id) 0.95f else 1f
                                            scaleY = if (draggedBlockId == block.id) 0.95f else 1f
                                        }
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            slideViewModel.focusContentBlock(block.id)
                                        }
                                ) {
                                    ContentBlockView(
                                        presentationId = presentationId,
                                        contentBlock = block,
                                        slide = slide,
                                        layout = layout,
                                        isFocused = focusedBlockId == block.id,
                                        currentBlockIndex = index,
                                        onSlideUpdate = onSlideUpdate,
                                        onBlockFocus = { slideViewModel.focusContentBlock(it) },
                                        slideViewModel = slideViewModel,
                                        enableClickable = false
                                    )
                                }
                            }
                        }
                    }

                    // Final drop zone — hover to drop after all blocks
                    item(key = "dropzone_final") {
                        var isFinalHovered by remember { mutableStateOf(false) }
                        DropZone(
                            targetIndex = slide.blocks.size,
                            isDragging = draggedBlockId != null,
                            isHighlighted = isFinalHovered,
                            dragDropManager = dragDropManager,
                            onDrop = { dragData ->
                                val fromIndex = dragData.sourceIndex
                                if (fromIndex != slide.blocks.size - 1) {
                                    val blockId = UUID.fromString(dragData.id)
                                    val updatedSlide = slideViewModel.reorderContentBlock(
                                        slide = slide,
                                        blockId = blockId,
                                        newIndex = slide.blocks.size - 1
                                    )
                                    onSlideUpdate(updatedSlide)
                                }
                                draggedBlockId = null
                                isFinalHovered = false
                                true
                            },
                            onDragEntered = { isFinalHovered = true },
                            onDragExited = { isFinalHovered = false }
                        )
                    }
                }

                // Bottom margin
                Spacer(modifier = Modifier.height(layout.margins.calculateBottomPadding()))
            }
        }

        // Drag overlay - renders the preview that follows the cursor
        dragDropManager.DragOverlay()
    }
}
