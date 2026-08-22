package app.lantr.lantr.Views.Presentations.Canvas

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.Arc
import app.lantr.lantr.Models.PredefinedBlockType
import app.lantr.lantr.Models.StoryBlock
import app.lantr.lantr.Models.StorySection
import app.lantr.lantr.Models.StorySectionType
import app.lantr.lantr.Models.displayName
import app.lantr.lantr.Models.getIcon
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.ViewModel.ArcViewModel
import app.lantr.lantr.Views.CreationFlow.components.TextBoxForStoryBlock
import app.lantr.lantr.Views.DragAndDrop.DragData
import app.lantr.lantr.Views.DragAndDrop.DragType
import app.lantr.lantr.Views.DragAndDrop.DropZone
import app.lantr.lantr.Views.DragAndDrop.DragAndDropManager
import app.lantr.lantr.Views.DragAndDrop.rememberDragAndDropManager
import app.lantr.lantr.Views.Presentations.Canvas.Base.AutoLayoutCanvasCard
import app.lantr.lantr.Views.Presentations.Canvas.Base.CanvasConfiguration
import app.lantr.lantr.Views.Presentations.Canvas.Base.LocalCanvasLayout
import app.lantr.lantr.Views.Presentations.Canvas.Base.vertical
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.cancel_filled
import org.jetbrains.compose.resources.painterResource
import java.util.UUID
import kotlinx.coroutines.delay

private const val TemplatePayloadPrefix = "template:"
private const val ExistingBlockPayloadPrefix = "existing:"
private const val SectionTemplatePayloadPrefix = "section-template:"

