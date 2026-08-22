package app.lantr.lantr.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.lantr.lantr.ui.icons.AppIcons

private val MustardColor = Color(0xFFC79A06)

@Composable
fun GetStartedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MustardColor,
            contentColor = Color.White
        ),
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(0.72f),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = AppIcons.RecordVoiceOver,
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(text = "Get Started", fontWeight = FontWeight.W600)
    }
}
