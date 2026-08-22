package app.lantr.lantr.Views.mainMenu.mainScreen.screens.presentations.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Theme.LantrTheme
import org.jetbrains.compose.resources.painterResource
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.add
import app.lantr.lantr.generated.resources.transition_chop

@Composable
fun DefaultThumbnail(
    name: String,
    widthSize: Dp = 174.dp,
    onClick: () -> Unit = {}
) {
    Column (
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .height(92.dp)
                .width(widthSize)
                .clip(RoundedCornerShape(12.dp))
                .background(LantrTheme.extendedColors.thumbnailBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(Res.drawable.transition_chop),
                contentDescription = "add",
                tint = LantrTheme.extendedColors.secondaryText
            )
        }

        Spacer(Modifier.height(2.dp))

        Text(text = name)
    }
}