@Composable
fun ArcCanvasContentView(
    arc: Arc,
    onArcChange: (Arc) -> Unit = {},
    arcViewModel: ArcViewModel,
    modifier: Modifier = Modifier
) {
    val config = CanvasConfiguration.arc
    val dragDropManager = rememberDragAndDropManager()
    val currentDragData = dragDropManager.currentDragData()
    val currentDragPosition = dragDropManager.currentDragPosition()

    var draggedSectionId by remember { mutableStateOf<UUID?>(null) }
    var draggedBlockId by remember { mutableStateOf<UUID?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        AutoLayoutCanvasCard(config = config) {
            ArcContentArea(
                arc = arc,
                onArcChange = onArcChange,
                arcViewModel = arcViewModel,
                isPhone = PlatformUtil.isPhone,
                config = config,
                dragDropManager = dragDropManager,
                currentDragData = currentDragData,
                currentDragPosition = currentDragPosition,
                draggedSectionId = draggedSectionId,
                draggedBlockId = draggedBlockId,
                onSectionDragStart = { draggedSectionId = it },
                onSectionDragEnd = { draggedSectionId = null },
                onBlockDragStart = { draggedBlockId = it },
                onBlockDragEnd = { draggedBlockId = null }
            )
        }

        dragDropManager.DragOverlay()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ArcContentArea(
    arc: Arc,
    onArcChange: (Arc) -> Unit,
    arcViewModel: ArcViewModel,
    isPhone: Boolean,
    config: CanvasConfiguration,
    dragDropManager: DragAndDropManager,
    currentDragData: DragData?,
    currentDragPosition: Offset?,
    draggedSectionId: UUID?,
    draggedBlockId: UUID?,
    onSectionDragStart: (UUID) -> Unit,
    onSectionDragEnd: () -> Unit,
    onBlockDragStart: (UUID) -> Unit,
    onBlockDragEnd: () -> Unit
) {
    val layout = LocalCanvasLayout.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(layout.margins.vertical),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Narrative Arc",
                color = Color.Gray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(end = config.contentHorizontalPadding)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(layout.availableSize.height.dp)
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = arc.sections.isEmpty()
            ) { isEmpty ->
                if (isEmpty) {
                    ArcContentPlaceholder(isPhone = isPhone)
                } else {
                    ArcSectionsList(
                        arc = arc,
                        onArcChange = onArcChange,
                        arcViewModel = arcViewModel,
                        isPhone = isPhone,
                        dragDropManager = dragDropManager,
                        currentDragData = currentDragData,
                        currentDragPosition = currentDragPosition,
                        draggedSectionId = draggedSectionId,
                        draggedBlockId = draggedBlockId,
                        onSectionDragStart = onSectionDragStart,
                        onSectionDragEnd = onSectionDragEnd,
                        onBlockDragStart = onBlockDragStart,
                        onBlockDragEnd = onBlockDragEnd
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(layout.margins.vertical))
    }
}

@Composable
fun ArcSectionsList(
    arc: Arc,
    onArcChange: (Arc) -> Unit,
    arcViewModel: ArcViewModel,
    isPhone: Boolean,
    dragDropManager: DragAndDropManager,
    currentDragData: DragData?,
    currentDragPosition: Offset?,
    draggedSectionId: UUID?,
    draggedBlockId: UUID?,
    onSectionDragStart: (UUID) -> Unit,
    onSectionDragEnd: () -> Unit,
    onBlockDragStart: (UUID) -> Unit,
    onBlockDragEnd: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isDraggingSection = currentDragData?.type == DragType.STORY_SECTION
    var viewportPosition by remember { mutableStateOf(Offset.Zero) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    val latestDragType by rememberUpdatedState(currentDragData?.type)
    val latestDragPosition by rememberUpdatedState(currentDragPosition)

    BoxWithConstraints {
        val width = maxWidth
        val arcPadding = if (isPhone) 10.dp else width / 4

        LaunchedEffect(scrollState) {
            while (true) {
                if (latestDragType == DragType.STORY_SECTION || latestDragType == DragType.STORY_BLOCK) {
                    val pointer = latestDragPosition
                    if (pointer != null && viewportSize.height > 0) {
                        val viewportTop = viewportPosition.y
                        val viewportBottom = viewportPosition.y + viewportSize.height
                        val threshold = 72f
                        val step = 12f

                        when {
                            pointer.y > viewportBottom - threshold && scrollState.value < scrollState.maxValue -> {
                                scrollState.scrollBy(step)
                            }
                            pointer.y < viewportTop + threshold && scrollState.value > 0 -> {
                                scrollState.scrollBy(-step)
                            }
                        }
                    }
                }
                delay(32)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    viewportPosition = it.positionInRoot()
                    viewportSize = it.size
                }
                .verticalScroll(scrollState)
                .padding(top = 16.dp, start = arcPadding, end = arcPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionReorderDropZone(
                targetIndex = 0,
                isDragging = isDraggingSection,
                dragDropManager = dragDropManager,
                onDrop = { dragData ->
                    handleSectionDrop(
                        dragData = dragData,
                        targetIndex = 0,
                        arcViewModel = arcViewModel,
                        onSectionDragEnd = onSectionDragEnd
                    )
                }
            )

            arc.sections.forEachIndexed { index, section ->
                StorySectionTimelineItem(
                    section = section,
                    sectionIndex = index,
                    isLastBox = index == arc.sections.lastIndex,
                    arc = arc,
                    onArcChange = onArcChange,
                    arcViewModel = arcViewModel,
                    dragDropManager = dragDropManager,
                    currentDragData = currentDragData,
                    draggedSectionId = draggedSectionId,
                    draggedBlockId = draggedBlockId,
                    onSectionDragStart = onSectionDragStart,
                    onSectionDragEnd = onSectionDragEnd,
                    onBlockDragStart = onBlockDragStart,
                    onBlockDragEnd = onBlockDragEnd
                )

                if (index < arc.sections.lastIndex) {
                    SectionConnector()
                    SectionReorderDropZone(
                        targetIndex = index + 1,
                        isDragging = isDraggingSection,
                        dragDropManager = dragDropManager,
                        onDrop = { dragData ->
                            handleSectionDrop(
                                dragData = dragData,
                                targetIndex = index + 1,
                                arcViewModel = arcViewModel,
                                onSectionDragEnd = onSectionDragEnd
                            )
                        }
                    )
                }
            }

            SectionReorderDropZone(
                targetIndex = arc.sections.size,
                isDragging = isDraggingSection,
                dragDropManager = dragDropManager,
                onDrop = { dragData ->
                    handleSectionDrop(
                        dragData = dragData,
                        targetIndex = arc.sections.size,
                        arcViewModel = arcViewModel,
                        onSectionDragEnd = onSectionDragEnd
                    )
                }
            )

            Spacer(modifier = Modifier.height(if (isPhone) 100.dp else 16.dp))
        }
    }
}

@Composable
private fun SectionReorderDropZone(
    targetIndex: Int,
    isDragging: Boolean,
    dragDropManager: DragAndDropManager,
    onDrop: (DragData) -> Boolean
) {
    var isHovered by remember { mutableStateOf(false) }

    DropZone(
        targetIndex = targetIndex,
        isDragging = isDragging,
        isHighlighted = isHovered,
        dragDropManager = dragDropManager,
        onDrop = {
            val handled = onDrop(it)
            isHovered = false
            handled
        },
        onDragEntered = { isHovered = true },
        onDragExited = { isHovered = false }
    )
}

@Composable
private fun SectionConnector() {
    Box(
        modifier = Modifier
            .width(2.dp)
            .height(24.dp)
            .background(Color.Gray.copy(alpha = 0.45f))
    )
}

@Composable
fun ArcContentPlaceholder(isPhone: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "The stage is yours.",
            fontSize = if (isPhone) 24.sp else 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Let's make something worth remembering.",
            fontSize = if (isPhone) 18.sp else 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        ArcPlaceholderActionLine(
            title = "Suggest an Arc",
            subtitle = "see a structure form in seconds.",
            tintColor = MaterialTheme.colorScheme.primary,
            isPhone = isPhone
        )

        ArcPlaceholderActionLine(
            title = "Brain Dump",
            subtitle = "throw your thoughts here, I'll catch them.",
            tintColor = Color(0xFFE57373),
            isPhone = isPhone
        )

        ArcPlaceholderActionLine(
            title = "Build Freely",
            subtitle = "drag blocks and sculpt your own flow.",
            tintColor = Color(0xFF9C27B0),
            isPhone = isPhone
        )
    }
}

@Composable
fun ArcPlaceholderActionLine(
    title: String,
    subtitle: String,
    tintColor: Color,
    isPhone: Boolean
) {
    if (isPhone) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = "o $title",
                fontWeight = FontWeight.SemiBold,
                color = tintColor,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            Text(
                text = "o $title",
                fontWeight = FontWeight.SemiBold,
                color = tintColor,
                fontSize = 15.sp
            )
            Text(
                text = " - $subtitle",
                color = Color.Gray,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun StorySectionTimelineItem(
    section: StorySection,
    sectionIndex: Int,
    isLastBox: Boolean,
    arc: Arc,
    onArcChange: (Arc) -> Unit,
    arcViewModel: ArcViewModel,
    dragDropManager: DragAndDropManager,
    currentDragData: DragData?,
    draggedSectionId: UUID?,
    draggedBlockId: UUID?,
    onSectionDragStart: (UUID) -> Unit,
    onSectionDragEnd: () -> Unit,
    onBlockDragStart: (UUID) -> Unit,
    onBlockDragEnd: () -> Unit
) {
    val sectionDragModifier = dragDropManager.createDragModifier(
        data = DragData(
            type = DragType.STORY_SECTION,
            id = section.id.toString(),
            sourceIndex = sectionIndex,
            payload = ""
        ),
        onDragStart = { onSectionDragStart(section.id) },
        onDragEnd = onSectionDragEnd,
        dragPreview = {
            StorySectionPreview(section = section)
        }
    )

    Box(
        modifier = Modifier.graphicsLayer {
            alpha = if (draggedSectionId == section.id) 0.45f else 1f
            scaleX = if (draggedSectionId == section.id) 0.98f else 1f
            scaleY = if (draggedSectionId == section.id) 0.98f else 1f
        }
    ) {
        StorySectionBoxView(
            section = section,
            sectionIndex = sectionIndex,
            isLastBox = isLastBox,
            arc = arc,
            onArcChange = onArcChange,
            arcViewModel = arcViewModel,
            sectionDragModifier = sectionDragModifier,
            dragDropManager = dragDropManager,
            currentDragData = currentDragData,
            draggedBlockId = draggedBlockId,
            onBlockDragStart = onBlockDragStart,
            onBlockDragEnd = onBlockDragEnd
        )
    }
}

@Composable
private fun StorySectionPreview(
    section: StorySection
) {
    Box(
        modifier = Modifier
            .width(340.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(section.type.storySectionColor.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = section.type.storySectionColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = section.type.rawValue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = section.type.storySectionColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (section.blocks.isEmpty()) {
                    "Empty section"
                } else {
                    "${section.blocks.size} block${if (section.blocks.size == 1) "" else "s"}"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StorySectionTemplateCard(
    sectionType: StorySectionType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(sectionType.storySectionColor.copy(alpha = 0.14f))
            .dashedBorder(
                color = sectionType.storySectionColor,
                width = 2.dp,
                radius = 20.dp,
                dashLength = 8.dp,
                gapLength = 8.dp
            )
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        Column {
            Text(
                text = sectionType.rawValue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = sectionType.storySectionColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Drag into the arc or click to add",
                color = sectionType.storySectionColor.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StorySectionBoxView(
    section: StorySection,
    sectionIndex: Int,
    isLastBox: Boolean,
    arc: Arc,
    onArcChange: (Arc) -> Unit,
    arcViewModel: ArcViewModel,
    sectionDragModifier: Modifier = Modifier,
    dragDropManager: DragAndDropManager,
    currentDragData: DragData?,
    draggedBlockId: UUID?,
    onBlockDragStart: (UUID) -> Unit,
    onBlockDragEnd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(section.type.storySectionColor.copy(alpha = 0.2f))
            .dashedBorder(
                color = section.type.storySectionColor,
                width = 2.8.dp,
                radius = 20.dp,
                dashLength = 8.dp,
                gapLength = 8.dp
            )
            .padding(horizontal = 10.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = sectionDragModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = section.type.rawValue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                    color = section.type.storySectionColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (section.blocks.isEmpty()) {
                EmptyStoryBlockDropTarget(
                    section = section,
                    currentDragData = currentDragData,
                    dragDropManager = dragDropManager,
                    arcViewModel = arcViewModel
                )
            } else {
                StoryBlocksColumn(
                    section = section,
                    currentDragData = currentDragData,
                    dragDropManager = dragDropManager,
                    arcViewModel = arcViewModel,
                    draggedBlockId = draggedBlockId,
                    onBlockDragStart = onBlockDragStart,
                    onBlockDragEnd = onBlockDragEnd
                )
            }
        }

        if (section.blocks.isEmpty()) {
            IconButton(
                onClick = { arcViewModel.removeStorySection(section.id) }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.cancel_filled),
                    contentDescription = "Delete empty section",
                    tint = section.type.storySectionColor
                )
            }
        }
    }
}

@Composable
private fun EmptyStoryBlockDropTarget(
    section: StorySection,
    currentDragData: DragData?,
    dragDropManager: DragAndDropManager,
    arcViewModel: ArcViewModel
) {
    var isHovered by remember { mutableStateOf(false) }
    val acceptsStoryBlock = currentDragData?.type == DragType.STORY_BLOCK

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isHovered) LantrTheme.extendedColors.goldSemiTransparent else Color.Transparent
            )
            .then(
                dragDropManager.createDropModifier(
                    onDrop = { dragData ->
                        val handled = handleStoryBlockDrop(
                            dragData = dragData,
                            targetSectionId = section.id,
                            targetIndex = 0,
                            arcViewModel = arcViewModel
                        )
                        isHovered = false
                        handled
                    },
                    onDragEntered = { if (acceptsStoryBlock) isHovered = true },
                    onDragExited = { isHovered = false }
                )
            )
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Text(
            text = if (acceptsStoryBlock) "Drop a story block here" else "Drag a block or tap for suggestion",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = section.type.storySectionColor
        )
    }
}

@Composable
private fun StoryBlocksColumn(
    section: StorySection,
    currentDragData: DragData?,
    dragDropManager: DragAndDropManager,
    arcViewModel: ArcViewModel,
    draggedBlockId: UUID?,
    onBlockDragStart: (UUID) -> Unit,
    onBlockDragEnd: () -> Unit
) {
    val isDraggingStoryBlock = currentDragData?.type == DragType.STORY_BLOCK

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StoryBlockDropZone(
            targetSectionId = section.id,
            targetIndex = 0,
            isDragging = isDraggingStoryBlock,
            dragDropManager = dragDropManager,
            arcViewModel = arcViewModel
        )

        section.blocks.forEachIndexed { blockIndex, block ->
            Box(
                modifier = dragDropManager.createDragModifier(
                    data = DragData(
                        type = DragType.STORY_BLOCK,
                        id = block.id.toString(),
                        sourceIndex = blockIndex,
                        payload = existingStoryBlockPayload(section.id)
                    ),
                    onDragStart = { onBlockDragStart(block.id) },
                    onDragEnd = onBlockDragEnd,
                    dragPreview = {
                        StoryBlockView(
                            block = block,
                            onDelete = {},
                            modifier = Modifier
                                .width(320.dp)
                                .border(
                                    width = 1.dp,
                                    color = section.type.storySectionColor.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        )
                    }
                ).graphicsLayer {
                    alpha = if (draggedBlockId == block.id) 0.35f else 1f
                    scaleX = if (draggedBlockId == block.id) 0.98f else 1f
                    scaleY = if (draggedBlockId == block.id) 0.98f else 1f
                }
            ) {
                StoryBlockView(
                    block = block,
                    onDelete = {
                        arcViewModel.removeStoryBlock(
                            sectionId = section.id,
                            blockId = block.id
                        )
                    }
                )
            }

            StoryBlockDropZone(
                targetSectionId = section.id,
                targetIndex = blockIndex + 1,
                isDragging = isDraggingStoryBlock,
                dragDropManager = dragDropManager,
                arcViewModel = arcViewModel
            )
        }
    }
}

@Composable
private fun StoryBlockDropZone(
    targetSectionId: UUID,
    targetIndex: Int,
    isDragging: Boolean,
    dragDropManager: DragAndDropManager,
    arcViewModel: ArcViewModel
) {
    var isHovered by remember { mutableStateOf(false) }

    DropZone(
        targetIndex = targetIndex,
        isDragging = isDragging,
        isHighlighted = isHovered,
        dragDropManager = dragDropManager,
        onDrop = { dragData ->
            val handled = handleStoryBlockDrop(
                dragData = dragData,
                targetSectionId = targetSectionId,
                targetIndex = targetIndex,
                arcViewModel = arcViewModel
            )
            isHovered = false
            handled
        },
        onDragEntered = { isHovered = true },
        onDragExited = { isHovered = false }
    )
}

private fun handleSectionDrop(
    dragData: DragData,
    targetIndex: Int,
    arcViewModel: ArcViewModel,
    onSectionDragEnd: () -> Unit
): Boolean {
    if (dragData.type != DragType.STORY_SECTION) {
        return false
    }

    dragData.predefinedSectionTypeOrNull()?.let { sectionType ->
        arcViewModel.insertStorySection(
            sectionType = sectionType,
            targetIndex = targetIndex
        )
        onSectionDragEnd()
        return true
    }

    arcViewModel.reorderSection(
        sourceIndex = dragData.sourceIndex,
        targetIndex = targetIndex
    )
    onSectionDragEnd()
    return true
}

private fun handleStoryBlockDrop(
    dragData: DragData,
    targetSectionId: UUID,
    targetIndex: Int,
    arcViewModel: ArcViewModel
): Boolean {
    if (dragData.type != DragType.STORY_BLOCK) {
        return false
    }

    dragData.predefinedBlockTypeOrNull()?.let { predefinedType ->
        arcViewModel.insertStoryBlock(
            sectionId = targetSectionId,
            blockType = predefinedType,
            targetIndex = targetIndex
        )
        return true
    }

    val sourceSectionId = dragData.sourceSectionIdOrNull() ?: return false
    val sourceBlockId = runCatching { UUID.fromString(dragData.id) }.getOrNull() ?: return false

    arcViewModel.moveStoryBlock(
        sourceSectionId = sourceSectionId,
        sourceBlockId = sourceBlockId,
        sourceIndex = dragData.sourceIndex,
        targetSectionId = targetSectionId,
        targetIndex = targetIndex
    )
    return true
}

private fun existingStoryBlockPayload(sectionId: UUID): String =
    "$ExistingBlockPayloadPrefix$sectionId"

private fun templateStoryBlockPayload(predefinedBlockType: PredefinedBlockType): String =
    "$TemplatePayloadPrefix${predefinedBlockType.name}"

fun sectionTemplatePayload(sectionType: StorySectionType): String =
    "$SectionTemplatePayloadPrefix${sectionType.name}"

private fun DragData.predefinedBlockTypeOrNull(): PredefinedBlockType? {
    if (!payload.startsWith(TemplatePayloadPrefix)) {
        return null
    }

    return runCatching {
        PredefinedBlockType.valueOf(payload.removePrefix(TemplatePayloadPrefix))
    }.getOrNull()
}

private fun DragData.sourceSectionIdOrNull(): UUID? {
    if (!payload.startsWith(ExistingBlockPayloadPrefix)) {
        return null
    }

    return runCatching {
        UUID.fromString(payload.removePrefix(ExistingBlockPayloadPrefix))
    }.getOrNull()
}

private fun DragData.predefinedSectionTypeOrNull(): StorySectionType? {
    if (!payload.startsWith(SectionTemplatePayloadPrefix)) {
        return null
    }

    return runCatching {
        StorySectionType.valueOf(payload.removePrefix(SectionTemplatePayloadPrefix))
    }.getOrNull()
}

@Composable
fun StoryBlockView(
    block: StoryBlock,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 5.dp, bottom = 5.dp, start = 5.dp, end = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = block.predefinedType?.iconPainter()
                            ?: painterResource(Res.drawable.cancel_filled),
                        contentDescription = null,
                        tint = Color.Red
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = block.blockType.displayName(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(Res.drawable.cancel_filled),
                    contentDescription = "Delete story block",
                    tint = Color(0xffCBCBCB)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextBoxForStoryBlock(text2 = "Brrrr write something...", onChange = {})
    }
}

private fun Modifier.dashedBorder(
    color: Color,
    width: Dp = 1.dp,
    radius: Dp = 0.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp
) = this.then(
    Modifier.drawWithContent {
        drawContent()

        val pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            phase = 0f
        )

        drawRoundRect(
            color = color,
            style = Stroke(
                width = width.toPx(),
                pathEffect = pathEffect
            ),
            cornerRadius = CornerRadius(radius.toPx())
        )
    }
)

fun arcTemplatePayload(predefinedBlockType: PredefinedBlockType): String =
    templateStoryBlockPayload(predefinedBlockType)
