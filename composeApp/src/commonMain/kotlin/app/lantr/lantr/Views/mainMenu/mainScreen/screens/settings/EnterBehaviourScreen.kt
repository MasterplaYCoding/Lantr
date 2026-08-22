package app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.enterbehaviour

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BackgroundColor = Color(0xFFF2F2F2)
private val BackIconColor = Color(0xFF9E9E9E)
private val AccentColor = Color(0xFFD4A017)
private val ControlOffset = 6.dp

enum class EnterAction {
    NewBlock,
    NewLine
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterBehaviourScreen(onBack: () -> Unit) {
    var selectedAction by remember { mutableStateOf(EnterAction.NewBlock) }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BackIconColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                text = "Enter Behaviour",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Choose what happens when hitting enter in a text block. List text blocks will override this.",
                fontSize = 16.sp,
                color = Color(0xFF717171),
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(24.dp))

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selected Behaviour",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = -ControlOffset)
                            .padding(end = ControlOffset)
                            .background(
                                color = Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(36.dp)
                            )
                            .padding(2.dp)
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            EnterActionSegment(
                                text = "New Block",
                                selected = selectedAction == EnterAction.NewBlock,
                                enabled = true,
                                onClick = { selectedAction = EnterAction.NewBlock },
                                modifier = Modifier.weight(1.06f)
                            )

                            Spacer(Modifier.width(4.dp))

                            EnterActionSegment(
                                text = "New Line",
                                selected = selectedAction == EnterAction.NewLine,
                                enabled = true,
                                onClick = { selectedAction = EnterAction.NewLine },
                                modifier = Modifier
                                    .offset(x = 16.dp)
                                    .weight(0.98f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EnterActionSegment(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        !enabled -> Color(0xFFEAEAEA)
        selected -> AccentColor
        else -> Color(0xFFEDEDED)
    }
    val textColor = if (selected) Color.White else Color.Black

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}
