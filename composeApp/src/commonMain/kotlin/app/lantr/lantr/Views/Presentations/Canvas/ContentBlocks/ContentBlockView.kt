package app.lantr.lantr.Views.Presentations.Canvas.ContentBlocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.ContentBlock
import app.lantr.lantr.Models.ImageBlock
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.TextBlock
import app.lantr.lantr.Models.models_UI.textAlignment
import app.lantr.lantr.Models.models_UI.canvasTextColor
import app.lantr.lantr.Models.models_UI.getSystemFont
import app.lantr.lantr.Util.loadPresentationImageInfo
import app.lantr.lantr.Util.preparePresentationImageForPresentation
import app.lantr.lantr.Services.CanvasLayout
import app.lantr.lantr.Util.PlatformImageFromPath
import app.lantr.lantr.Util.requestPickPresentationImage
import app.lantr.lantr.ViewModel.SlideViewModel
import app.lantr.lantr.Views.Presentations.Canvas.Base.CanvasConfiguration
import app.lantr.lantr.Views.Presentations.Canvas.ContentBlocks.Images.ImageContentBlockHandle
import app.lantr.lantr.Views.Presentations.Canvas.ContentBlocks.Texts.TextContentBlockEditingView
import java.util.UUID
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.add_circle
import org.jetbrains.compose.resources.painterResource

@Composable
fun ContentBlockView(
    presentationId: String,
    contentBlock: ContentBlock,
    slide: Slide,
    layout: CanvasLayout,
    isFocused: Boolean,
    currentBlockIndex: Int,
    onSlideUpdate: (Slide) -> Unit,
    onBlockFocus: (UUID) -> Unit,
    slideViewModel: SlideViewModel,
    modifier: Modifier = Modifier,
    enableClickable: Boolean = true
) {
    println("ContentBlockView: Rendering block ${contentBlock.id}, isFocused = $isFocused, isWideLayout = ${layout.isUsingWideLayout}")
    val config = remember { CanvasConfiguration.slide }

    // Determine layout mode based on available space
    if (layout.isUsingWideLayout) {
        WideLayoutContentBlock(
            contentBlock = contentBlock,
            presentationId = presentationId,
            slide = slide,
            layout = layout,
            isFocused = isFocused,
            currentBlockIndex = currentBlockIndex,
            onSlideUpdate = onSlideUpdate,
            onBlockFocus = onBlockFocus,
            slideViewModel = slideViewModel,
            config = config,
            modifier = modifier,
            enableClickable = enableClickable
        )
    } else {
        CompactLayoutContentBlock(
            contentBlock = contentBlock,
            presentationId = presentationId,
            slide = slide,
            layout = layout,
            isFocused = isFocused,
            currentBlockIndex = currentBlockIndex,
            onSlideUpdate = onSlideUpdate,
            onBlockFocus = onBlockFocus,
            slideViewModel = slideViewModel,
            config = config,
            modifier = modifier,
            enableClickable = enableClickable
        )
    }
}

