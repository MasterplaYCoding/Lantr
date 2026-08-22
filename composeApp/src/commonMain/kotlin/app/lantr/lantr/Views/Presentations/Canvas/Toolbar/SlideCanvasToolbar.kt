package app.lantr.lantr.Views.Presentations.Canvas.Toolbar


import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import app.lantr.lantr.Models.BlockType
import app.lantr.lantr.Models.ContentAlignment
import app.lantr.lantr.Models.ContentArrangement
import app.lantr.lantr.Models.ImageBlock
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.TextBlock
import app.lantr.lantr.Models.Themes.Theme
import app.lantr.lantr.Models.models_UI.displayName
import app.lantr.lantr.Models.models_UI.isImageType
import app.lantr.lantr.Models.models_UI.slideBackgroundColor
import app.lantr.lantr.Models.models_UI.textColor
import app.lantr.lantr.Models.models_UI.toRGBColor
import app.lantr.lantr.Util.NormalizedCropRect
import app.lantr.lantr.Util.loadPresentationImageInfo
import app.lantr.lantr.Util.loadPresentationImageBitmap
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.Util.PlatformImageFromPath
import app.lantr.lantr.Util.applyPresentationImageEdits
import app.lantr.lantr.Util.preparePresentationImageForPresentation
import app.lantr.lantr.Util.requestPickPresentationImage
import app.lantr.lantr.Util.savePresentationImageBitmapToPresentation
import app.lantr.lantr.ViewModel.SlideViewModel
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.LocalCropperStyle
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.cropperStyle
import com.attafitamim.krop.core.crop.rememberImageCropper
import com.attafitamim.krop.ui.CropperPreview
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.add_circle
import app.lantr.lantr.generated.resources.add_photo_alternate
import app.lantr.lantr.generated.resources.align_vertical_bottom
import app.lantr.lantr.generated.resources.align_vertical_center
import app.lantr.lantr.generated.resources.align_vertical_top
import app.lantr.lantr.generated.resources.crop
import app.lantr.lantr.generated.resources.event_note
import app.lantr.lantr.generated.resources.format_align_center
import app.lantr.lantr.generated.resources.format_align_left
import app.lantr.lantr.generated.resources.format_align_right
import app.lantr.lantr.generated.resources.format_ink_highlighter
import app.lantr.lantr.generated.resources.palette
import app.lantr.lantr.generated.resources.replace_image
import app.lantr.lantr.generated.resources.timeline
import app.lantr.lantr.generated.resources.transition_chop

import kotlin.Unit
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Main Toolbar Composable
@Composable
fun SlideCanvasToolbar(
    presentationId: String,
    slide: Slide,
    theme: Theme,
    onSlideChange: (Slide) -> Unit,
    // onThemeChange: (Theme) -> Unit = {},
    deleteSlideAction: () -> Unit,
    viewModel: SlideViewModel,
    onEditorModeChange: () -> Unit,
    // sheetManager: SheetManager = remember { SheetManager() },
    modifier: Modifier = Modifier
) {

    val toolbarHeight = if (PlatformUtil.isDesktop) 46.dp else 52.dp
    val focusedBlockId by viewModel.focusedContentBlockId.collectAsState()
    val focusedBlock = focusedBlockId?.let { id -> slide.blocks.firstOrNull { it.id == id } }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(toolbarHeight)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(start = 10.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            when {
                focusedBlock == null -> {
                    DefaultToolbar(
                        slide = slide,
                        theme = theme,
                        onSlideChange = onSlideChange,
                        deleteSlideAction = deleteSlideAction,
                        viewModel = viewModel,
                        onEditorModeChange = onEditorModeChange,
                    // sheetManager = sheetManager
                    )
                }
                focusedBlock?.type?.isImageType == true -> {
                    ImageBlockToolbar(
                        presentationId = presentationId,
                        slide = slide,
                        onSlideChange = onSlideChange,
                        viewModel = viewModel,
                       // sheetManager = sheetManager
                    )
                }
                else -> {
                    TextBlockToolbar(
                        slide = slide,
                        theme = theme,
                        onSlideChange = onSlideChange,
                        viewModel = viewModel,
                        // sheetManager = sheetManager
                    )
                }
            }
        }
    }
}

