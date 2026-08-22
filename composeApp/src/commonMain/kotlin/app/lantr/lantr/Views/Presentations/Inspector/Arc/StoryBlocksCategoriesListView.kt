package app.lantr.lantr.Views.Presentations.Inspector.Arc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.PredefinedBlockType
import app.lantr.lantr.Models.StoryBlock
import app.lantr.lantr.Models.StoryBlockCategory
import app.lantr.lantr.Models.getIcon
import app.lantr.lantr.Models.models_UI.tintColor
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.Views.DragAndDrop.DragData
import app.lantr.lantr.Views.DragAndDrop.DragType
import app.lantr.lantr.Views.DragAndDrop.rememberDragAndDropManager
import app.lantr.lantr.Views.Presentations.Canvas.arcTemplatePayload

@Composable
fun StoryBlocksInspectorListView(
    selectedCategory: StoryBlockCategory?,
    onCategorySelected: (StoryBlockCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val dragDropManager = rememberDragAndDropManager()
    var searchText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            SingleLineTextField(
                text = searchText,
                onTextChange = { searchText = it },
                placeholder = "Search",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(LantrTheme.extendedColors.arcInspectorSearchBar)
            )
        }

        item {
            Text(
                text = "Favourites",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .dashedBorder(
                        color = Color.Gray.copy(alpha = 0.4f),
                        width = 2.8.dp,
                        radius = 28.dp,
                        dashLength = 8.dp,
                        gapLength = 8.dp
                    )
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FavouriteStoryBlockCard(
                        storyBlock = StoryBlock.predefined(PredefinedBlockType.DEEP_DIVE),
                        dragDropManager = dragDropManager
                    )
                    FavouriteStoryBlockCard(
                        storyBlock = StoryBlock.predefined(PredefinedBlockType.COMIC_BEAT),
                        dragDropManager = dragDropManager
                    )
                    FavouriteStoryBlockCard(
                        storyBlock = StoryBlock.predefined(PredefinedBlockType.OPEN_LOOP),
                        dragDropManager = dragDropManager
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            StoryBlocksCategoryRow(
                category = StoryBlockCategory.HOOK,
                iconColor = Color(0xffB01433),
                onSelect = { onCategorySelected(StoryBlockCategory.HOOK) }
            )
        }

        item {
            StoryBlocksCategoryRow(
                category = StoryBlockCategory.STAKES,
                iconColor = Color(0xff731B49),
                onSelect = { onCategorySelected(StoryBlockCategory.STAKES) }
            )
        }

        item {
            StoryBlocksCategoryRow(
                category = StoryBlockCategory.CORE,
                iconColor = Color(0xff731B49),
                onSelect = { onCategorySelected(StoryBlockCategory.CORE) }
            )
        }

        item {
            StoryBlocksCategoryRow(
                category = StoryBlockCategory.TURN,
                iconColor = Color(0xffCB9D08),
                onSelect = { onCategorySelected(StoryBlockCategory.TURN) }
            )
        }

        item {
            StoryBlocksCategoryRow(
                category = StoryBlockCategory.AUDIENCE,
                iconColor = Color(0xffCB9D08),
                onSelect = { onCategorySelected(StoryBlockCategory.AUDIENCE) }
            )
        }

        item {
            StoryBlocksCategoryRow(
                category = StoryBlockCategory.CLOSE,
                iconColor = Color(0xffB01433),
                onSelect = { onCategorySelected(StoryBlockCategory.CLOSE) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun SingleLineTextField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null)
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun StoryBlocksCategoryRow(
    category: StoryBlockCategory,
    iconColor: Color,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .height(60.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(30.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(17.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.displayTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = category.displaySubtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        HorizontalDivider()
    }
}

@Composable
private fun FavouriteStoryBlockCard(
    storyBlock: StoryBlock,
    dragDropManager: app.lantr.lantr.Views.DragAndDrop.DragAndDropManager,
    modifier: Modifier = Modifier
) {
    val predefinedType = storyBlock.predefinedType ?: return
    var isDragging by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .width(180.dp)
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
                        FavouriteStoryBlockCardPreview(storyBlock = storyBlock)
                    }
                )
            )
            .graphicsLayer {
                alpha = if (isDragging) 0.5f else 1f
                scaleX = if (isDragging) 0.98f else 1f
                scaleY = if (isDragging) 0.98f else 1f
            },
        shadowElevation = 13.dp,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(storyBlock.tintColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = storyBlock.blockType.getIcon(),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(storyBlock.tintColor.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HeartBroken,
                        contentDescription = "Remove favorite",
                        tint = storyBlock.tintColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = storyBlock.name,
                fontSize = if (PlatformUtil.isDesktop) 16.sp else 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun FavouriteStoryBlockCardPreview(
    storyBlock: StoryBlock
) {
    Surface(
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(28.dp),
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
        }
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

        val pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            phase = 0f
        )

        drawRoundRect(
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = width.toPx(),
                pathEffect = pathEffect
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius.toPx())
        )
    }
)