package app.lantr.lantr.Views.Presentations.Canvas

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.Themes.Theme
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.ViewModel.SlideViewModel
import app.lantr.lantr.Views.Presentations.Canvas.Base.BaseCanvasView
import app.lantr.lantr.Views.Presentations.Canvas.Base.CanvasConfiguration
import app.lantr.lantr.Views.Presentations.Canvas.Toolbar.SlideCanvasToolbar


@Composable
fun SlideCanvasView(
    presentationId: String,
    slide: Slide,
    theme: Theme,
    hasOverflow: Boolean = false,
    onSlideUpdate: (Slide) -> Unit = {},
    onDeleteSlide: () -> Unit = {},
    onEditorModeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val slideViewModel: SlideViewModel = remember {
        SlideViewModel(
            markdownParser = app.lantr.lantr.Services.MarkdownParser(),
            layoutService = app.lantr.lantr.Services.CanvasLayoutService()
        )
    }
    val focusedBlockId by slideViewModel.focusedContentBlockId.collectAsState()
    val canvasLayout by slideViewModel.canvasLayout.collectAsState()

    val config: CanvasConfiguration = remember { CanvasConfiguration.slide }
    val density = LocalDensity.current.density

    BaseCanvasView(
        config = config,
        onLayoutChange = { layout ->
            slideViewModel.updateCanvasLayout(
                availableSize = layout.availableSize,
                density = density,
                isDesktop = PlatformUtil.isDesktop
            )
        },
        toolbarContent = {
            SlideCanvasToolbar(
                presentationId = presentationId,
                slide = slide,
                theme = theme,
                onSlideChange = onSlideUpdate,
                deleteSlideAction = onDeleteSlide,
                viewModel = slideViewModel,
                onEditorModeChange = onEditorModeChange
            )
        },
        contentArea = {
            SlideCanvasContentView(
                presentationId = presentationId,
                slide = slide,
                hasOverflow = hasOverflow,
                onSlideUpdate = onSlideUpdate,
                slideViewModel = slideViewModel
            )
        },
        modifier = modifier
    )
}
