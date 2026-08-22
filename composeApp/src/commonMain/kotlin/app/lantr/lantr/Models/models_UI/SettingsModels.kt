package app.lantr.lantr.Models.models_UI

import androidx.compose.ui.graphics.vector.ImageVector

// Tipurile de rânduri din listă
sealed interface SettingRow {
    val id: String
    val title: String
    val subtitle: String?
    val leadingIcon: ImageVector?

    data class Toggle(
        override val id: String,
        override val title: String,
        override val subtitle: String? = null,
        val checked: Boolean,
        val onToggle: (Boolean) -> Unit,
        override val leadingIcon: ImageVector? = null
    ) : SettingRow

    data class Navigation(
        override val id: String,
        override val title: String,
        override val subtitle: String? = null,
        val onClick: () -> Unit,
        override val leadingIcon: ImageVector? = null,
        val trailingText: String? = null
    ) : SettingRow

    data class Info(
        override val id: String,
        override val title: String,
        override val subtitle: String? = null,
        override val leadingIcon: ImageVector? = null,
        val trailingText: String? = null,
        val enabled: Boolean = false,
        val onClick: Any
    ) : SettingRow
}

// O secțiune ("General", "Editor" etc.)
data class SettingsSectionUI(
    val title: String,
    val items: List<SettingRow>
)

// Date pentru cardul de cont
data class AccountCardUI(
    val name: String,
    val subtitle: String,
    val onClick: () -> Unit
)

// Filtrare simplă după query (în titlu/subtitlu)
fun List<SettingsSectionUI>.filterByQuery(query: String): List<SettingsSectionUI> {
    if (query.isBlank()) return this
    val q = query.trim().lowercase()
    return mapNotNull { section ->
        val filtered = section.items.filter { row ->
            val t = row.title.lowercase()
            val s = row.subtitle?.lowercase() ?: ""
            t.contains(q) || s.contains(q)
        }
        if (filtered.isNotEmpty()) section.copy(items = filtered) else null
    }
}
