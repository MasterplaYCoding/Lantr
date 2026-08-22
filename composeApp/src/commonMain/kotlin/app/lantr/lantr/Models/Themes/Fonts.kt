package app.lantr.lantr.Models.Themes

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight as ComposeFontWeight
import androidx.compose.ui.text.TextStyle as ComposeTextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

enum class VariableFont(
    val fontName: String,
    val displayName: String,
    val hasItalic: Boolean,
    val italicName: String? = null,
    val minFontWeight: FontWeight,
    val maxFontWeight: FontWeight
) {
    INTER(
        fontName = "Inter-Regular",
        displayName = "Inter",
        hasItalic = true,
        italicName = "Inter-Italic",
        minFontWeight = FontWeight.ultralight,
        maxFontWeight = FontWeight.black
    ),

    WORK_SANS(
        fontName = "WorkSans-Regular",
        displayName = "Work Sans",
        hasItalic = true,
        italicName = "WorkSans-Italic",
        minFontWeight = FontWeight.ultralight,
        maxFontWeight = FontWeight.black
    ),

    URBANIST(
        fontName = "Urbanist-Regular",
        displayName = "Urbanist",
        hasItalic = true,
        italicName = "Urbanist-Italic",
        minFontWeight = FontWeight.ultralight,
        maxFontWeight = FontWeight.black
    ),

    LORA(
        fontName = "Lora-Regular",
        displayName = "Lora",
        hasItalic = true,
        italicName = "Lora-Italic",
        minFontWeight = FontWeight.regular,
        maxFontWeight = FontWeight.bold
    ),

    MERRIWEATHER(
        fontName = "Merriweather-Light",
        displayName = "Merriweather",
        hasItalic = true,
        italicName = "Merriweather-LightItalic",
        minFontWeight = FontWeight.light,
        maxFontWeight = FontWeight.black
    ),

    MUSEO_MODERNO(
        fontName = "MuseoModerno-Thin",
        displayName = "Museo Moderno",
        hasItalic = true,
        italicName = "MuseoModerno-ThinItalic",
        minFontWeight = FontWeight.thin,
        maxFontWeight = FontWeight.black
    ),

    PLAYFAIR_DISPLAY(
        fontName = "PlayfairDisplay-Regular",
        displayName = "Playfair Display",
        hasItalic = true,
        italicName = "PlayfairDisplay-Italic",
        minFontWeight = FontWeight.medium,
        maxFontWeight = FontWeight.black
    ),

    PLAYWRITE_IE(
        fontName = "Playwrite IE",
        displayName = "Playwrite IE",
        hasItalic = false,
        italicName = null,
        minFontWeight = FontWeight.ultralight,
        maxFontWeight = FontWeight.regular
    ),

    TEXTURINA(
        fontName = "Texturina",
        displayName = "Texturina",
        hasItalic = true,
        italicName = "Texturina-MediumItalic",
        minFontWeight = FontWeight.thin,
        maxFontWeight = FontWeight.black
    ),

    JETBRAINS_MONO(
        fontName = "JetBrainsMono-Regular",
        displayName = "JetBrains Mono",
        hasItalic = true,
        italicName = "JetBrainsMono-Italic",
        minFontWeight = FontWeight.ultralight,
        maxFontWeight = FontWeight.heavy
    ),

    SPACE_GROTESK(
        fontName = "Space Grotesk",
        displayName = "Space Grotesk",
        hasItalic = false,
        italicName = null,
        minFontWeight = FontWeight.light,
        maxFontWeight = FontWeight.bold
    ),

    NUNITO(
        fontName = "Nunito",
        displayName = "Nunito",
        hasItalic = true,
        italicName = "Nunito-ExtraLightItalic",
        minFontWeight = FontWeight.thin,
        maxFontWeight = FontWeight.black
    ),

    POPPINS(
        fontName = "Poppins Devanagari",
        displayName = "Poppins",
        hasItalic = true,
        italicName = "PoppinsDevanagari-ThinItalic",
        minFontWeight = FontWeight.ultralight,
        maxFontWeight = FontWeight.black
    );

    companion object {
        fun fromDisplayName(name: String): VariableFont? {
            return values().firstOrNull {
                it.displayName.equals(name, ignoreCase = true)
            }
        }
    }
}

