package app.lantr.lantr.Models

import androidx.compose.ui.Alignment
import app.lantr.lantr.Models.Themes.RGBColor
import app.lantr.lantr.Util.UUIDSerializer
import java.util.UUID
import kotlinx.serialization.Serializable

interface ContentBlock {
    val id: UUID
    val type: BlockType
    var contentAlignment: ContentAlignment
}
@Serializable
data class TextBlock(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID = UUID.randomUUID(),
    override val type: BlockType = BlockType.body,
    override var contentAlignment: ContentAlignment = ContentAlignment.leading,
    var content: String = "",
    var overrideColor: RGBColor? = null
) : ContentBlock

@Serializable
data class ImageBlock(
    @Serializable(with = UUIDSerializer::class)
    override val id: UUID = UUID.randomUUID(),
    override val type: BlockType = BlockType.image,
    override var contentAlignment: ContentAlignment = ContentAlignment.center,

    var imagePath: String = "",
    var imageHeight: Float,
    var cornerRadius: Float = 0f
) : ContentBlock

enum class ContentAlignment(val boxAlignment: Alignment) {
    leading(Alignment.CenterStart),
    center(Alignment.Center),
    trailing(Alignment.CenterEnd)
}

enum class BlockType {
    h1, h2, h3, body, quote, bulletList, numberedList, presenterNotes, image;
/*
    fun isListType(): Boolean {
        return this == bulletList || this == numberedList
    }*/
}
