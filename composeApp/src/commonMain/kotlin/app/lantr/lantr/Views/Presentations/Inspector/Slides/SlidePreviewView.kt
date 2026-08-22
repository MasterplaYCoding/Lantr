package app.lantr.lantr.Views.Presentations.Inspector.Slides

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.BlockType
import app.lantr.lantr.Models.ContentAlignment
import app.lantr.lantr.Models.ContentArrangement
import app.lantr.lantr.Models.ImageBlock
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.StoryBlock
import app.lantr.lantr.Models.TextBlock
import app.lantr.lantr.Models.Themes.Theme
import app.lantr.lantr.Models.models_UI.fontSize
import app.lantr.lantr.Models.models_UI.textColor
import app.lantr.lantr.Util.PlatformImageFromPath
import app.lantr.lantr.ViewModel.PresentationViewModel
import java.io.File

@Composable
fun SlidePreviewView(
    slide: Slide,
    theme: Theme,
    isSelected: Boolean,
    slideIndex: Int,
    storyBlock: StoryBlock,
    viewModel: PresentationViewModel,
    onTap: () -> Unit,
    onOverflowChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDropTarget by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Drop zone area
        if (isDropTarget) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            )
        }

        // Slide preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .onSizeChanged { size ->
                    canvasSize = size
                }
        ) {
            val canvasWidth = with(density) { canvasSize.width.toDp() }
            val slideHeight = canvasWidth * 9f / 16f

            // Slide background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(slideHeight)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        slide.overrideColor?.toColor() ?: if (slide.usesAltScheme) {
                            theme.altBackgroundColor.toColor()
                        } else {
                            theme.backgroundColor.toColor()
                        }
                    )
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) {
                            Color(0xffCB9D08)
                        } else {
                            Color.Gray.copy(alpha = 0.3f)
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(onClick = onTap)
            ) {
                val contentMargins = scaledValue(theme.contentMargins, canvasWidth)
                val availableContentHeight = slideHeight - (contentMargins * 2)

                // Content with alignment
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentMargins),
                    contentAlignment = when (slide.contentArrangement) {
                        ContentArrangement.top -> Alignment.TopCenter
                        ContentArrangement.center -> Alignment.Center
                        ContentArrangement.bottom -> Alignment.BottomCenter
                    }
                ) {
                    SlideContent(
                        slide = slide,
                        theme = theme,
                        canvasWidth = canvasWidth,
                        availableHeight = availableContentHeight,
                        onOverflowChange = onOverflowChange
                    )
                }
            }
        }
    }

    // TODO: Add drop destination support when implementing drag & drop
}

@Composable
private fun SlideContent(
    slide: Slide,
    theme: Theme,
    canvasWidth: androidx.compose.ui.unit.Dp,
    availableHeight: androidx.compose.ui.unit.Dp,
    onOverflowChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var contentHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .widthIn(max = canvasWidth)
            .heightIn(max = availableHeight)
            .onSizeChanged { size ->
                with(density) {
                    val newHeight = size.height.toDp()
                    if (contentHeight != newHeight) {
                        contentHeight = newHeight
                        checkForOverflow(contentHeight, availableHeight, onOverflowChange)
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        slide.blocks.forEachIndexed { index, block ->
            if (block.type != BlockType.presenterNotes) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    when (block) {
                        is ImageBlock -> {
                            ImageBlockContent(
                                imageBlock = block,
                                theme = theme,
                                canvasWidth = canvasWidth
                            )
                        }
                        is TextBlock -> {
                            TextBlockContent(
                                textBlock = block,
                                theme = theme,
                                slide = slide,
                                canvasWidth = canvasWidth
                            )
                        }
                    }

                    // Spacing between blocks
                    val hasMoreVisibleBlocks = slide.blocks
                        .drop(index + 1)
                        .any { it.type != BlockType.presenterNotes }

                    if (hasMoreVisibleBlocks) {
                        Spacer(modifier = Modifier.height(scaledValue(theme.elementSpacing, canvasWidth)))
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageBlockContent(
    imageBlock: ImageBlock,
    theme: Theme,
    canvasWidth: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val imageHeight = scaledValue(imageBlock.imageHeight, canvasWidth)
    val imageCornerRadius = scaledValue(imageBlock.cornerRadius, canvasWidth)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(imageHeight),
        contentAlignment = imageBlock.contentAlignment.toComposeAlignment()
    ) {
        when {
            imageBlock.imagePath.isEmpty() -> {
                // Empty placeholder
                Box(modifier = Modifier.fillMaxSize())
            }
            imageBlock.imagePath.startsWith("/") -> {
                // File path - load from disk
                val file = File(imageBlock.imagePath)
                if (file.exists()) {
                    PlatformImageFromPath(
                        imagePath = file.absolutePath,
                        contentDescription = null,
                        modifier = Modifier
                            .height(imageHeight)
                            .clip(RoundedCornerShape(imageCornerRadius)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    // File not found
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(imageCornerRadius))
                            .background(Color.Gray.copy(alpha = 0.2f))
                    )
                }
            }
            else -> {
                PlatformImageFromPath(
                    imagePath = imageBlock.imagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .height(imageHeight)
                        .clip(RoundedCornerShape(imageCornerRadius)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun TextBlockContent(
    textBlock: TextBlock,
    theme: Theme,
    slide: Slide,
    canvasWidth: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val fontSize = theme.fontSize(textBlock.type)?.let {
        scaledValue(it, canvasWidth)
    } ?: 16.dp

    val textStyle = /*theme.textStyles[textBlock.type]?.getTextStyle(fontSize)
        ?: */MaterialTheme.typography.bodyMedium

    val textColor = textBlock.overrideColor?.toColor()
        ?: theme.textColor(textBlock.type)

    Text(
        text = textBlock.content,
        modifier = modifier.fillMaxWidth(),
        style = textStyle,
        color = textColor,
        textAlign = textBlock.contentAlignment.toTextAlign()
    )
}

private fun checkForOverflow(
    contentHeight: androidx.compose.ui.unit.Dp,
    availableHeight: androidx.compose.ui.unit.Dp,
    onOverflowChange: (Boolean) -> Unit
) {
    val hasOverflow = contentHeight > availableHeight
    onOverflowChange(hasOverflow)
}

private fun scaledValue(
    originalValue: Float,
    canvasWidth: androidx.compose.ui.unit.Dp
): androidx.compose.ui.unit.Dp {
    val calculatedScale = canvasWidth.value / 1920f
    return (originalValue * calculatedScale).dp
}

// Extension functions for alignment conversions
private fun ContentAlignment.toComposeAlignment(): Alignment {
    return when (this) {
        ContentAlignment.leading -> Alignment.CenterStart
        ContentAlignment.center -> Alignment.Center
        ContentAlignment.trailing -> Alignment.CenterEnd
    }
}

private fun ContentAlignment.toTextAlign(): TextAlign {
    return when (this) {
        ContentAlignment.leading -> TextAlign.Start
        ContentAlignment.center-> TextAlign.Center
        ContentAlignment.trailing -> TextAlign.End
    }
}