// Default Toolbar
@Composable
fun DefaultToolbar(
    slide: Slide,
    theme: Theme,
    onSlideChange: (Slide) -> Unit,
    deleteSlideAction: () -> Unit,
    viewModel: SlideViewModel,
    onEditorModeChange: () -> Unit,
    // sheetManager: SheetManager
) {
    var isInsertToggled by remember { mutableStateOf(false) }
    var isColorPickerShown by remember { mutableStateOf(false) }

    val insertButtonTitle = if (isInsertToggled) null else "Insert"

    val currentSlideColor = slide.overrideColor?.toColor()
        ?: (if (slide.usesAltScheme) theme.altBackgroundColor else theme.slideBackgroundColor)

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.wrapContentWidth()
    ) {
        // Editor Group
        ToolbarGroup {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Editor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 6.dp)
                )
                ToolbarButtonGroup {
                    ToolbarButton(
                        title = "Arc",
                        icon = Res.drawable.timeline,
                        isActive = false,
                        isDisabled = false,
                        helpText = "Switch to Arc Mode",
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        tintColor = MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            onEditorModeChange()
                        }
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        title = "Slides",
                        icon = Res.drawable.transition_chop,
                        isActive = true,
                        isDisabled = false,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        tintColor = MaterialTheme.colorScheme.background,
                        helpText = "Switch to Slides Mode",
                        onClick = { }
                    )
                }
            }
        }

        // Insert Group
        ToolbarGroup {
            AnimatedContent(targetState = isInsertToggled) { expanded ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToolbarButton(
                        title = insertButtonTitle,
                        icon = Res.drawable.add_circle,
                        isActive = true,
                        helpText = "Insert content",
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        tintColor = MaterialTheme.colorScheme.background,
                        onClick = { isInsertToggled = !isInsertToggled }
                    )

                    if (expanded) {
                        ToolbarButtonGroup {
                            ToolbarButton(
                                title = "Text",
                                icon = Res.drawable.format_align_left,
                                isActive = false,
                                helpText = "Insert text",
                                onClick = {
                                    onSlideChange(viewModel.addContentBlock(slide, BlockType.body))
                                }
                            )
                            ToolbarDivider()
                            ToolbarButton(
                                title = "Notes",
                                icon = Res.drawable.event_note,
                                isActive = false,
                                helpText = "Insert presenter notes",
                                onClick = {
                                    onSlideChange(viewModel.addContentBlock(slide, BlockType.presenterNotes))
                                }
                            )
                        }
                    ToolbarButton(
                        title = "Image",
                        icon = Res.drawable.add_photo_alternate,
                        isActive = false,
                        helpText = "Insert image",
                        onClick = {
                                onSlideChange(viewModel.addContentBlock(slide, BlockType.image))
                        }
                    )
                }
            }
            }
        }

        // Arrangement Group
        ToolbarGroup {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Arrangement",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 6.dp)
                )
                ToolbarButtonGroup {
                    ToolbarButton(
                        icon = Res.drawable.align_vertical_top,
                        isActive = viewModel.isSlideContentArranged(slide, ContentArrangement.top),
                        isDisabled = false,
                        helpText = "Arrange Top",
                        backgroundColor = if (viewModel.isSlideContentArranged(slide, ContentArrangement.top)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        tintColor = if (viewModel.isSlideContentArranged(slide, ContentArrangement.top)) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        onClick = {
                            onSlideChange(viewModel.setSlideContentArrangement(slide, ContentArrangement.top))
                        }
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        icon = Res.drawable.align_vertical_center,
                        isActive = viewModel.isSlideContentArranged(slide, ContentArrangement.center),
                        isDisabled = false,
                        helpText = "Arrange Center",
                        backgroundColor = if (viewModel.isSlideContentArranged(slide, ContentArrangement.center)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        tintColor = if (viewModel.isSlideContentArranged(slide, ContentArrangement.center)) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        onClick = {
                            onSlideChange(viewModel.setSlideContentArrangement(slide, ContentArrangement.center))
                        }
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        icon = Res.drawable.align_vertical_bottom,
                        isActive = viewModel.isSlideContentArranged(slide, ContentArrangement.bottom),
                        isDisabled = false,
                        helpText = "Arrange Bottom",
                        backgroundColor = if (viewModel.isSlideContentArranged(slide, ContentArrangement.bottom)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        tintColor = if (viewModel.isSlideContentArranged(slide, ContentArrangement.bottom)) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        onClick = {
                            onSlideChange(viewModel.setSlideContentArrangement(slide, ContentArrangement.bottom))
                        }
                    )
                }
            }
        }

        // Slide Color Group
        ToolbarGroup {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Color",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 6.dp)
                )
                ToolbarButton(
                    icon = Res.drawable.palette,
                    isActive = true,
                    isDisabled = false,
                    helpText = "Slide Color",
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    tintColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        isColorPickerShown = true
                        // sheetManager.willPresent()
                    }
                )
            }
        }

        // Delete Slide Group
        ToolbarGroup {
            ToolbarDeleteButton(
                helpText = "Delete Slide",
                action = deleteSlideAction
            )
        }
    }

    // Color picker dialog
    if (isColorPickerShown) {
        ExtendedColorPicker(
            selectedColor = currentSlideColor as Color,
            onColorSelected = { color ->
                viewModel.setSlideOverrideColor(slide, color.toRGBColor())
            },
            onDismiss = {
                isColorPickerShown = false
                // sheetManager.didDismiss()
            }
        )
    }
}

