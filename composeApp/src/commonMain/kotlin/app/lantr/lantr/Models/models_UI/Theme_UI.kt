package app.lantr.lantr.Models.models_UI

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import app.lantr.lantr.Models.BlockType
import app.lantr.lantr.Models.Themes.RGBColor
import app.lantr.lantr.Models.Themes.Theme

fun Theme.fontSize(type: BlockType): Float? {
    if (type == BlockType.image) return null
    if (type == BlockType.presenterNotes) return textStyles[BlockType.body]?.fontSize
    return textStyles[type]?.fontSize
}

fun Theme.fontWeight(type: BlockType): FontWeight? {
    if (type == BlockType.image) return null
    if (type == BlockType.presenterNotes) return FontWeight.Normal

    val weight = textStyles[type]?.fontWeight ?: return null
    return when (weight) {
        app.lantr.lantr.Models.Themes.FontWeight.ultralight -> FontWeight.ExtraLight
        app.lantr.lantr.Models.Themes.FontWeight.thin -> FontWeight.Thin
        app.lantr.lantr.Models.Themes.FontWeight.light -> FontWeight.Light
        app.lantr.lantr.Models.Themes.FontWeight.regular -> FontWeight.Normal
        app.lantr.lantr.Models.Themes.FontWeight.medium -> FontWeight.Medium
        app.lantr.lantr.Models.Themes.FontWeight.semibold -> FontWeight.SemiBold
        app.lantr.lantr.Models.Themes.FontWeight.bold -> FontWeight.Bold
        app.lantr.lantr.Models.Themes.FontWeight.heavy -> FontWeight.ExtraBold
        app.lantr.lantr.Models.Themes.FontWeight.black -> FontWeight.Black
    }
}

fun Theme.fontFamily(type: BlockType): String? {
    if (type == BlockType.image || type == BlockType.presenterNotes) return null
    return textStyles[type]?.fontFamily
}

fun Theme.textColor(type: BlockType): Color {
    if (type == BlockType.presenterNotes) return Color.Gray
    val color = textStyles[type]?.textColor ?: return Color.Black
    return color.toColor()
}

val Theme.slideBackgroundColor: Color
    get() = backgroundColor.toColor()

fun Color.toRGBColor(): RGBColor {
    return RGBColor(red = this.red, green = this.green, blue = this.blue)
}

@Composable
fun BlockType.getSystemFont(): androidx.compose.ui.text.TextStyle {
    return when (this) {
        BlockType.h1 -> MaterialTheme.typography.displayLarge.copy(
            fontWeight = FontWeight.Bold
        )
        BlockType.h2 -> MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.SemiBold
        )
        BlockType.h3 -> MaterialTheme.typography.displaySmall.copy(
            fontWeight = FontWeight.Medium
        )
        BlockType.body -> MaterialTheme.typography.bodyLarge
        BlockType.quote -> MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Medium
        )
        BlockType.bulletList, BlockType.numberedList -> MaterialTheme.typography.bodyLarge
        BlockType.presenterNotes -> MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Normal
        )
        BlockType.image -> MaterialTheme.typography.bodyLarge  // Not used for images
    }
}

// Extension: Canvas text color (theme-independent)
val BlockType.canvasTextColor: Color
    @Composable
    get() = when (this) {
        BlockType.presenterNotes -> Color.Gray
        else -> MaterialTheme.colorScheme.onBackground  // Uses theme default
}
