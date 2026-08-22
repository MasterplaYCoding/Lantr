package app.lantr.lantr.Models

import app.lantr.lantr.Models.Themes.RGBColor
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class Slide(
    val id: String = UUID.randomUUID().toString(),
    var contentArrangement: ContentArrangement = ContentArrangement.top,
    var overrideColor: RGBColor? = null,
    var usesAltScheme: Boolean = false,
    val blocks: List<ContentBlock> = mutableListOf(
        TextBlock(content = "Welcome to Lantr", type = BlockType.h1),
        TextBlock(content = "Start creating your presentation", type = BlockType.body)
    )
)

enum class ContentArrangement {
    top, center, bottom
}