@Composable
private fun WideLayoutContentBlock(
    contentBlock: ContentBlock,
    presentationId: String,
    slide: Slide,
    layout: CanvasLayout,
    isFocused: Boolean,
    currentBlockIndex: Int,
    onSlideUpdate: (Slide) -> Unit,
    onBlockFocus: (UUID) -> Unit,
    slideViewModel: SlideViewModel,
    config: CanvasConfiguration,
    modifier: Modifier = Modifier,
    enableClickable: Boolean = true
) {
    Box(modifier = modifier) {
        // Drop zone above
        /*
        if (isFocused) {
            DropZoneView(
                layout = layout,
                onDrop = { transferable ->
                    val updatedSlide = slideViewModel.handleContentBlockDrop(
                        transferable = transferable,
                        slide = slide,
                        targetIndex = currentBlockIndex
                    )
                    onSlideUpdate(updatedSlide)
                }
            )
        }
*/
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .background(
                    color = if (isFocused) Color.Gray.copy(alpha = 0.1f) else Color.Transparent,
                    shape = RoundedCornerShape(config.cardCornerRadius / 2)
                )
                .then(
                    if (enableClickable) {
                        Modifier.clickable {
                            println("ContentBlockView (Wide): Block ${contentBlock.id} clicked!")
                            onBlockFocus(contentBlock.id)
                            if (contentBlock is ImageBlock && contentBlock.imagePath.isBlank()) {
                                requestPickPresentationImage { imagePath ->
                                    imagePath?.let {
                                        preparePresentationImageForPresentation(it, presentationId)?.let { preparedImage ->
                                            onSlideUpdate(
                                                slideViewModel.updateImageBlockAsset(
                                                    slide = slide,
                                                    blockId = contentBlock.id,
                                                    imagePath = preparedImage.imagePath,
                                                    imageHeight = preparedImage.suggestedHeight
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Modifier
                    }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(config.blockPadding)
            ) {
                BlockContent(
                    presentationId = presentationId,
                    contentBlock = contentBlock,
                    slide = slide,
                    layout = layout,
                    isFocused = isFocused,
                    onSlideUpdate = onSlideUpdate,
                    slideViewModel = slideViewModel
                )
            }

            // Divider (visible when focused)
            if (isFocused) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            }

            // Controls area (visible when focused)
            if (isFocused) {



                Column(
                    modifier = Modifier
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ContentBlockControlButton(
                        contentBlock = contentBlock,
                        slide = slide,
                        viewModel = slideViewModel,
                        onSlideUpdate = onSlideUpdate
                    )

                    if (contentBlock is ImageBlock) {
                        ImageContentBlockHandle(
                            imageContentBlock = contentBlock,
                            slide = slide,
                            viewModel = slideViewModel,
                            onSlideUpdate = onSlideUpdate
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactLayoutContentBlock(
    contentBlock: ContentBlock,
    presentationId: String,
    slide: Slide,
    layout: CanvasLayout,
    isFocused: Boolean,
    currentBlockIndex: Int,
    onSlideUpdate: (Slide) -> Unit,
    onBlockFocus: (UUID) -> Unit,
    slideViewModel: SlideViewModel,
    config: CanvasConfiguration,
    modifier: Modifier = Modifier,
    enableClickable: Boolean = true
) {
    Column(modifier = modifier) {
        // Drop zone above
        /*
        if (isFocused) {
            DropZoneView(
                layout = layout,
                onDrop = { transferable ->
                    val updatedSlide = slideViewModel.handleContentBlockDrop(
                        transferable = transferable,
                        slide = slide,
                        targetIndex = currentBlockIndex
                    )
                    onSlideUpdate(updatedSlide)
                }
            )
        }
*/
        Column(
            modifier = Modifier
                .background(
                    color = if (isFocused) Color.Gray.copy(alpha = 0.1f) else Color.Transparent,
                    shape = RoundedCornerShape(config.cardCornerRadius / 2)
                )
                .then(
                    if (enableClickable) {
                        Modifier.clickable {
                            println("ContentBlockView (Compact): Block ${contentBlock.id} clicked!")
                            onBlockFocus(contentBlock.id)
                            if (contentBlock is ImageBlock && contentBlock.imagePath.isBlank()) {
                                requestPickPresentationImage { imagePath ->
                                    imagePath?.let {
                                        preparePresentationImageForPresentation(it, presentationId)?.let { preparedImage ->
                                            onSlideUpdate(
                                                slideViewModel.updateImageBlockAsset(
                                                    slide = slide,
                                                    blockId = contentBlock.id,
                                                    imagePath = preparedImage.imagePath,
                                                    imageHeight = preparedImage.suggestedHeight
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            // Content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(config.blockPadding)
            ) {
                BlockContent(
                    presentationId = presentationId,
                    contentBlock = contentBlock,
                    slide = slide,
                    layout = layout,
                    isFocused = isFocused,
                    onSlideUpdate = onSlideUpdate,
                    slideViewModel = slideViewModel
                )
            }

            // Controls area (visible when focused)
            if (isFocused) {
                HorizontalDivider(modifier = Modifier.padding(top = 4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.Gray.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(config.cardCornerRadius / 3)
                        )
                        .padding(horizontal = config.blockPadding.calculateStartPadding(
                            LayoutDirection.Ltr) / 2)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ContentBlockControlButton(
                        contentBlock = contentBlock,
                        slide = slide,
                        viewModel = slideViewModel,
                        onSlideUpdate = onSlideUpdate
                    )

                    if (contentBlock is ImageBlock) {
                        Spacer(modifier = Modifier.weight(1f))
                        ImageContentBlockHandle(
                            imageContentBlock = contentBlock,
                            slide = slide,
                            viewModel = slideViewModel,
                            onSlideUpdate = onSlideUpdate
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockContent(
    presentationId: String,
    contentBlock: ContentBlock,
    slide: Slide,
    layout: CanvasLayout,
    isFocused: Boolean,
    onSlideUpdate: (Slide) -> Unit,
    slideViewModel: SlideViewModel
) {
    when {
        isFocused && contentBlock is TextBlock -> {
            TextContentBlockEditingView(
                textContentBlock = contentBlock,
                slide = slide,
                layout = layout,
                viewModel = slideViewModel,
                onSlideUpdate = onSlideUpdate
            )
        }
        contentBlock is TextBlock -> {
            Text(
                text = contentBlock.content,
                style = contentBlock.type.getSystemFont(),
                color = contentBlock.type.canvasTextColor,
                textAlign = contentBlock.contentAlignment.textAlignment,
                modifier = Modifier.fillMaxWidth()
            )
        }
        contentBlock is ImageBlock -> {
            val canvasLongEdge = maxOf(layout.availableSize.width, layout.availableSize.height)
            val scale = maxOf(0.4f, canvasLongEdge / 1920f)
            val scaledHeight = contentBlock.imageHeight * scale
            val scaledCornerRadius = contentBlock.cornerRadius * scale
            val imageShape = RoundedCornerShape(scaledCornerRadius.dp)
            val imageInfo = remember(contentBlock.imagePath) {
                contentBlock.imagePath.takeIf { it.isNotBlank() }?.let(::loadPresentationImageInfo)
            }
            val imageAspectRatio = imageInfo?.let { info ->
                if (info.height > 0) info.width.toFloat() / info.height.toFloat() else null
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(scaledHeight.dp)
                    .clip(imageShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f))
                    .then(
                        if (contentBlock.imagePath.isBlank()) {
                            Modifier
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                    shape = imageShape
                                )
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = contentBlock.contentAlignment.boxAlignment
            ) {
                if (contentBlock.imagePath.isBlank()) {
                    EmptyImagePlaceholder(
                        presentationId = presentationId,
                        slideViewModel = slideViewModel,
                        slide = slide,
                        onSlideUpdate = onSlideUpdate
                    )
                } else {
                    PlatformImageFromPath(
                        imagePath = contentBlock.imagePath,
                        contentDescription = null,
                        modifier = Modifier
                            .height(scaledHeight.dp)
                            .then(
                                if (imageAspectRatio != null && imageAspectRatio > 0f) {
                                    Modifier.aspectRatio(imageAspectRatio, matchHeightConstraintsFirst = true)
                                } else {
                                    Modifier.fillMaxWidth()
                                }
                            )
                            .clip(imageShape)
                            .background(Color.Transparent),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.EmptyImagePlaceholder(
    presentationId: String,
    slideViewModel: SlideViewModel,
    slide: Slide,
    onSlideUpdate: (Slide) -> Unit
) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .clickable {
                ImageFileExplorer(
                    presentationId = presentationId,
                    slideViewModel = slideViewModel,
                    slide = slide,
                    onSlideUpdate = onSlideUpdate
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.add_circle),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Import",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun ImageFileExplorer(
    presentationId: String,
    slideViewModel: SlideViewModel,
    slide: Slide,
    onSlideUpdate: (Slide) -> Unit
) {
    val focusedImageBlock = slideViewModel.getFocusedContentBlock(slide) as? ImageBlock

    requestPickPresentationImage { imagePath ->
        imagePath?.let {
            val imageBlock = focusedImageBlock ?: return@let
            preparePresentationImageForPresentation(it, presentationId)?.let { preparedImage ->
                onSlideUpdate(
                    slideViewModel.updateImageBlockAsset(
                        slide = slide,
                        blockId = imageBlock.id,
                        imagePath = preparedImage.imagePath,
                        imageHeight = preparedImage.suggestedHeight
                    )
                )
            }
        }
    }
}
