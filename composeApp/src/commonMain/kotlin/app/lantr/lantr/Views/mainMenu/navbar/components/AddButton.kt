package app.lantr.lantr.Views.mainMenu.navbar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.add
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddButton(
    onClick: () -> Unit = {}
) {
    if (PlatformUtil.isDesktop) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(LantrTheme.extendedColors.destructive)
                .clickable(onClick = onClick)
                .height(50.dp)
                .widthIn(min = 96.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.add),
                contentDescription = "Add",
                tint = LantrTheme.extendedColors.onGold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "New",
                color = LantrTheme.extendedColors.onGold,
                fontWeight = FontWeight.SemiBold
            )
        }
        return
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .width(50.dp)
            .height(50.dp)
            .background(LantrTheme.extendedColors.destructive)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.add),
            contentDescription = "add",
            tint = LantrTheme.extendedColors.onGold
        )
    }
}

@Composable
fun AddButtonLight(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(50.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(LantrTheme.extendedColors.destructive)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.add),
            contentDescription = "add",
            tint = LantrTheme.extendedColors.onGold
        )
    }
}