// Text Block Toolbar
@Composable
fun TextBlockToolbar(
    slide: Slide,
    theme: Theme,
    onSlideChange: (Slide) -> Unit,
    viewModel: SlideViewModel,
   //  sheetManager: SheetManager
) {
    var isColorPickerShown by remember { mutableStateOf(false) }
    var showStylePopover by remember { mutableStateOf(false) }

    val focusedBlock = viewModel.getFocusedContentBlock(slide) as? TextBlock
    val focusedBlockId by viewModel.focusedContentBlockId.collectAsState()
    val currentTextColor = focusedBlock?.overrideColor?.toColor()
        ?: theme.textColor(focusedBlock?.type ?: BlockType.body)

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.wrapContentWidth()
    ) {
        // Format Group
        ToolbarGroup {
            ToolbarTextStyleButton(
                currentStyleName = focusedBlock?.type?.displayName ?: "Style",
                showStylePopover = showStylePopover,
                onToggle = { showStylePopover = !showStylePopover },
            ) {
                TextStyleRow(BlockType.h1, focusedBlock?.type ?: BlockType.h1,
                    onSelect = { focusedBlockId?.let { id ->
                        onSlideChange(viewModel.updateTextBlockType(slide, id, BlockType.h1))
                    }})
                TextStyleRow(BlockType.h2, focusedBlock?.type ?: BlockType.h1,
                    onSelect = { focusedBlockId?.let { id ->
                        onSlideChange(viewModel.updateTextBlockType(slide, id, BlockType.h2))
                    }})
                TextStyleRow(BlockType.h3, focusedBlock?.type ?: BlockType.h1,
                    onSelect = { focusedBlockId?.let { id ->
                        onSlideChange(viewModel.updateTextBlockType(slide, id, BlockType.h3))
                    }})
                TextStyleRow(BlockType.quote, focusedBlock?.type ?: BlockType.h1,
                    onSelect = { focusedBlockId?.let { id ->
                        onSlideChange(viewModel.updateTextBlockType(slide, id, BlockType.quote))
                    }})
                TextStyleRow(BlockType.body, focusedBlock?.type ?: BlockType.h1,
                    onSelect = { focusedBlockId?.let { id ->
                        onSlideChange(viewModel.updateTextBlockType(slide, id, BlockType.body))
                    }})
                TextStyleRow(BlockType.presenterNotes, focusedBlock?.type ?: BlockType.h1,
                    onSelect = { focusedBlockId?.let { id ->
                        onSlideChange(viewModel.updateTextBlockType(slide, id, BlockType.presenterNotes))
                    }})
            }
        }

        // Align Group
        ToolbarGroup {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Align",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 6.dp)
                )
                ToolbarButtonGroup {

                    ToolbarButton(
                        icon = Res.drawable.format_align_left,
                        isActive = viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.leading),
                        isDisabled = false,
                        helpText = "Align Left",
                        backgroundColor = if (viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.leading)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        tintColor = if (viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.leading)) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        onClick = {
                            onSlideChange(viewModel.setFocusedContentBlockAlignment(slide, ContentAlignment.leading))
                        }
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        icon = Res.drawable.format_align_center,
                        isActive = viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.center),
                        isDisabled = false,
                        helpText = "Align Center",
                        backgroundColor = if (viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.center)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        tintColor = if (viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.center)) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        onClick = {
                            onSlideChange(viewModel.setFocusedContentBlockAlignment(slide, ContentAlignment.center))
                        }
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        icon = Res.drawable.format_align_right,
                        isActive = viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.trailing),
                        isDisabled = false,
                        helpText = "Align Right",
                        backgroundColor = if (viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.trailing)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        tintColor = if (viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.trailing)) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        onClick = {
                            onSlideChange(viewModel.setFocusedContentBlockAlignment(slide, ContentAlignment.trailing))
                        }
                    )
                }
            }
        }

        // Text Color Group (not shown for presenter notes)
        if (!viewModel.isFocusedContentBlockOfType(slide, BlockType.presenterNotes)) {
            ToolbarGroup {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Color",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                    ToolbarButton(
                        icon = Res.drawable.palette,
                        isActive = true,
                        isDisabled = false,
                        helpText = "Text Color",
                        tintColor = MaterialTheme.colorScheme.onSurface,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        onClick = {
                            isColorPickerShown = true
                            // sheetManager.willPresent()
                        }
                    )
                }
            }
        }

        // Delete Block Group
        ToolbarGroup {
            ToolbarDeleteButton(
                helpText = "Delete Block",
                action = {
                    onSlideChange(viewModel.removeFocusedContentBlock(slide))
                }
            )
        }
    }

    /*
    // Style popover
    if (showStylePopover) {
        TextStylePopover(
            currentType = focusedBlock?.type ?: BlockType.body,
            onTypeSelected = { type ->
                viewModel.setFocusedContentBlockType(slide, type, onSlideChange)
                showStylePopover = false
                // sheetManager.didDismiss()
            },
            onDismiss = {
                showStylePopover = false
               // sheetManager.didDismiss()
            }
        )
    }

     */

    // Color picker dialog
    if (isColorPickerShown) {
        ExtendedColorPicker(
            selectedColor = currentTextColor,
            onColorSelected = { color ->
                viewModel.setTextBlockOverrideColor(slide, color.toRGBColor())
            },
            onDismiss = {
                isColorPickerShown = false
                // sheetManager.didDismiss()
            }
        )
    }
}

