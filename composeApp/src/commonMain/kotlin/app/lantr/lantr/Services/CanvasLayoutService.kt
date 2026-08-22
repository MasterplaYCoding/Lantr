package app.lantr.lantr.Services

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

/**
 * Layout decisions are made once here and shared with all content blocks for consistency.
 */
data class CanvasLayout(
    /**
     * Available space for content after subtracting UI margins
     * This is the actual drawable area for content blocks
     */
    val availableSize: Size,

    /**
     * UI margins reserved for interface elements:
     * - top: 36dp for topbar (eye icon, slide number)
     * - start: 0dp (no left UI elements)
     * - bottom: 10dp (small buffer)
     * - end: 54dp if wide layout, 0dp if compact layout
     */
    val margins: PaddingValues
) {
    /**
     * Whether we're using wide layout (controls on side) or compact layout (controls below)
     * Determined by whether trailing margin is reserved during layout calculation
     */
    val isUsingWideLayout: Boolean
        get() = margins.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr) > 0.dp
}

/**
 * Service responsible for calculating canvas layout dimensions
 *
 * RESPONSIBILITIES:
 * 1. Determine if screen size supports wide layout (side controls) vs compact (bottom controls)
 * 2. Calculate UI margins accordingly (reserve trailing space only if needed)
 * 3. Return available content space after subtracting margins
 *
 * LAYOUT SWITCHING LOGIC:
 * - Desktop: Wide layout when width > 600dp (aggressive, since more space available)
 * - Android: Wide layout when landscape AND width > 700dp (conservative, smaller screens)
 */
class CanvasLayoutService {

    /**
     * Simple layout calculation - uses all available space with basic UI margins
     * @param availableSize The available canvas size in pixels
     * @param density Screen density (pixels per dp). Pass LocalDensity.current.density from Compose
     * @param isDesktop Whether running on desktop platform
     */
    fun calculateLayout(availableSize: Size, density: Float = 1f, isDesktop: Boolean = false): CanvasLayout {
        // Convert thresholds from dp to pixels based on screen density
        val desktopThresholdPx = 600f * density
        val androidThresholdPx = 700f * density

        // Determine if we should use wide layout with same logic as ContentBlockView
        val shouldUseWideLayout = if (isDesktop) {
            // Desktop: Use wide layout more aggressively since we have more space
            availableSize.width > desktopThresholdPx
        } else {
            // Android: Only use wide layout on larger screens and landscape
            availableSize.width >= availableSize.height && availableSize.width > androidThresholdPx
        }

        // Simple UI margins - only reserve trailing space if actually using wide layout
        val margins = PaddingValues(
            top = 36.dp,     // Space for top bar used for information/alerts
            start = 0.dp,    // No left UI elements
            bottom = 10.dp,  // Small bottom buffer
            end = if (shouldUseWideLayout) 54.dp else 0.dp  // Control buttons area (wide layout only)
        )

        // Calculate available content space
        val marginHorizontal = margins.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr).value +
                margins.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr).value
        val marginVertical = margins.calculateTopPadding().value +
                margins.calculateBottomPadding().value

        val contentWidth = maxOf(100f, availableSize.width - marginHorizontal)
        val contentHeight = maxOf(100f, availableSize.height - marginVertical)

        return CanvasLayout(
            availableSize = Size(width = contentWidth, height = contentHeight),
            margins = margins
        )
    }
}