package app.lantr.lantr.Views.Presentations.Inspector.Arc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.PredefinedBlockType
import app.lantr.lantr.Models.Rhetoric
import app.lantr.lantr.Models.RhetoricValue
import app.lantr.lantr.Models.StoryBlock
import app.lantr.lantr.Models.StoryBlockCategory
import app.lantr.lantr.Models.getIcon
import app.lantr.lantr.Models.models_UI.tintColor
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.Views.DragAndDrop.DragData
import app.lantr.lantr.Views.DragAndDrop.DragType
import app.lantr.lantr.Views.DragAndDrop.rememberDragAndDropManager
import app.lantr.lantr.Views.Presentations.Canvas.arcTemplatePayload

@Composable
fun StoryBlocksCategoryView(
    selectedCategory: StoryBlockCategory?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dragDropManager = rememberDragAndDropManager()

    if (selectedCategory == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("What are you doing here??")
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Transparent) // DEBUG: Should see light red
                .padding(
                    top = if (PlatformUtil.isDesktop) 8.dp else 20.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onBack)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = selectedCategory.tintColor,
                    modifier = Modifier.size(18.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = selectedCategory.displayTitle,
                        fontSize = if (PlatformUtil.isDesktop) 18.sp else 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = selectedCategory.displaySubtitle,
                        fontSize = if (PlatformUtil.isDesktop) 14.sp else 16.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            // Story blocks list
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    top = 10.dp,    // Space before first item
                    bottom = 10.dp, // Space after last item (scroll down to see)
                    start = 10.dp,   // Space on left
                    end = 10.dp      // Space on right
                )
            ) {
                items(
                    items = PredefinedBlockType.values()
                        .filter { it.category == selectedCategory },
                    key = { it.name }
                ) { predefinedBlockType ->
                    val templateStoryBlock = StoryBlock.predefined(predefinedBlockType)

                    StoryBlockCardView(
                        storyBlock = templateStoryBlock,
                        dragDropManager = dragDropManager,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryBlockCardView(
    storyBlock: StoryBlock,
    dragDropManager: app.lantr.lantr.Views.DragAndDrop.DragAndDropManager,
    modifier: Modifier = Modifier
) {
    val predefinedType = storyBlock.predefinedType ?: return
    var isDragging by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .then(
                dragDropManager.createDragModifier(
                    data = DragData(
                        type = DragType.STORY_BLOCK,
                        id = predefinedType.name,
                        sourceIndex = 0,
                        payload = arcTemplatePayload(predefinedType)
                    ),
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    dragPreview = {
                        StoryBlockCardViewPreview(storyBlock = storyBlock)
                    }
                )
            )
            .graphicsLayer {
                alpha = if (isDragging) 0.5f else 1f
                scaleX = if (isDragging) 0.98f else 1f
                scaleY = if (isDragging) 0.98f else 1f
            }
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(storyBlock.tintColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = storyBlock.blockType.getIcon(),
                        contentDescription = storyBlock.name,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Title and rhetoric
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = storyBlock.name,
                        fontSize = if (PlatformUtil.isDesktop) 16.sp else 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    RhetoricLabelView(rhetoric = storyBlock.rhetoric)
                }

                // Favorite button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(storyBlock.tintColor.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = storyBlock.tintColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider()

            // Description
            Text(
                text = storyBlock.description,
                fontSize = if (PlatformUtil.isDesktop) 13.sp else 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = if (PlatformUtil.isDesktop) 16.sp else 18.sp
            )
        }
    }
}

@Composable
private fun StoryBlockCardViewPreview(
    storyBlock: StoryBlock
) {
    Surface(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = storyBlock.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = storyBlock.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RhetoricLabelView(
    rhetoric: Rhetoric,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // High values first
        if (rhetoric.ethos == RhetoricValue.HIGH) {
            RhetoricLabel("ethos", Icons.Default.ArrowUpward)
        }
        if (rhetoric.logos == RhetoricValue.HIGH) {
            RhetoricLabel("logos", Icons.Default.ArrowUpward)
        }
        if (rhetoric.pathos == RhetoricValue.HIGH) {
            RhetoricLabel("pathos", Icons.Default.ArrowUpward)
        }

        // Middle values second
        if (rhetoric.ethos == RhetoricValue.MIDDLE) {
            RhetoricLabel("ethos", Icons.Default.ArrowForward)
        }
        if (rhetoric.logos == RhetoricValue.MIDDLE) {
            RhetoricLabel("logos", Icons.Default.ArrowForward)
        }
        if (rhetoric.pathos == RhetoricValue.MIDDLE) {
            RhetoricLabel("pathos", Icons.Default.ArrowForward)
        }
    }
}

@Composable
private fun RhetoricLabel(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = text,
            fontSize = if (PlatformUtil.isDesktop) 14.sp else 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}

// Icon extension for StoryBlock (category-based fallback)
val StoryBlock.icon: androidx.compose.ui.graphics.vector.ImageVector
    get() = when (category) {
        StoryBlockCategory.HOOK -> Icons.Default.PlayArrow
        StoryBlockCategory.STAKES -> Icons.Default.Warning
        StoryBlockCategory.CORE -> Icons.Default.Star
        StoryBlockCategory.TURN -> Icons.Default.Refresh
        StoryBlockCategory.AUDIENCE -> Icons.Default.Person
        StoryBlockCategory.CLOSE -> Icons.Default.Check
        StoryBlockCategory.OTHER -> Icons.Default.Info
    }

// Colors
val CurtainRed = Color(0xFFDC143C)
val WallPurple = Color(0xFF9370DB)
