package app.lantr.lantr.Views.Presentations.Canvas.Base
/**
 * CanvasCard.kt
 * Lantr
 *
 * Created by Claude on 02.10.2025.
 * Converted from Swift to Kotlin
 */

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Theme.LantrTheme

/**
 * Reusable canvas card component that provides consistent styling and layout
 * for all canvas content areas
 */
@Composable
fun CanvasCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    shadowRadius: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier.shadow(
            elevation = shadowRadius,
            shape = RoundedCornerShape(cornerRadius)
        ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = LantrTheme.extendedColors.canvasCardSurface
        )
    ) {
        Box {
            content()
        }
    }
}

/**
 * Canvas card that automatically uses layout information from environment
 */
@Composable
fun AutoLayoutCanvasCard(
    config: CanvasConfiguration,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val layout = LocalCanvasLayout.current
    val totalWidth = layout.availableSize.width.dp +
            layout.margins.calculateLeftPadding(LayoutDirection.Ltr) +
            layout.margins.calculateRightPadding(LayoutDirection.Ltr)
    val totalHeight = layout.availableSize.height.dp +
            layout.margins.calculateTopPadding() +
            layout.margins.calculateBottomPadding()

    CanvasCard(
        modifier = modifier
            .size(width = totalWidth, height = totalHeight)
            .padding(horizontal = 10.dp),
        cornerRadius = config.cardCornerRadius,
        shadowRadius = config.cardShadowRadius,
        content = content
    )
}
