package app.lantr.lantr.Views.Presentations.Inspector.Slides

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.StoryBlock
import app.lantr.lantr.Models.StoryBlockCategory
import app.lantr.lantr.Models.models_UI.icon
import app.lantr.lantr.Models.models_UI.tintColor
import app.lantr.lantr.ViewModel.PresentationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.add_box
import app.lantr.lantr.generated.resources.add_circle
import org.jetbrains.compose.resources.painterResource
import java.util.UUID

@Composable
fun PresentationInspectorContentView(
    viewModel: PresentationViewModel,
    modifier: Modifier = Modifier
) {
    var showAddPopover by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val showButtons = this.maxHeight > 200.dp

        // Slides timeline
        SlidesViewTimeline(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom buttons overlay
        if (showButtons) {
            BottomButtonsOverlay(
                showAddPopover = showAddPopover,
                onShowPopoverChange = { showAddPopover = it },
                onAddSlide = {
                    viewModel.addSlideAfterCurrent(usesAltScheme = false)
                    showAddPopover = false
                },
                onAddAltSlide = {
                    viewModel.addSlideAfterCurrent(usesAltScheme = true)
                    showAddPopover = false
                }
            )
        }
    }
}

@Composable
private fun SlidesViewTimeline(
    viewModel: PresentationViewModel,
    modifier: Modifier = Modifier
) {
    val presentation by viewModel.presentation.collectAsState()
    val selectedSlide by viewModel.selectedSlide.collectAsState()

    val lastBlock = presentation.arc.sections.lastOrNull()?.blocks?.lastOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.Delete, Key.Backspace -> {
                            selectedSlide?.let { slide ->
                                viewModel.deleteSlide(slide.id)
                            }
                            true
                        }
                        Key.DirectionUp -> {
                            viewModel.selectPreviousSlide()
                            true
                        }
                        Key.DirectionDown -> {
                            viewModel.selectNextSlide()
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
            .focusable(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        presentation.arc.sections.forEach { storySection ->
            storySection.blocks.forEach { storyBlock ->
                item(key = storyBlock.id) {
                    StoryBlockContainerView(
                        title = storyBlock.name,
                        iconName = storyBlock.icon,
                        tintColor = storyBlock.tintColor,
                        blockId = storyBlock.id,
                        slidesCount = storyBlock.slides.size,
                        hasConnector = lastBlock?.id != storyBlock.id,
                        viewModel = viewModel
                    ) {
                        storyBlock.slides.forEachIndexed { slideIndex, slide ->
                            SlidePreviewView(
                                slide = slide,
                                theme = presentation.theme,
                                isSelected = selectedSlide?.id == slide.id,
                                slideIndex = slideIndex,
                                storyBlock = storyBlock,
                                viewModel = viewModel,
                                onTap = {
                                    // Haptic feedback (Android only)
                                    // Add HapticFeedback here if needed
                                    viewModel.selectSlide(slide.id)
                                },
                                onOverflowChange = { hasOverflow ->
                                    // Only update for selected slide
                                    if (selectedSlide?.id == slide.id) {
                                        viewModel.setSlideOverflow(hasOverflow)
                                    }
                                },
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomButtonsOverlay(
    showAddPopover: Boolean,
    onShowPopoverChange: (Boolean) -> Unit,
    onAddSlide: () -> Unit,
    onAddAltSlide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        CircularMenuButton(
            icon = {
                Icon(
                    painterResource(Res.drawable.add_box),
                    contentDescription = "",
                    tint = Color.White
                )
            },
            isPrimary = true,
            showPopover = showAddPopover,
            onShowPopoverChange = onShowPopoverChange,
            onAddSlide = onAddSlide,
            onAddAltSlide = onAddAltSlide
        )
    }
}

@Composable
private fun CircularMenuButton(
    icon: @Composable () -> Unit,
    isPrimary: Boolean,
    showPopover: Boolean,
    onShowPopoverChange: (Boolean) -> Unit,
    onAddSlide: () -> Unit,
    onAddAltSlide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = { onShowPopoverChange(!showPopover) },
            containerColor = Color(0xffCB9D08)
        ) {
            icon()
        }

        DropdownMenu(
            expanded = showPopover,
            onDismissRequest = { onShowPopoverChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text("Add Alt Slide") },
                leadingIcon = {
                    Icon(Icons.Default.Add, contentDescription = null)
                },
                onClick = onAddAltSlide
            )
            DropdownMenuItem(
                text = { Text("Add Slide") },
                leadingIcon = {
                    Icon(Icons.Default.Add, contentDescription = null)
                },
                onClick = onAddSlide
            )
        }
    }
}

@Composable
private fun StoryBlockContainerView(
    title: String,
    iconName: ImageVector,
    tintColor: Color,
    blockId: UUID,
    slidesCount: Int,
    hasConnector: Boolean,
    viewModel: PresentationViewModel,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isDropTarget by remember { mutableStateOf(false) }
    var isNewSlideTapped by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isNewSlideTapped) 0.95f else 1f,
        label = "scale"
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(tintColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconName,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

                // Content area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Empty state - New Slide button
                    if (slidesCount == 0 && !isDropTarget) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .scale(scale)
                                .clip(RoundedCornerShape(12.dp))
                                .background(tintColor.copy(alpha = 0.2f))
                                .dashedBorder(
                                    color = tintColor,
                                    width = 2.dp,
                                    radius = 12.dp,
                                    dashLength = 5.dp,
                                    gapLength = 3.dp
                                )
                                .clickable {
                                    // viewModel.addSlideToBlock(blockId)

                                    // Trigger tap animation
                                    scope.launch {
                                        isNewSlideTapped = true
                                        delay(100)
                                        isNewSlideTapped = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "New Slide",
                                tint = tintColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Slides content
                    content()

                    // Drop zone
                    if (isDropTarget) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        )
                    }
                }
            }
        }

        // Connector line
        if (hasConnector) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(14.dp)
                    .background(Color.Gray)
            )
        }
    }

    // TODO: Add drop destination support when implementing drag & drop
}

// Helper extension for dashed border (reuse from previous code)
private fun Modifier.dashedBorder(
    color: Color,
    width: Dp = 1.dp,
    radius: Dp = 0.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp
): Modifier = this.then(
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

// Extension properties for StoryBlock (tintColor and icon) are now imported from StoryBlock_UI.kt