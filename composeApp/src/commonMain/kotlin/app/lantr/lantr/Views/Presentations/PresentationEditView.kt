package app.lantr.lantr.Views.Presentations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.Arc
import app.lantr.lantr.Models.Presentation
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.Util.requestSaveLtrnPresentation
import app.lantr.lantr.Util.requestSaveLtrnPresentationAs
import app.lantr.lantr.ViewModel.ArcViewModel
import app.lantr.lantr.ViewModel.PresentationViewModel
import app.lantr.lantr.Views.Presentations.Canvas.ArcCanvasView
import app.lantr.lantr.Views.Presentations.Canvas.Popovers.PresentationInfoView
import app.lantr.lantr.Views.Presentations.Canvas.Popovers.ThemeDetailView
import app.lantr.lantr.Views.Presentations.Canvas.SlideCanvasView
import app.lantr.lantr.Views.Presentations.Inspector.EditorMode
import app.lantr.lantr.Views.Presentations.Inspector.InspectorContainer
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.right_panel_close
import org.jetbrains.compose.resources.painterResource

private const val InspectorAnimationDurationMs = 300
private val DesktopInspectorWidth = 350.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentationEditView(
    presentation: Presentation,
    initialFilePath: String? = null,
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel = remember(presentation) { PresentationViewModel(presentation) }
    val arcViewModel = remember(presentation) { ArcViewModel(presentation.arc) }
    val currentPresentation by viewModel.presentation.collectAsState()
    val currentArc by arcViewModel.arc.collectAsState()

    var editorMode by remember { mutableStateOf(EditorMode.ARC) }
    var inspectorShown by remember { mutableStateOf(true) }
    var showingEmptyArcAlert by remember { mutableStateOf(false) }
    var isInfoPopoverPresented by remember { mutableStateOf(false) }
    var isThemePopoverPresented by remember { mutableStateOf(false) }
    var currentFilePath by remember(presentation.id, initialFilePath) { mutableStateOf(initialFilePath) }

    val mergedPresentation = remember(currentPresentation, currentArc) {
        mergePresentationWithArc(currentPresentation, currentArc)
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    Scaffold(
        topBar = {
            PresentationEditTopBar(
                title = currentPresentation.title,
                editorMode = editorMode,
                inspectorShown = inspectorShown,
                isDesktop = PlatformUtil.isDesktop,
                onClose = onClose,
                onShowInfo = { isInfoPopoverPresented = true },
                onShowTheme = { isThemePopoverPresented = true },
                onShowInspector = { inspectorShown = true },
                onSave = {
                    requestSaveLtrnPresentation(
                        presentation = mergedPresentation,
                        currentFilePath = currentFilePath,
                        onSaved = { savedPath ->
                            currentFilePath = savedPath ?: currentFilePath
                        }
                    )
                },
                onSaveAs = {
                    requestSaveLtrnPresentationAs(
                        presentation = mergedPresentation,
                        suggestedTitle = mergedPresentation.title,
                        onSaved = { savedPath ->
                            currentFilePath = savedPath ?: currentFilePath
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        if (PlatformUtil.isPhone) {
            MobilePresentationEditor(
                viewModel = viewModel,
                arcViewModel = arcViewModel,
                editorMode = editorMode,
                onEditorModeChange = { editorMode = it },
                scaffoldState = bottomSheetScaffoldState,
                modifier = modifier.padding(paddingValues)
            )
        } else {
            DesktopPresentationEditor(
                viewModel = viewModel,
                arcViewModel = arcViewModel,
                editorMode = editorMode,
                inspectorShown = inspectorShown,
                onEditorModeChange = { editorMode = it },
                onInspectorShownChange = { inspectorShown = it },
                modifier = modifier.padding(paddingValues)
            )
        }

        if (isInfoPopoverPresented) {
            AlertDialog(
                onDismissRequest = { isInfoPopoverPresented = false },
                title = { Text("Presentation Information") },
                text = { PresentationInfoView(viewModel = viewModel) },
                confirmButton = {
                    TextButton(onClick = { isInfoPopoverPresented = false }) {
                        Text("Close")
                    }
                }
            )
        }

        if (isThemePopoverPresented) {
            AlertDialog(
                onDismissRequest = { isThemePopoverPresented = false },
                title = { Text("Theme") },
                text = {
                    ThemeDetailView(
                        viewModel = viewModel,
                        onDismiss = { isThemePopoverPresented = false }
                    )
                },
                confirmButton = {
                    TextButton(onClick = { isThemePopoverPresented = false }) {
                        Text("Done")
                    }
                }
            )
        }

        if (showingEmptyArcAlert) {
            AlertDialog(
                onDismissRequest = { showingEmptyArcAlert = false },
                title = { Text("Cannot Switch to Slides") },
                text = { Text("Add at least one block to your arc before proceeding to slides.") },
                confirmButton = {
                    TextButton(onClick = { showingEmptyArcAlert = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresentationEditTopBar(
    title: String,
    editorMode: EditorMode,
    inspectorShown: Boolean,
    isDesktop: Boolean,
    onClose: () -> Unit,
    onShowInfo: () -> Unit,
    onShowTheme: () -> Unit,
    onShowInspector: () -> Unit,
    onSave: () -> Unit,
    onSaveAs: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (!isDesktop) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        },
        actions = {
            Row(
                modifier = Modifier.animateContentSize(
                    animationSpec = tween(
                        durationMillis = InspectorAnimationDurationMs,
                        easing = FastOutSlowInEasing
                    )
                ),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isDesktop) {
                    DesktopEditorActions(
                        editorMode = editorMode,
                        onShowInfo = onShowInfo,
                        onShowTheme = onShowTheme,
                        onSave = onSave,
                        onSaveAs = onSaveAs
                    )
                } else {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                    IconButton(onClick = onSaveAs) {
                        Icon(Icons.Default.Save, contentDescription = "Save As")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Redo, contentDescription = "Redo")
                    }
                    IconButton(onClick = onShowInfo) {
                        Icon(Icons.Default.Info, contentDescription = "Information")
                    }
                    IconButton(onClick = { if (editorMode == EditorMode.SLIDES) onShowTheme() }) {
                        Icon(
                            imageVector = if (editorMode == EditorMode.SLIDES) Icons.Default.Palette else Icons.Default.Help,
                            contentDescription = if (editorMode == EditorMode.SLIDES) "Theme" else "Help"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Present")
                    }
                }

                if (isDesktop) {
                    AnimatedVisibility(
                        visible = !inspectorShown,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })
                    ) {
                        IconButton(onClick = onShowInspector) {
                            Icon(
                                painter = painterResource(Res.drawable.right_panel_close),
                                contentDescription = "Show Inspector",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun DesktopEditorActions(
    editorMode: EditorMode,
    onShowInfo: () -> Unit,
    onShowTheme: () -> Unit,
    onSave: () -> Unit,
    onSaveAs: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DesktopActionButton(
            icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
            label = "Save",
            onClick = onSave
        )
        DesktopActionButton(
            icon = { Icon(Icons.Default.Save, contentDescription = "Save As") },
            label = "Save As",
            onClick = onSaveAs
        )
        DesktopActionButton(
            icon = { Icon(Icons.Default.Undo, contentDescription = "Undo") },
            label = "Undo",
            onClick = {}
        )
        DesktopActionButton(
            icon = { Icon(Icons.Default.Redo, contentDescription = "Redo") },
            label = "Redo",
            onClick = {}
        )
        DesktopActionButton(
            icon = { Icon(Icons.Default.Info, contentDescription = "Info") },
            label = "Info",
            onClick = onShowInfo
        )
        DesktopActionButton(
            icon = {
                Icon(
                    imageVector = if (editorMode == EditorMode.SLIDES) Icons.Default.Palette else Icons.Default.Help,
                    contentDescription = if (editorMode == EditorMode.SLIDES) "Theme" else "Help"
                )
            },
            label = if (editorMode == EditorMode.SLIDES) "Theme" else "Help",
            onClick = { if (editorMode == EditorMode.SLIDES) onShowTheme() }
        )
        DesktopActionButton(
            icon = { Icon(Icons.Default.Share, contentDescription = "Share") },
            label = "Share",
            onClick = {}
        )
        DesktopActionButton(
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Present") },
            label = "Present",
            onClick = {}
        )
    }
}

private fun mergePresentationWithArc(
    presentation: Presentation,
    arc: Arc
): Presentation {
    val slidesByBlockId = presentation.arc.sections
        .flatMap { section -> section.blocks }
        .associateBy({ it.id }, { it.slides })

    val mergedSections = arc.sections.map { section ->
        section.copy(
            blocks = section.blocks.map { block ->
                block.copy(slides = slidesByBlockId[block.id] ?: block.slides)
            }
        )
    }

    return presentation.copy(arc = arc.copy(sections = mergedSections))
}

@Composable
private fun DesktopActionButton(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LantrTheme.extendedColors.toolbarButtonInactiveBackground,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        icon()
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DesktopPresentationEditor(
    viewModel: PresentationViewModel,
    arcViewModel: ArcViewModel,
    editorMode: EditorMode,
    inspectorShown: Boolean,
    onEditorModeChange: (EditorMode) -> Unit,
    onInspectorShownChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val inspectorTransition = updateTransition(
        targetState = inspectorShown,
        label = "desktopInspector"
    )
    val animatedInspectorWidth = inspectorTransition.animateDp(
        transitionSpec = {
            tween(
                durationMillis = InspectorAnimationDurationMs,
                easing = FastOutSlowInEasing
            )
        },
        label = "inspectorWidth"
    ) { shown ->
        if (shown) DesktopInspectorWidth else 0.dp
    }
    val inspectorAlpha = inspectorTransition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = InspectorAnimationDurationMs,
                easing = FastOutSlowInEasing
            )
        },
        label = "inspectorAlpha"
    ) { shown ->
        if (shown) 1f else 0f
    }
    val density = LocalDensity.current
    val fullInspectorWidthPx = with(density) { DesktopInspectorWidth.toPx() }

    Row(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = InspectorAnimationDurationMs,
                    easing = FastOutSlowInEasing
                )
            )
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            PresentationCanvasContent(
                viewModel = viewModel,
                arcViewModel = arcViewModel,
                editorMode = editorMode,
                onEditorModeChange = onEditorModeChange
            )
        }

        Box(
            modifier = Modifier
                .requiredWidth(animatedInspectorWidth.value)
                .fillMaxHeight()
                .clipToBounds()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .graphicsLayer {
                        val hiddenFraction = 1f - inspectorAlpha.value
                        translationX = hiddenFraction * fullInspectorWidthPx
                        alpha = inspectorAlpha.value
                    },
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 1.dp
            ) {
                if (animatedInspectorWidth.value > 0.dp) {
                    InspectorContainer(
                        editorMode = editorMode,
                        viewModel = viewModel,
                        onSlideUpdate = { updatedSlide -> viewModel.updateSelectedSlide(updatedSlide) },
                        showCloseButton = true,
                        onClose = { onInspectorShownChange(false) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MobilePresentationEditor(
    viewModel: PresentationViewModel,
    arcViewModel: ArcViewModel,
    editorMode: EditorMode,
    onEditorModeChange: (EditorMode) -> Unit,
    scaffoldState: androidx.compose.material3.BottomSheetScaffoldState,
    modifier: Modifier = Modifier
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 80.dp,
        sheetContent = {
            InspectorContainer(
                editorMode = editorMode,
                viewModel = viewModel,
                onSlideUpdate = { updatedSlide -> viewModel.updateSelectedSlide(updatedSlide) },
                showCloseButton = false,
                onClose = {},
                modifier = Modifier.fillMaxHeight(0.9f)
            )
        },
        modifier = modifier
    ) {
        PresentationCanvasContent(
            viewModel = viewModel,
            arcViewModel = arcViewModel,
            editorMode = editorMode,
            onEditorModeChange = onEditorModeChange
        )
    }
}

@Composable
private fun PresentationCanvasContent(
    viewModel: PresentationViewModel,
    arcViewModel: ArcViewModel,
    editorMode: EditorMode,
    onEditorModeChange: (EditorMode) -> Unit
) {
    when (editorMode) {
        EditorMode.SLIDES -> {
            val selectedSlide by viewModel.selectedSlide.collectAsState()
            val presentation by viewModel.presentation.collectAsState()
            val hasOverflow by viewModel.selectedSlideHasOverflow.collectAsState()

            selectedSlide?.let { slide ->
                SlideCanvasView(
                    presentationId = presentation.id.toString(),
                    slide = slide,
                    theme = presentation.theme,
                    hasOverflow = hasOverflow,
                    onSlideUpdate = { updatedSlide -> viewModel.updateSelectedSlide(updatedSlide) },
                    onEditorModeChange = { onEditorModeChange(EditorMode.ARC) }
                )
            }
        }

        EditorMode.ARC -> {
            ArcCanvasView(
                arcViewModel = arcViewModel,
                onEditorModeChange = { onEditorModeChange(EditorMode.SLIDES) }
            )
        }
    }
}
