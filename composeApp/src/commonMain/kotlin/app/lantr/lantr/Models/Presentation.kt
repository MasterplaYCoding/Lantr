package app.lantr.lantr.Models

import app.lantr.lantr.Models.Themes.Theme
import java.util.Date
import java.util.UUID

data class Presentation (
    val id: UUID = UUID.randomUUID(),
    var title: String = "New Presentation",
    var author: String = "User Name",
    var description: String = "",

    var duration: PresentationDuration = PresentationDuration.THIRTY_SECONDS,
    var audience: Audience = Audience(),
    val purpose: MutableList<PresentationPurpose> = mutableListOf(),

    var createdAt: Date = Date(),
    var modifiedAt: Date = Date(),

    var theme: Theme = Theme.standard,

    var arc: Arc = Arc(emptyList())
) {
    fun updateModifiedDate() {
        modifiedAt = Date()
    }
}