// Image Block Toolbar
@Composable
fun ImageBlockToolbar(
    presentationId: String,
    slide: Slide,
    onSlideChange: (Slide) -> Unit,
    viewModel: SlideViewModel,
    // sheetManager: SheetManager
) {
    var isImageCropperShown by remember { mutableStateOf(false) }
    val focusedImageBlock = viewModel.getFocusedContentBlock(slide) as? ImageBlock

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.wrapContentWidth()
    ) {
        // Image Group
        ToolbarGroup {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolbarButton(
                    icon = Res.drawable.replace_image,
                    isActive = true,
                    isDisabled = false,
                    helpText = "Replace Image",
                    backgroundColor = Color(0xffF2E7C1),
                    tintColor = Color(0xffCB9D08),
                    onClick = {
                        val imageBlock = focusedImageBlock ?: return@ToolbarButton

                        requestPickPresentationImage { imagePath ->
                            imagePath?.let {
                                preparePresentationImageForPresentation(it, presentationId)?.let { preparedImage ->
                                    onSlideChange(
                                        viewModel.updateImageBlockAsset(
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
                )
                ToolbarButtonGroup {
                    ToolbarButton(
                        icon = Res.drawable.crop,
                        isActive = false,
                        isDisabled = false,
                        helpText = "Crop",
                        backgroundColor = Color(0xffF2E7C1),
                        tintColor = Color(0xffCB9D08),
                        onClick = {
                            isImageCropperShown = true
                            // sheetManager.willPresent()
                        }
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        icon = Res.drawable.format_ink_highlighter,
                        isActive = false,
                        isDisabled = false,
                        helpText = "Markup",
                        backgroundColor = Color(0xffF2E7C1),
                        tintColor = Color(0xffCB9D08),
                        onClick = { }
                    )
                }
            }
        }

        // Align Group
        ToolbarGroup {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Align",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 6.dp)
                )
                ToolbarButtonGroup {
                    ToolbarButton(
                        icon = Res.drawable.format_align_left,
                        isActive = viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.leading),
                        isDisabled = false,
                        helpText = "Align Left",
                        backgroundColor = Color(0xffF2E7C1),
                        tintColor = Color(0xffCB9D08),
                        onClick = {
                            onSlideChange(viewModel.setFocusedContentBlockAlignment(slide, ContentAlignment.leading))
                        }
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        icon = Res.drawable.format_align_center,
                        isActive = viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.center),
                        isDisabled = false,
                        helpText = "Align Center",
                        backgroundColor = Color(0xffF2E7C1),
                        tintColor = Color(0xffCB9D08),
                        onClick = {
                            onSlideChange(viewModel.setFocusedContentBlockAlignment(slide, ContentAlignment.center))
                        }
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        icon = Res.drawable.format_align_right,
                        isActive = viewModel.isFocusedContentBlockAligned(slide, ContentAlignment.trailing),
                        isDisabled = false,
                        helpText = "Align Right",
                        backgroundColor = Color(0xffF2E7C1),
                        tintColor = Color(0xffCB9D08),
                        onClick = {
                            onSlideChange(viewModel.setFocusedContentBlockAlignment(slide, ContentAlignment.trailing))
                        }
                    )
                }
            }
        }

        // Delete Block Group
        ToolbarGroup {
            ToolbarDeleteButton(
                helpText = "Delete Block",
                action = {
                    onSlideChange(viewModel.removeFocusedContentBlock(slide))
                }
            )
        }
    }

    /*

    // Image cropper dialog
    if (isImageCropperShown) {
        Dialog(onDismissRequest = {
            isImageCropperShown = false
            // sheetManager.didDismiss()
        }) {
            CropImageView(
                slide = slide,
                onSlideChange = onSlideChange,
                viewModel = viewModel
            )
        }
    }

     */

    if (isImageCropperShown && focusedImageBlock != null) {
        ImageEditDialog(
            presentationId = presentationId,
            slide = slide,
            imageBlock = focusedImageBlock,
            viewModel = viewModel,
            onSlideChange = onSlideChange,
            onDismiss = { isImageCropperShown = false }
        )
    }
}

