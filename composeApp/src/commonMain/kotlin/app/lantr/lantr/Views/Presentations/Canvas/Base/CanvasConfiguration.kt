package app.lantr.lantr.Views.Presentations.Canvas.Base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Util.PlatformUtil

/**
 * Configuration object that centralizes all canvas layout constants
 * This replaces hardcoded values scattered throughout the canvas system
 */
data class CanvasConfiguration(
    // MARK: - Toolbar Configuration

    /** Height of the canvas dynamic toolbar */
    val toolbarHeight: Dp,

    /** Spacing between toolbar and content area */
    val toolbarSpacing: Dp,

    // MARK: - Canvas Padding

    /** Overall padding around the entire canvas */
    val canvasPadding: PaddingValues,

    /** Horizontal padding applied to content within the canvas card */
    val contentHorizontalPadding: Dp,

    // MARK: - Card Appearance

    /** Corner radius for the main canvas card */
    val cardCornerRadius: Dp,

    /** Shadow radius for the canvas card */
    val cardShadowRadius: Dp,

    // MARK: - Content Layout

    /** Padding around individual content blocks */
    val blockPadding: PaddingValues,

    /** Additional bottom padding for scrollable content (keyboard/inspector space) */
    val scrollContentBottomPadding: Dp
) {
    companion object {
        // MARK: - Platform-Specific Configurations

        /** Configuration for slide editing canvas */
        val slide: CanvasConfiguration by lazy {
            if (PlatformUtil.isDesktop) {
                CanvasConfiguration(
                    toolbarHeight = 46.dp,
                    toolbarSpacing = 12.dp,
                    canvasPadding = PaddingValues(
                        top = 10.dp,
                        start = 0.dp,
                        bottom = 10.dp,
                        end = 0.dp
                    ),
                    contentHorizontalPadding = 10.dp,
                    cardCornerRadius = 24.dp,
                    cardShadowRadius = 4.dp,
                    blockPadding = PaddingValues(
                        top = 8.dp,
                        start = 24.dp,
                        bottom = 8.dp,
                        end = 24.dp
                    ),
                    scrollContentBottomPadding = 0.dp
                )
            } else {
                CanvasConfiguration(
                    toolbarHeight = 52.dp,
                    toolbarSpacing = 12.dp,
                    canvasPadding = PaddingValues(
                        top = 2.dp,
                        start = 0.dp,
                        bottom = 12.dp,
                        end = 0.dp
                    ),
                    contentHorizontalPadding = 10.dp,
                    cardCornerRadius = 24.dp,
                    cardShadowRadius = 4.dp,
                    blockPadding = PaddingValues(
                        top = 8.dp,
                        start = 24.dp,
                        bottom = 8.dp,
                        end = 24.dp
                    ),
                    scrollContentBottomPadding = 120.dp  // Extra space for inspector on Android
                )
            }
        }

        /** Configuration for arc overview canvas */
        val arc: CanvasConfiguration by lazy {
            if (PlatformUtil.isDesktop) {
                CanvasConfiguration(
                    toolbarHeight = 46.dp,
                    toolbarSpacing = 12.dp,
                    canvasPadding = PaddingValues(
                        top = 10.dp,
                        start = 0.dp,
                        bottom = 10.dp,
                        end = 0.dp
                    ),
                    contentHorizontalPadding = 10.dp,
                    cardCornerRadius = 24.dp,
                    cardShadowRadius = 4.dp,
                    blockPadding = PaddingValues(
                        top = 8.dp,
                        start = 24.dp,
                        bottom = 8.dp,
                        end = 24.dp
                    ),
                    scrollContentBottomPadding = 0.dp
                )
            } else {
                CanvasConfiguration(
                    toolbarHeight = 52.dp,
                    toolbarSpacing = 12.dp,
                    canvasPadding = PaddingValues(
                        top = 2.dp,
                        start = 0.dp,
                        bottom = 12.dp,
                        end = 0.dp
                    ),
                    contentHorizontalPadding = 10.dp,
                    cardCornerRadius = 24.dp,
                    cardShadowRadius = 4.dp,
                    blockPadding = PaddingValues(
                        top = 8.dp,
                        start = 24.dp,
                        bottom = 8.dp,
                        end = 24.dp
                    ),
                    scrollContentBottomPadding = 0.dp
                )
            }
        }
    }
}

// MARK: - Helper Extensions

/** Total vertical space consumed by non-content UI elements */
val CanvasConfiguration.totalUIVerticalSpace: Dp
    get() = toolbarHeight + toolbarSpacing +
            canvasPadding.calculateTopPadding() +
            canvasPadding.calculateBottomPadding()

/** Total horizontal space consumed by padding */
val CanvasConfiguration.totalUIHorizontalSpace: Dp
    get() = canvasPadding.calculateLeftPadding(LayoutDirection.Ltr) +
            canvasPadding.calculateRightPadding(LayoutDirection.Ltr) +
            (contentHorizontalPadding * 2)

// PaddingValues Extensions

val PaddingValues.horizontal: Dp
    get() = calculateLeftPadding(LayoutDirection.Ltr) +
            calculateRightPadding(LayoutDirection.Ltr)

val PaddingValues.vertical: Dp
    get() = calculateTopPadding() + calculateBottomPadding()