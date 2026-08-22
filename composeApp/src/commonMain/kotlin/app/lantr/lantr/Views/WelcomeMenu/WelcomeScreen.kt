package app.lantr.lantr.Views.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.ui.components.GetStartedButton
import app.lantr.lantr.ui.theme.welcomeVignette

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onPrivacy: () -> Unit = {}
) {
    val cardBackground = Color(0xFF0B0B0B)
    val accent = Color(0xFFA5172E)

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .welcomeVignette()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(cardBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Transparent,
                                    0.55f to Color.Transparent,
                                    1.0f to Color.Black.copy(alpha = 0.67f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Welcome to",
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Lantr",
                        color = accent,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 36.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "The stage is yours.",
                        color = Color(0xFFEDEDED),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Make them feel something.",
                        color = accent,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            GetStartedButton(
                onClick = onGetStarted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF7B5446).copy(alpha = 0.35f))
                    .padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            AssistChip(
                onClick = onPrivacy,
                label = {
                    Text(
                        text = "Privacy & Terms",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                },
                border = BorderStroke(1.dp, Color(0xFF5D2B38)),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF5D2B38)
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