private enum class ImageEditTab(val title: String) {
    Crop("Crop"),
    Rotate("Rotation"),
    Radius("Corners")
}

@Composable
private fun ImageEditDialog(
    presentationId: String,
    slide: Slide,
    imageBlock: ImageBlock,
    viewModel: SlideViewModel,
    onSlideChange: (Slide) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember(imageBlock.id) { mutableStateOf(ImageEditTab.Crop) }
    var rotationQuarterTurns by remember(imageBlock.id) { mutableStateOf(0) }
    var radiusValue by remember(imageBlock.id) { mutableStateOf(imageBlock.cornerRadius) }
    var cropErrorMessage by remember(imageBlock.id) { mutableStateOf<String?>(null) }
    var editBaseImagePath by remember(imageBlock.id) { mutableStateOf(imageBlock.imagePath) }

    val originalBlock = remember(imageBlock.id) { imageBlock.copy() }
    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState
    val cropperLoadingStatus = imageCropper.loadingStatus
    val scope = rememberCoroutineScope()
    val embeddedCropperStyle = remember {
        cropperStyle(
            backgroundColor = Color(0xFF111111),
            rectColor = Color.White,
            overlay = Color.Black.copy(alpha = 0.55f),
            autoZoom = true
        )
    }

    LaunchedEffect(imageBlock.id) {
        editBaseImagePath = imageBlock.imagePath
    }

    LaunchedEffect(imageBlock.id, rotationQuarterTurns, editBaseImagePath) {
        if (editBaseImagePath.isBlank()) {
            return@LaunchedEffect
        }

        if (rotationQuarterTurns == 0) {
            val focusedImageBlock = viewModel.getFocusedContentBlock(slide) as? ImageBlock
            if (focusedImageBlock != null && focusedImageBlock.imagePath != editBaseImagePath) {
                onSlideChange(
                    viewModel.updateImageBlockPath(
                        slide = slide,
                        blockId = originalBlock.id,
                        imagePath = editBaseImagePath
                    )
                )
            }
            return@LaunchedEffect
        }

        delay(120)
        applyPresentationImageEdits(
            imagePath = editBaseImagePath,
            cropRect = NormalizedCropRect(),
            rotationQuarterTurns = rotationQuarterTurns
        )?.let { editedImage ->
            onSlideChange(
                viewModel.updateImageBlockAsset(
                    slide = slide,
                    blockId = originalBlock.id,
                    imagePath = editedImage.imagePath,
                    imageHeight = currentImageHeight(
                        slide = slide,
                        viewModel = viewModel,
                        fallback = originalBlock.imageHeight,
                        blockId = originalBlock.id
                    )
                )
            )
        }
    }

    Dialog(
        onDismissRequest = {
            onSlideChange(viewModel.replaceImageBlock(slide, originalBlock))
            onDismiss()
        }
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .width(720.dp)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Edit image",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                val currentImageBlock = viewModel.getFocusedContentBlock(slide) as? ImageBlock ?: imageBlock

                TabRow(selectedTabIndex = selectedTab.ordinal) {
                    ImageEditTab.entries.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = { Text(tab.title) }
                        )
                    }
                }

                when (selectedTab) {
                    ImageEditTab.Crop -> {
                        if (cropState != null) {
                            EmbeddedCropPreview(
                                cropState = cropState,
                                modifier = Modifier.fillMaxWidth(),
                                cropperStyle = embeddedCropperStyle
                            )
                        } else {
                            ImagePreviewSurface(
                                imagePath = currentImageBlock.imagePath,
                                cornerRadius = radiusValue
                            )
                        }
                        CropControls(
                            cropSessionActive = cropState != null,
                            isLoading = cropperLoadingStatus != null,
                            errorMessage = cropErrorMessage,
                            onOpenCropTool = {
                                val sourcePath = currentImageBlock.imagePath
                                if (sourcePath.isBlank()) {
                                    cropErrorMessage = "No image available to crop yet."
                                    return@CropControls
                                }

                                scope.launch {
                                    cropErrorMessage = null
                                    val imageBitmap = loadPresentationImageBitmap(sourcePath)
                                    if (imageBitmap == null) {
                                        cropErrorMessage = "The image couldn't be loaded for cropping."
                                        return@launch
                                    }

                                    when (
                                        val result = imageCropper.crop(
                                            imageBitmap,
                                            maxResultSize = IntSize(imageBitmap.width, imageBitmap.height)
                                        )
                                    ) {
                                        CropResult.Cancelled -> Unit
                                        CropError.LoadingError -> {
                                            cropErrorMessage = "The image couldn't be loaded for cropping."
                                        }
                                        CropError.SavingError -> {
                                            cropErrorMessage = "The cropped image couldn't be created."
                                        }
                                        is CropResult.Success -> {
                                            val preparedImage = savePresentationImageBitmapToPresentation(
                                                bitmap = result.bitmap,
                                                presentationId = presentationId
                                            )

                                            if (preparedImage == null) {
                                                cropErrorMessage = "The cropped image couldn't be saved."
                                                return@launch
                                            }

                                            editBaseImagePath = preparedImage.imagePath
                                            rotationQuarterTurns = 0
                                            onSlideChange(
                                                viewModel.updateImageBlockAsset(
                                                    slide = slide,
                                                    blockId = originalBlock.id,
                                                    imagePath = preparedImage.imagePath,
                                                    imageHeight = currentImageBlock.imageHeight
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            onResetCrop = { cropState?.reset() },
                            onCancelCrop = { cropState?.done(accept = false) },
                            onApplyCrop = { cropState?.done(accept = true) }
                        )
                    }

                    ImageEditTab.Rotate -> {
                        ImagePreviewSurface(
                            imagePath = currentImageBlock.imagePath,
                            cornerRadius = radiusValue
                        )
                        RotationControls(
                            onRotateLeft = { rotationQuarterTurns -= 1 },
                            onRotateRight = { rotationQuarterTurns += 1 }
                        )
                    }

                    ImageEditTab.Radius -> {
                        ImagePreviewSurface(
                            imagePath = currentImageBlock.imagePath,
                            cornerRadius = radiusValue
                        )
                        CornerRadiusControls(
                            radiusValue = radiusValue,
                            onRadiusChange = { value ->
                                radiusValue = value
                                onSlideChange(
                                    viewModel.updateImageBlockRadius(
                                        slide = slide,
                                        blockId = originalBlock.id,
                                        radius = value
                                    )
                                )
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            cropState?.done(accept = false)
                            onSlideChange(viewModel.replaceImageBlock(slide, originalBlock))
                            onDismiss()
                        }
                    ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedTab == ImageEditTab.Crop && cropState != null) {
                                cropState.done(accept = true)
                                return@Button
                            }
                            onDismiss()
                        }
                    ) {
                        Text(
                            if (selectedTab == ImageEditTab.Crop && cropState != null) {
                                "Apply Crop"
                            } else {
                                "Done"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImagePreviewSurface(
    imagePath: String,
    cornerRadius: Float
) {
    val imageInfo = remember(imagePath) {
        imagePath.takeIf { it.isNotBlank() }?.let(::loadPresentationImageInfo)
    }
    val imageAspectRatio = imageInfo?.let { info ->
        if (info.height > 0) info.width.toFloat() / info.height.toFloat() else null
    }
    val previewHeight = 280.dp
    val renderedWidth = when {
        imageAspectRatio != null && imageAspectRatio > 0f -> previewHeight * imageAspectRatio
        else -> previewHeight
    }
    val previewLongEdge = maxOf(renderedWidth.value, previewHeight.value)
    val previewCornerRadius = (cornerRadius * maxOf(0.4f, previewLongEdge / 1920f)).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(previewHeight)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        if (imagePath.isNotBlank()) {
            PlatformImageFromPath(
                imagePath = imagePath,
                contentDescription = "Image preview",
                modifier = Modifier
                    .height(previewHeight)
                    .then(
                        if (imageAspectRatio != null && imageAspectRatio > 0f) {
                            Modifier.aspectRatio(imageAspectRatio, matchHeightConstraintsFirst = true)
                        } else {
                            Modifier.fillMaxWidth()
                        }
                    )
                    .clip(RoundedCornerShape(previewCornerRadius)),
                contentScale = ContentScale.Fit
            )
        } else {
            Text("No image available")
        }
    }
}

@Composable
private fun CropControls(
    cropSessionActive: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onOpenCropTool: () -> Unit,
    onResetCrop: () -> Unit,
    onCancelCrop: () -> Unit,
    onApplyCrop: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = if (cropSessionActive) {
                "Drag the crop handles over the image, then apply the crop."
            } else {
                "Start crop mode to place and resize a crop window over the image."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (cropSessionActive) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onResetCrop) {
                    Text("Reset")
                }
                OutlinedButton(onClick = onCancelCrop) {
                    Text("Cancel Crop")
                }
                Button(onClick = onApplyCrop) {
                    Text("Apply Crop")
                }
            }
        } else {
            Button(
                onClick = onOpenCropTool,
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Preparing crop tool..." else "Start Crop Mode")
            }
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun EmbeddedCropPreview(
    cropState: com.attafitamim.krop.core.crop.CropState,
    modifier: Modifier = Modifier,
    cropperStyle: com.attafitamim.krop.core.crop.CropperStyle
) {
    CompositionLocalProvider(LocalCropperStyle provides cropperStyle) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF111111))
        ) {
            CropperPreview(
                state = cropState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun RotationControls(
    onRotateLeft: () -> Unit,
    onRotateRight: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onRotateLeft) {
            Text("Rotate 90° left")
        }
        Button(onClick = onRotateRight) {
            Text("Rotate 90° right")
        }
    }
}

@Composable
private fun CornerRadiusControls(
    radiusValue: Float,
    onRadiusChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Corner radius", style = MaterialTheme.typography.labelLarge)
            Text(radiusValue.roundToInt().toString(), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Slider(
            value = radiusValue,
            onValueChange = onRadiusChange,
            valueRange = 0f..100f
        )
    }
}

private fun currentImageHeight(
    slide: Slide,
    viewModel: SlideViewModel,
    fallback: Float,
    blockId: java.util.UUID
): Float {
    val imageBlock = viewModel.getFocusedContentBlock(slide) as? ImageBlock
    return when {
        imageBlock?.id == blockId -> imageBlock.imageHeight
        else -> (slide.blocks.firstOrNull { it.id == blockId } as? ImageBlock)?.imageHeight ?: fallback
    }
}

// Helper Components
@Composable
fun ToolbarGroup(
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.wrapContentSize()
            .clip(RoundedCornerShape(20.dp))
    ) {
        Box(modifier = Modifier.padding(6.dp)) {
            content()
        }
    }
}

@Composable
fun ToolbarDivider() {
    VerticalDivider(
        modifier = Modifier
            .padding(vertical = 4.dp),
        color = Color.Gray.copy(alpha = 0.3f)
    )
}

@Composable
fun TextStyleRow(
    type: BlockType,
    currentType: BlockType,
    onSelect: (BlockType) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isHovered) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .hoverable(interactionSource)
            .clickable { onSelect(type) }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (currentType == type) "✓" else " ",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            modifier = Modifier.width(34.dp)
        )
        Text(
            text = type.displayName,
            fontSize = when(type) {
                BlockType.h1 -> 20.sp
                BlockType.h2 -> 18.sp
                BlockType.h3 -> 16.sp
                else -> 14.sp
            },
            fontWeight = when(type) {
                BlockType.h1, BlockType.h2, BlockType.h3 -> FontWeight.Bold
                BlockType.quote -> FontWeight.Medium
                else -> FontWeight.Normal
            },
            color = if (type == BlockType.presenterNotes) Color.Gray
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Placeholder components
@Composable
fun ExtendedColorPicker(
    selectedColor: Color,
    onColorSelected: @Composable (Color) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .size(280.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Color Picker", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                // Color picker implementation would go here
                Button(onClick = onDismiss) {
                    Text("Done")
                }
            }
        }
    }
}
