package app.lantr.lantr.Views.Presentations.Canvas.Popovers

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Util.PlatformUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrainDumpView(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var brainDumpText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Brain Dump") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            // TODO: Process brain dump
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC143C) // CurtainRed
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Done")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Description text
            Text(
                text = "Write freely. Drop any thought, fragment, or idea. The Director will shape it into story blocks.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )

            // Text editor
            BrainDumpTextEditor(
                text = brainDumpText,
                onTextChange = { brainDumpText = it },
                placeholder = "Anything on your mind, half-formed ideas, stats, sparks…",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Past dumps button
            PastDumpsButton(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BrainDumpTextEditor(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(
            if (PlatformUtil.isDesktop) 22.dp else 26.dp
        ),
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    if (PlatformUtil.isDesktop) 10.dp else 12.dp
                )
        ) {
            // Placeholder
            if (text.isEmpty()) {
                Text(
                    text = placeholder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = if (PlatformUtil.isDesktop) 4.dp else 4.dp,
                            vertical = if (PlatformUtil.isDesktop) 0.dp else 6.dp
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = if (PlatformUtil.isDesktop) 14.sp else 17.sp
                )
            }

            // TextField
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFFDC143C) // CurtainRed
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (PlatformUtil.isDesktop) 14.sp else 17.sp
                )
            )
        }
    }
}

@Composable
private fun PastDumpsButton(
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = modifier.padding(
            top = if (PlatformUtil.isDesktop) 6.dp else 10.dp
        ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .then(
                    if (PlatformUtil.isDesktop) {
                        Modifier
                            .background(
                                color = if (isHovered) {
                                    Color.Gray.copy(alpha = 0.1f)
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp)
                            .hoverable(interactionSource)
                    } else {
                        Modifier
                    }
                ),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "View Past Dumps",
                color = Color.Gray,
                fontSize = if (PlatformUtil.isDesktop) 14.sp else 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}