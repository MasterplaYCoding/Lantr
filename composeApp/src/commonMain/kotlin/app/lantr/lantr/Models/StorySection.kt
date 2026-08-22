package app.lantr.lantr.Models

import androidx.compose.ui.graphics.Color
import app.lantr.lantr.Util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID
import app.lantr.lantr.Theme.StoryCategoryRed
import app.lantr.lantr.Theme.StoryCategoryBlue

@Serializable
data class StorySection(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    var type: StorySectionType,
    var blocks: List<StoryBlock> = emptyList()
)

enum class StorySectionType(
    val rawValue: String,
    val storySectionColor: Color
) {
    hook("Hook", StoryCategoryRed),
    stakes("Stakes", Color(0xff520B31)),
    core("Core", Color(0xff520B31)),
    turn("Turn", StoryCategoryBlue),
    audience("Audience", StoryCategoryBlue),
    close("Close", StoryCategoryRed);

    companion object {
        fun fromRawValue(value: String): StorySectionType? {
            val normalized = value.trim().lowercase()

            return when (normalized) {
                "hook" -> hook

                // legacy support
                "tension", "stakes" -> stakes

                "core" -> core

                // legacy support
                "turning", "turn" -> turn

                "audience" -> audience

                // legacy support
                "closing", "close" -> close

                else -> null
            }
        }
    }
}