package app.lantr.lantr.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.min

fun Modifier.welcomeVignette(): Modifier = drawBehind {
    val dark = Color(0xFF25181E)
    val mid = Color(0xFF2E1F26)
    val light = Color(0xFF3C2A33)

    drawRect(dark)

    val center = Offset(size.width / 2f, size.height * 0.82f)
    val radius = min(size.width, size.height) * 1.35f

    drawRect(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to light,
                0.55f to mid,
                1.0f to dark
            ),
            center = center,
            radius = radius
        )
    )
}
