package app.lantr.lantr.ui.adaptive

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.WindowHeightSizeClass
import app.lantr.lantr.Models.WindowSizeClass
import app.lantr.lantr.Models.WindowWidthSizeClass

// INFO expus tuturor ecranelor
data class WindowInfo(
    val widthDp: Int,
    val heightDp: Int,
    val isLandscape: Boolean,
    val sizeClass: WindowSizeClass
)

val LocalWindowInfo = staticCompositionLocalOf<WindowInfo> {
    error("LocalWindowInfo not provided")
}
/** KMP-safe: obținem mărimile cu BoxWithConstraints (merge Android/Desktop/iOS) */
@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun ProvideWindowInfo(content: @Composable () -> Unit) {
    BoxWithConstraints {
        val w = maxWidth.value.toInt()
        val h = maxHeight.value.toInt()
        val isLandscape = w > h

        val widthClass = when {
            w < 600 -> WindowWidthSizeClass.Compact
            w < 840 -> WindowWidthSizeClass.Medium
            else    -> WindowWidthSizeClass.Expanded
        }
        val heightClass = when {
            h < 480 -> WindowHeightSizeClass.Compact
            h < 900 -> WindowHeightSizeClass.Medium
            else    -> WindowHeightSizeClass.Expanded
        }

        val info = remember(w, h) {
            WindowInfo(
                widthDp = w,
                heightDp = h,
                isLandscape = isLandscape,
                sizeClass = WindowSizeClass(widthClass, heightClass)
            )
        }
        CompositionLocalProvider(LocalWindowInfo provides info) {
            content()
        }
    }
}

// Padding uniform în funcție de clasa de lățime/orientare
@Composable
fun horizontalScreenPadding(): Dp = when (LocalWindowInfo.current.sizeClass.widthSizeClass) {
    WindowWidthSizeClass.Compact  -> 16.dp
    WindowWidthSizeClass.Medium   -> 24.dp
    WindowWidthSizeClass.Expanded -> 32.dp
}
@Composable
fun verticalScreenPadding(): Dp =
    if (LocalWindowInfo.current.isLandscape) 16.dp else 24.dp

/** Wrapper 2-panels: Portrait→Column, Landscape (non-Compact)→Row */
@Composable
fun AdaptiveTwoPane(
    modifier: Modifier = Modifier,
    primaryPane: @Composable ColumnScope.() -> Unit,   // sus / stânga
    secondaryPane: @Composable ColumnScope.() -> Unit, // jos / dreapta
    verticalAlignmentLandscape: Alignment.Vertical = Alignment.Top,
    weightPrimary: Float = 1f,
    weightSecondary: Float = 1f,
) {
    val wi = LocalWindowInfo.current
    val hPad = horizontalScreenPadding()
    val vPad = verticalScreenPadding()
    val isTwoPane = wi.isLandscape && wi.sizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    AnimatedContent(targetState = isTwoPane, label = "adaptive_two_pane_switch") { twoPane ->
        if (twoPane) {
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = hPad, vertical = vPad),
                verticalAlignment = verticalAlignmentLandscape
            ) {
                Column(Modifier.weight(weightPrimary).padding(end = 12.dp)) { primaryPane() }
                Column(Modifier.weight(weightSecondary)) { secondaryPane() }
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = hPad, vertical = vPad)
            ) {
                primaryPane()
                Spacer(Modifier.height(16.dp))
                secondaryPane()
            }
        }
    }
}

/** Wrapper generic: Portrait→Column, Landscape (non-Compact)→Row */
// Adaptive.kt
@Composable
fun AdaptiveScreen(
    modifier: Modifier = Modifier,
    includeDefaultPadding: Boolean = true, // <— nou
    portraitContent: @Composable ColumnScope.() -> Unit,
    landscapeContent: @Composable RowScope.() -> Unit
) {
    val wi = LocalWindowInfo.current
    val withPad = if (includeDefaultPadding) {
        modifier
            .padding(horizontal = horizontalScreenPadding(), vertical = verticalScreenPadding())
    } else {
        modifier
    }

    val isLandscape = wi.isLandscape // nu mai băgăm paddinguri implicite aici

    AnimatedContent(targetState = isLandscape, label = "adaptive_screen_switch") { land ->
        if (land) {
            Row(modifier = withPad, content = landscapeContent)
        } else {
            Column(modifier = withPad, content = portraitContent)
        }
    }
}
