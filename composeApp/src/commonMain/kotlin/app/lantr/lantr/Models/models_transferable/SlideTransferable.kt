package app.lantr.lantr.Models.models_transferable

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Custom transferable type for slide drag and drop operations
 * Ensures slides can only be dropped within the slides timeline
 */
@Serializable
data class SlideTransferable(
    /**
     * The unique identifier of the slide being transferred
     */
    val slideId: String,

    /**
     * The story block containing this slide for context during reordering
     */
    val sourceBlockId: String,

    /**
     * The position of the slide within its story block
     */
    val sourceSlideIndex: Int
) {
    /**
     * Converts this transferable to JSON string
     */
    fun toJson(): String {
        return Json.encodeToString(this)
    }

    // UUID accessors for convenience
    val slideUUID: UUID
        get() = UUID.fromString(slideId)

    val sourceBlockUUID: UUID
        get() = UUID.fromString(sourceBlockId)

    companion object {
        const val MIME_TYPE = "application/vnd.lantr.slide"

        /**
         * Creates a transferable representation of a slide
         * @param slideId The unique identifier of the slide
         * @param sourceBlockId The story block containing this slide
         * @param sourceSlideIndex The position within the story block
         */
        fun create(
            slideId: UUID,
            sourceBlockId: UUID,
            sourceSlideIndex: Int
        ): SlideTransferable {
            return SlideTransferable(
                slideId = slideId.toString(),
                sourceBlockId = sourceBlockId.toString(),
                sourceSlideIndex = sourceSlideIndex
            )
        }

        /**
         * Decodes a SlideTransferable from JSON string
         */
        fun fromJson(jsonString: String): SlideTransferable? {
            return try {
                Json.decodeFromString<SlideTransferable>(jsonString)
            } catch (e: Exception) {
                null
            }
        }
    }
}