package app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.models_UI.SettingRow
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.AvatarGold
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.AvatarIconGold
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.BubbleGold
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.CardBg
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.CellBg
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.CellBgSubtle
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.IconGold
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.SectionLabel
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.TitleOnBg
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.ToggleOffThumb
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.ToggleOffTrack
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.ToggleOnThumb
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.ToggleOnTrack
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.TrailingMuted

@Composable
fun AccountCell(
    name: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(CardBg)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(AvatarGold),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = AvatarIconGold,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        ProvideTextStyle(MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TitleOnBg
                )
                Text(text = subtitle, color = SectionLabel)
            }
        }
    }
}

@Composable
fun SettingToggleCell(
    item: SettingRow.Toggle,
    bubbleBg: Color = BubbleGold,
    bubbleTint: Color = IconGold,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CellBg)
            .clickable { item.onToggle(!item.checked) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item.leadingIcon?.let {
            IconBubble(icon = it, background = bubbleBg, iconTint = bubbleTint)
            Spacer(Modifier.width(12.dp))
        }

        ProvideTextStyle(MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)) {
            Column(Modifier.weight(1f)) {
                Text(item.title, color = TitleOnBg)
                item.subtitle?.let { Text(it, color = SectionLabel) }
            }
        }

        Switch(
            checked = item.checked,
            onCheckedChange = item.onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ToggleOnThumb,
                checkedTrackColor = ToggleOnTrack,
                uncheckedThumbColor = ToggleOffThumb,
                uncheckedTrackColor = ToggleOffTrack
            )
        )
    }
}

@Composable
fun SettingNavCell(
    item: SettingRow.Navigation,
    bubbleBg: Color = BubbleGold,
    bubbleTint: Color = IconGold,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = item.onClick)
            .background(CellBg)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item.leadingIcon?.let {
            IconBubble(icon = it, background = bubbleBg, iconTint = bubbleTint)
            Spacer(Modifier.width(12.dp))
        }

        Column(Modifier.weight(1f)) {
            Text(item.title, color = TitleOnBg)
            item.subtitle?.let { Text(it, color = SectionLabel) }
        }

        item.trailingText?.let {
            Text(it, color = TrailingMuted)
            Spacer(Modifier.width(6.dp))
        }

        Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = TitleOnBg)
    }
}

@Composable
fun SettingInfoCell(
    item: SettingRow.Info,
    bubbleBg: Color = BubbleGold,
    bubbleTint: Color = IconGold,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CellBgSubtle)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item.leadingIcon?.let {
            IconBubble(icon = it, background = bubbleBg, iconTint = bubbleTint)
            Spacer(Modifier.width(12.dp))
        }

        Column(Modifier.weight(1f)) {
            Text(item.title, color = TitleOnBg)
            item.subtitle?.let { Text(it, color = SectionLabel) }
        }

        item.trailingText?.let {
            Text(it, color = TrailingMuted)
        }
    }
}
