package app.lantr.lantr.Views.Presentations.Inspector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.ViewModel.PresentationViewModel
import app.lantr.lantr.Views.Presentations.Inspector.Arc.ArcInspectorContentView
import app.lantr.lantr.Views.Presentations.Inspector.Slides.SlidesInspectorContentView
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.right_panel_close
import org.jetbrains.compose.resources.painterResource

enum class EditorMode {
    ARC, SLIDES
}

/**
 * Wrapper component for inspector content that can be used in both sidebar and bottom sheet contexts.
 *
 * @param editorMode Current editor mode (ARC or SLIDES)
 * @param viewModel Presentation view model
 * @param onSlideUpdate Callback when a slide is updated
 * @param showCloseButton Whether to show the close button (desktop sidebar only)
 * @param onClose Callback when close button is clicked
 * @param modifier Modifier for the container
 */
@Composable
fun InspectorContainer(
    editorMode: EditorMode,
    viewModel: PresentationViewModel,
    onSlideUpdate: (Slide) -> Unit,
    showCloseButton: Boolean = true,
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(LantrTheme.extendedColors.inspectorBackground)
    ) {
        // Desktop inspector header with close button
        if (showCloseButton && PlatformUtil.isDesktop) {
            Surface(
                modifier = Modifier.background(LantrTheme.extendedColors.inspectorBackground),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LantrTheme.extendedColors.inspectorBackground)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {

                    IconButton(onClick = onClose) {
                        Icon(
                            painterResource(Res.drawable.right_panel_close),
                            "Hide inspector",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Inspector content based on editor mode
        when (editorMode) {
            EditorMode.SLIDES -> {
                SlidesInspectorContentView(
                    viewModel = viewModel,
                    onSlideUpdate = onSlideUpdate
                )
            }
            EditorMode.ARC -> {
                ArcInspectorContentView(
                    viewModel = viewModel,
                    onSlideUpdate = onSlideUpdate
                )
            }
        }
    }
}
