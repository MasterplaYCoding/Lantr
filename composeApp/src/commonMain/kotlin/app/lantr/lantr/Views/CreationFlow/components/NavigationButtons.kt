package app.lantr.lantr.Views.CreationFlow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.arrow_back
import app.lantr.lantr.generated.resources.arrow_forward
import app.lantr.lantr.generated.resources.check
import app.lantr.lantr.generated.resources.close
import org.jetbrains.compose.resources.painterResource

@Composable
fun NextButton(
    onClick: () -> Unit
) {
    val backgroundColor = LantrTheme.extendedColors.goldPrimary
    Row (
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(vertical = 8.dp, horizontal = 15.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.width(1.dp))

        Icon(painterResource(Res.drawable.arrow_forward), contentDescription = "", tint = LantrTheme.extendedColors.onGold)

        Spacer(modifier = Modifier.width(6.dp))

        Text(text = "Next", color = LantrTheme.extendedColors.onGold)
    }
}

@Composable
fun CancelButton(
    onClick: () -> Unit

) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp, horizontal = 15.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.width(1.dp))

        Icon(painterResource(Res.drawable.close), contentDescription = "", tint = LantrTheme.extendedColors.goldPrimary)

        Spacer(modifier = Modifier.width(6.dp))

        Text(text = "Cancel", color = LantrTheme.extendedColors.goldPrimary)
    }
}

@Composable
fun PreviousButton(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp, horizontal = 15.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.width(1.dp))

        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "", tint = LantrTheme.extendedColors.goldPrimary)

        Spacer(modifier = Modifier.width(6.dp))

        Text(text = "Previous", color = LantrTheme.extendedColors.goldPrimary)
    }
}

@Composable
fun BackButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(Res.drawable.arrow_back),
            contentDescription = "")
    }
}

@Composable
fun DoneButton(
    onClick: () -> Unit
) {
    val backgroundColor = LantrTheme.extendedColors.selectionIcon
    Row (
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(vertical = 8.dp, horizontal = 15.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.width(1.dp))

        Icon(painterResource(Res.drawable.check), contentDescription = "", tint = LantrTheme.extendedColors.onGold)

        Spacer(modifier = Modifier.width(6.dp))

        Text(text = "Next", color = LantrTheme.extendedColors.onGold)
    }
}