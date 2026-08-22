package app.lantr.lantr.Models.models_transferable

import app.lantr.lantr.Models.BlockType
import app.lantr.lantr.Util.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Custom transferable type for content block drag and drop operations
 * Ensures content blocks can only be dropped within the same slide canvas
 */
@Serializable
data class ContentBlockTransferable(
    /**
     * The unique identifier of the content block being transferred
     */
    @Serializable(with = UUIDSerializer::class)
    val blockId: UUID,

    /**
     * The slide containing this content block for context during reordering
     */
    @Serializable(with = UUIDSerializer::class)
    val sourceSlideId: UUID,

    /**
     * The position of the content block within its slide
     */
    val sourceBlockIndex: Int,

    /**
     * The type of content block for validation during drops
     */
    val blockType: BlockType
) {
    companion object {
        const val MIME_TYPE = "application/vnd.lantr.contentblock"

        private val json = Json { ignoreUnknownKeys = true }

        /**
         * Creates a transferable representation of a content block
         */
        fun create(
            blockId: UUID,
            sourceSlideId: UUID,
            sourceBlockIndex: Int,
            blockType: BlockType
        ): ContentBlockTransferable {
            return ContentBlockTransferable(
                blockId = blockId,
                sourceSlideId = sourceSlideId,
                sourceBlockIndex = sourceBlockIndex,
                blockType = blockType
            )
        }

        /**
         * Decodes a ContentBlockTransferable from JSON string
         */
        fun fromJson(jsonString: String): ContentBlockTransferable? {
            return try {
                json.decodeFromString<ContentBlockTransferable>(jsonString)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Converts this transferable to JSON string
     */
    fun toJson(): String {
        return json.encodeToString(this)
    }

    /*
    // UUID accessors for convenience
    val blockUUID: UUID
        get() = UUID.fromString(blockId)

    val sourceSlideUUID: UUID
        get() = UUID.fromString(sourceSlideId)

     */
}