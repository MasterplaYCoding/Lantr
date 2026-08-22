package app.lantr.lantr.Views.Presentations.Canvas.Base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import app.lantr.lantr.Services.CanvasLayout
import app.lantr.lantr.Services.CanvasLayoutService
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Util.PlatformUtil
import kotlin.math.max

// Define LocalCanvasLayout at module level so it can be accessed by all composables
val LocalCanvasLayout = compositionLocalOf<CanvasLayout> {
    error("No CanvasLayout provided")
}

@Composable
fun BaseCanvasView(
    config: CanvasConfiguration,
    onLayoutChange: (CanvasLayout) -> Unit = {},
    toolbarContent: @Composable () -> Unit,
    contentArea: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {

    val layoutService = remember { CanvasLayoutService() }
    var layout by remember { mutableStateOf<CanvasLayout?>(null) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LantrTheme.extendedColors.canvasGradientCenter,
                        LantrTheme.extendedColors.canvasGradientEdge
                    )
                )
            )
    ) {

        val w = this.maxWidth
        val h = this.maxHeight

        val density = LocalDensity.current.density
        val sizePx = with(LocalDensity.current) {
            Size(w.toPx(), h.toPx())
        }

        LaunchedEffect(sizePx) {
            layout = layoutService.calculateLayout(
                availableSize = sizePx,
                density = density,
                isDesktop = PlatformUtil.isDesktop
            )
        }

        layout?.let { currentLayout ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(config.canvasPadding)
            ) {
                // Toolbar
                Box(
                    modifier = Modifier.height(config.toolbarHeight)
                ) {
                    toolbarContent()
                }

                Spacer(modifier = Modifier.height(config.toolbarSpacing))

                // Content Area
                CompositionLocalProvider(
                    LocalCanvasLayout provides currentLayout
                ) {
                    contentArea()
                }
            }
        }
    }
}

@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}