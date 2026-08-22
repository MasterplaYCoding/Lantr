package app.lantr.lantr.Views.DragAndDrop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

expect class DragAndDropManager() {
    fun createDragModifier(
        data: DragData,
        onDragStart: () -> Unit = {},
        onDragEnd: () -> Unit = {},
        dragPreview: (@Composable () -> Unit)? = null
    ): Modifier

    fun createDropModifier(
        onDrop: (DragData) -> Boolean,
        onDragEntered: () -> Unit = {},
        onDragExited: () -> Unit = {}
    ): Modifier

    @Composable
    fun currentDragData(): DragData?

    @Composable
    fun currentDragPosition(): androidx.compose.ui.geometry.Offset?

    @Composable
    fun DragOverlay()
}

/**
 * Remember a DragAndDropManager instance.
 */
@Composable
fun rememberDragAndDropManager(): DragAndDropManager {
    return remember { DragAndDropManager() }
}

data class DragData(
    val type: DragType,
    val id: String,
    val sourceIndex: Int,
    val payload: String // Additional data as needed
) {
    /**
     * Serializes this DragData to JSON string for transfer.
     */
    fun toJson(): String {
        // Escape payload string to handle quotes
        val escapedPayload = payload.replace("\"", "\\\"")
        return """{"type":"${type.name}","id":"$id","sourceIndex":$sourceIndex,"payload":"$escapedPayload"}"""
    }

    companion object {
        /**
         * Deserializes JSON string back to DragData.
         * Returns null if parsing fails.
         */
        fun fromJson(json: String): DragData? {
            return try {
                // Simple regex-based parsing
                val typeRegex = "\"type\":\"([^\"]+)\"".toRegex()
                val idRegex = "\"id\":\"([^\"]+)\"".toRegex()
                val indexRegex = "\"sourceIndex\":(\\d+)".toRegex()
                val payloadRegex = "\"payload\":\"((?:[^\"\\\\]|\\\\.)*)\"".toRegex()

                val typeMatch = typeRegex.find(json)?.groupValues?.get(1) ?: return null
                val idMatch = idRegex.find(json)?.groupValues?.get(1) ?: return null
                val indexMatch = indexRegex.find(json)?.groupValues?.get(1)?.toIntOrNull() ?: return null
                val payloadMatch = payloadRegex.find(json)?.groupValues?.get(1) ?: ""

                // Unescape the payload
                val unescapedPayload = payloadMatch.replace("\\\"", "\"")

                DragData(
                    type = DragType.valueOf(typeMatch),
                    id = idMatch,
                    sourceIndex = indexMatch,
                    payload = unescapedPayload
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

enum class DragType {
    CONTENT_BLOCK,
    SLIDE,
    STORY_BLOCK,
    STORY_SECTION
}
