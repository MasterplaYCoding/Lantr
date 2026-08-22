package app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.models_UI.SettingRow
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.BgScreen
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.BubbleGold
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.CellBg
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.IconGold
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.SectionLabel
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.TitleOnBg
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.ToggleOffThumb
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.ToggleOffTrack
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.ToggleOnThumb
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.ToggleOnTrack
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.TrailingMuted

@Composable
fun SettingsSectionCard(
    items: List<SettingRow>,
    showChevron: Boolean = false,
    iconBubbleBackground: Color = BubbleGold,
    iconBubbleTint: Color = IconGold,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier
                .clip(shape)
                .background(CellBg)
        ) {
            items.forEachIndexed { index, row ->
                val isLast = index == items.lastIndex
                val interaction = remember { MutableInteractionSource() }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interaction,
                            indication = LocalIndication.current,
                            enabled = true,
                            onClick = {
                                when (row) {
                                    is SettingRow.Navigation -> row.onClick()
                                    is SettingRow.Info -> Unit
                                    is SettingRow.Toggle -> row.onToggle(!row.checked)
                                }
                            }
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.leadingIcon?.let {
                        IconBubble(icon = it, background = iconBubbleBackground, iconTint = iconBubbleTint)
                        Spacer(Modifier.width(12.dp))
                    }

                    Column(Modifier.weight(1f)) {
                        Text(row.title, style = MaterialTheme.typography.bodyLarge, color = TitleOnBg)
                        row.subtitle?.let {
                            Spacer(Modifier.height(2.dp))
                            Text(it, style = MaterialTheme.typography.bodySmall, color = SectionLabel)
                        }
                    }

                    when (row) {
                        is SettingRow.Toggle -> {
                            Switch(
                                checked = row.checked,
                                onCheckedChange = row.onToggle,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ToggleOnThumb,
                                    checkedTrackColor = ToggleOnTrack,
                                    uncheckedThumbColor = ToggleOffThumb,
                                    uncheckedTrackColor = ToggleOffTrack
                                )
                            )
                        }

                        is SettingRow.Info -> {
                            row.trailingText?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TrailingMuted
                                )
                            }
                        }

                        is SettingRow.Navigation -> {
                            row.trailingText?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TrailingMuted
                                )
                                Spacer(Modifier.width(6.dp))
                            }

                            if (showChevron) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = TitleOnBg
                                )
                            }
                        }
                    }
                }

                if (!isLast) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(BgScreen)
                    )
                }
            }
        }
    }
}
