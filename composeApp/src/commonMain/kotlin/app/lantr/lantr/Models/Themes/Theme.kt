package app.lantr.lantr.Models.Themes

import androidx.compose.ui.graphics.Color
import app.lantr.lantr.Models.BlockType
import kotlinx.serialization.Serializable

@Serializable
data class Theme(
    val contentMargins: Float = 50f,
    val elementSpacing: Float = 10f,
    val altBackgroundColor: RGBColor= RGBColor("000000"),
    val backgroundColor: RGBColor = RGBColor("FFFFFF"),
    val textStyles: Map<BlockType, TextStyle> = emptyMap()
) {
    companion object {
        val defaultTextStyles: Map<BlockType, TextStyle> = buildMap {
            put(
                BlockType.h1, TextStyle(
                fontSize = 84f,
                fontWeight = FontWeight.bold,
                fontFamily = null,
                textColor = RGBColor(red = 0f, green = 0f, blue = 0f),
                    altTextColor = RGBColor("FFFFFF")
            ))
            put(
                BlockType.h2, TextStyle(
                fontSize = 64f,
                fontWeight = FontWeight.bold,
                fontFamily = null,
                textColor = RGBColor(red = 0f, green = 0f, blue = 0f),
                    altTextColor = RGBColor("FFFFFF")
            ))
            put(
                BlockType.h3, TextStyle(
                fontSize = 54f,
                fontWeight = FontWeight.semibold,
                fontFamily = null,
                textColor = RGBColor(red = 0f, green = 0f, blue = 0f),
                    altTextColor = RGBColor("FFFFFF")
            ))
            put(
                BlockType.body, TextStyle(
                fontSize = 38f,
                fontWeight = FontWeight.regular,
                fontFamily = null,
                textColor = RGBColor(red = 0f, green = 0f, blue = 0f),
                    altTextColor = RGBColor("FFFFFF")
            ))
            put(
                BlockType.quote, TextStyle(
                fontSize = 42f,
                fontWeight = FontWeight.medium,
                fontFamily = null,
                textColor = RGBColor(red = 0.69f, green = 0.08f, blue = 0.2f),
                    altTextColor = RGBColor("FFFFFF")
            ))
            put(
                BlockType.bulletList, TextStyle(
                fontSize = 38f,
                fontWeight = FontWeight.regular,
                fontFamily = null,
                textColor = RGBColor(red = 0f, green = 0f, blue = 0f),
                    altTextColor = RGBColor("FFFFFF")
            ))
            put(
                BlockType.numberedList, TextStyle(
                fontSize = 38f,
                fontWeight = FontWeight.regular,
                fontFamily = null,
                textColor = RGBColor(red = 0f, green = 0f, blue = 0f),
                    altTextColor = RGBColor("FFFFFF")
            ))
        }

        val standard = Theme(textStyles = defaultTextStyles)

        fun predefined(type: PredefinedThemes): Theme {
            return Theme(
                contentMargins = type.contentMargins.value,
                elementSpacing = type.elementSpacing.value,
                altBackgroundColor = type.altBackgroundColor,
                backgroundColor = type.backgroundColor,
                textStyles = type.textStyles
            )
        }
    }
}

@Serializable
data class TextStyle(
    val fontSize: Float,
    val fontWeight: FontWeight,
    val fontFamily: String? = null,
    val textColor: RGBColor,
    val altTextColor: RGBColor
)

enum class FontWeight(val value: Int) {
    ultralight(100), thin(200), light(300),
    regular(400), medium(500), semibold(600),
    bold(700), heavy(800), black(900);

    companion object {
        fun fromValue(value: Int): FontWeight? {
            return entries.toTypedArray().minByOrNull {
                kotlin.math.abs(it.value - value)
            }
        }
    }
}

@Serializable
data class RGBColor(
    val red: Float,
    val green: Float,
    val blue: Float
) {
    // Secondary constructor for RGB values (0-255 range)
    constructor(r: Int, g: Int, b: Int) : this(
        red = r / 255f,
        green = g / 255f,
        blue = b / 255f
    )

    // Secondary constructor for hex string
    constructor(hex: String) : this(
        red = parseHexComponent(hex, 0),
        green = parseHexComponent(hex, 2),
        blue = parseHexComponent(hex, 4)
    )

    fun toColor(): Color {
        return Color(red, green, blue)
    }

    fun toHexString(): String {
        val r = (red * 255).toInt().coerceIn(0, 255)
        val g = (green * 255).toInt().coerceIn(0, 255)
        val b = (blue * 255).toInt().coerceIn(0, 255)
        return "%02X%02X%02X".format(r, g, b)
    }

    companion object {
        private fun parseHexComponent(hex: String, offset: Int): Float {
            val hexSanitized = hex.trim()
                .removePrefix("#")
                .uppercase()

            return try {
                val component = hexSanitized.substring(offset, offset + 2)
                component.toInt(16) / 255f
            } catch (e: Exception) {
                0f
            }
        }
    }
}

enum class ContentMargins(val value: Float) {
    small(80.0f),
    medium(110.0f),
    large(130.0f)
}

enum class ElementSpacing(val value: Float) {
    small(24.0f),
    medium(36.0f),
    large(48.0f),
    extraLarge(60.0f)
}