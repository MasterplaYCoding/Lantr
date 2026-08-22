package app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.BubbleGold
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.IconGold

@Composable
fun IconBubble(
    icon: ImageVector,
    background: Color = BubbleGold,
    iconTint: Color = IconGold,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = iconTint)
    }
}
