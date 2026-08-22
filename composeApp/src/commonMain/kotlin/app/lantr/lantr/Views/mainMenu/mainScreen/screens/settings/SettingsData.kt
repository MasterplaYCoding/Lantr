package app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.KeyboardReturn
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.ui.graphics.vector.ImageVector
import app.lantr.lantr.Models.models_UI.SettingRow
import app.lantr.lantr.Models.models_UI.SettingsSectionUI
import app.lantr.lantr.ui.icons.AppIcons

data class AccountCard(
    val name: String,
    val subtitle: String,
    val onClick: () -> Unit
)

fun settingsAccountCard(onAccount: () -> Unit): AccountCard =
    AccountCard(
        name = "Boanca Raul",
        subtitle = "Account, Authorship & Subscription",
        onClick = onAccount
    )

fun settingsSections(
    haptic: Boolean,
    sounds: Boolean,
    onHapticToggle: (Boolean) -> Unit,
    onSoundsToggle: (Boolean) -> Unit,
    onEnterBehaviour: () -> Unit,
    onInterventionLevel: () -> Unit,
    onPersonalization: () -> Unit,
    onSmartFormatting: () -> Unit,
    onContactUs: () -> Unit,
    smartFormattingIcon: ImageVector
): List<SettingsSectionUI> {
    val general = SettingsSectionUI(
        title = "General",
        items = listOf(
            SettingRow.Toggle(
                id = "haptic",
                title = "Haptic Feedback",
                subtitle = null,
                checked = haptic,
                onToggle = onHapticToggle,
                leadingIcon = Icons.Outlined.Waves
            ),
            SettingRow.Toggle(
                id = "sound",
                title = "Sound Effects",
                subtitle = null,
                checked = sounds,
                onToggle = onSoundsToggle,
                leadingIcon = Icons.Filled.Radio
            )
        )
    )

    val editor = SettingsSectionUI(
        title = "Editor",
        items = listOf(
            SettingRow.Navigation(
                id = "smart_formatting",
                title = "Smart Formatting",
                subtitle = null,
                leadingIcon = AppIcons.Pilcrow,
                onClick = onSmartFormatting,
                trailingText = "Enabled"
            ),
            SettingRow.Navigation(
                id = "enter_behaviour",
                title = "Enter Behaviour",
                subtitle = null,
                onClick = onEnterBehaviour,
                leadingIcon = Icons.Outlined.KeyboardReturn,
                trailingText = null
            )
        )
    )

    val director = SettingsSectionUI(
        title = "The Director",
        items = listOf(
            SettingRow.Navigation(
                id = "intervention_level",
                title = "Intervention Level",
                subtitle = null,
                onClick = onInterventionLevel,
                leadingIcon = Icons.Outlined.Tune
            ),
            SettingRow.Navigation(
                id = "personalization",
                title = "Personalization",
                subtitle = null,
                onClick = onPersonalization,
                leadingIcon = Icons.Outlined.Person
            )
        )
    )

    val other = SettingsSectionUI(
        title = "Other",
        items = listOf(
            SettingRow.Navigation("privacy", "Privacy & Terms", null, onClick = {}, leadingIcon = Icons.Outlined.Description),
            SettingRow.Navigation("faq", "FAQ", null, onClick = {}, leadingIcon = Icons.Outlined.Help),
            SettingRow.Navigation("contact", "Contact Us", null, onClick = onContactUs, leadingIcon = Icons.Outlined.Mail),
            SettingRow.Navigation("experimental", "Experimental", null, onClick = {}, leadingIcon = Icons.Outlined.Science),
            SettingRow.Navigation("about", "About & Community", null, onClick = {}, leadingIcon = Icons.Outlined.Groups)
        )
    )

    return listOf(general, editor, director, other)
}