/*
// FontWeight enum matching Swift's FontWeight
enum class FontWeight(val value: Int) {
    ULTRALIGHT(100),
    THIN(200),
    LIGHT(300),
    REGULAR(400),
    MEDIUM(500),
    SEMIBOLD(600),
    BOLD(700),
    HEAVY(800),
    BLACK(900);

    companion object {
        fun fromValue(value: Int): FontWeight {
            return values().minByOrNull {
                kotlin.math.abs(it.value - value)
            } ?: REGULAR
        }
    }
}*/

// Extension function to create variable font
fun VariableFont.getTextStyle(
    weight: FontWeight,
    size: TextUnit,
    italic: Boolean = false
): ComposeTextStyle {
    val clampedWeight = clampWeight(weight, this)
    val fontNameToUse = if (italic && hasItalic) italicName else fontName

    // Get the font family (you'll need to load these fonts in your app)
    val fontFamily = getFontFamily(fontNameToUse ?: fontName)

    return ComposeTextStyle(
        fontFamily = fontFamily,
        fontWeight = clampedWeight.toComposeFontWeight(),
        fontSize = size,
        fontStyle = if (italic && hasItalic) FontStyle.Italic else FontStyle.Normal
    )
}

// Helper function to clamp weight within font's supported range
private fun clampWeight(weight: FontWeight, font: VariableFont): FontWeight {
    val minWeight = font.minFontWeight.value
    val maxWeight = font.maxFontWeight.value
    val clampedValue = max(minWeight, min(maxWeight, weight.value))

    return FontWeight.fromValue(clampedValue) ?: weight
}

// Convert custom FontWeight to Compose FontWeight
fun FontWeight.toComposeFontWeight(): ComposeFontWeight {
    return when (this) {
        FontWeight.ultralight -> ComposeFontWeight.ExtraLight
        FontWeight.thin -> ComposeFontWeight.Thin
        FontWeight.light -> ComposeFontWeight.Light
        FontWeight.regular -> ComposeFontWeight.Normal
        FontWeight.medium -> ComposeFontWeight.Medium
        FontWeight.semibold -> ComposeFontWeight.SemiBold
        FontWeight.bold -> ComposeFontWeight.Bold
        FontWeight.heavy -> ComposeFontWeight.ExtraBold
        FontWeight.black -> ComposeFontWeight.Black
    }
}

// Helper to get FontFamily (you'll need to define these based on your loaded fonts)
private fun getFontFamily(fontName: String): FontFamily {
    // This is a placeholder - you'll need to load actual fonts
    // and create FontFamily instances for each font
    return when {
        fontName.startsWith("Inter") -> AppFonts.Inter
        fontName.startsWith("WorkSans") -> AppFonts.WorkSans
        fontName.startsWith("Urbanist") -> AppFonts.Urbanist
        fontName.startsWith("Lora") -> AppFonts.Lora
        fontName.startsWith("Merriweather") -> AppFonts.Merriweather
        fontName.startsWith("MuseoModerno") -> AppFonts.MuseoModerno
        fontName.startsWith("PlayfairDisplay") -> AppFonts.PlayfairDisplay
        fontName.startsWith("JetBrainsMono") -> AppFonts.JetBrainsMono
        fontName.contains("Space Grotesk") -> AppFonts.SpaceGrotesk
        fontName.contains("Texturina") -> AppFonts.Texturina
        fontName.contains("Playwrite") -> AppFonts.PlaywriteIE
        fontName.contains("Nunito") -> AppFonts.Nunito
        fontName.contains("Poppins") -> AppFonts.Poppins
        else -> FontFamily.Default
    }
}

// Define your font families (you need to add font files to resources)
object AppFonts {
    val Inter = FontFamily.Default // Replace with actual font loading
    val WorkSans = FontFamily.SansSerif
    val Urbanist = FontFamily.SansSerif
    val Lora = FontFamily.Serif
    val Merriweather = FontFamily.Serif
    val MuseoModerno = FontFamily.SansSerif
    val PlayfairDisplay = FontFamily.Serif
    val JetBrainsMono = FontFamily.Monospace
    val SpaceGrotesk = FontFamily.Monospace
    val Texturina = FontFamily.Serif
    val PlaywriteIE = FontFamily.Cursive
    val Nunito = FontFamily.SansSerif
    val Poppins = FontFamily.SansSerif
}