package app.lantr.lantr.Views.Presentations.Canvas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.lantr.lantr.ViewModel.ArcViewModel
import app.lantr.lantr.Views.Presentations.Canvas.Base.BaseCanvasView
import app.lantr.lantr.Views.Presentations.Canvas.Base.CanvasConfiguration
import app.lantr.lantr.Views.Presentations.Canvas.Toolbar.ArcCanvasToolbar

@Composable
fun ArcCanvasView(
    arcViewModel: ArcViewModel = viewModel(),
    onEditorModeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val arc by arcViewModel.arc.collectAsState()
    val config = remember { CanvasConfiguration.arc }

    BaseCanvasView(
        config = config,
        onLayoutChange = { layout ->
            // Arc canvas layout updates can be handled here if needed
        },
        toolbarContent = {
            ArcCanvasToolbar(
                arcViewModel = arcViewModel,
                arc = arc,
                onEditorModeChange = onEditorModeChange,
            )
        },
        contentArea = {
            ArcCanvasContentView(arcViewModel = arcViewModel, arc = arc)
        },
        modifier = modifier
    )
}