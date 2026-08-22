package app.lantr.lantr.Views.Presentations.Canvas.Popovers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.BlockType
import app.lantr.lantr.Models.Themes.PredefinedThemes
import app.lantr.lantr.Models.Themes.Theme
import app.lantr.lantr.Models.models_UI.textColor
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.ViewModel.PresentationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ThemeDetailView(
    viewModel: PresentationViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val presentation by viewModel.presentation.collectAsState()

    fun isThemeSelected(predefinedTheme: PredefinedThemes): Boolean {
        val currentTheme = presentation.theme
        val predefinedThemeInstance = Theme.predefined(predefinedTheme)

        return currentTheme.backgroundColor == predefinedThemeInstance.backgroundColor &&
                currentTheme.altBackgroundColor == predefinedThemeInstance.altBackgroundColor &&
                currentTheme.contentMargins == predefinedThemeInstance.contentMargins &&
                currentTheme.elementSpacing == predefinedThemeInstance.elementSpacing
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (PlatformUtil.isPhone) {
                    Modifier.fillMaxHeight()
                } else {
                    Modifier.heightIn(max = 420.dp)
                }
            )
            .widthIn(min = 380.dp)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 4.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Theme",
                fontSize = if (PlatformUtil.isDesktop) 18.sp else 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Grid of themes
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 2.dp, bottom = 12.dp)
        ) {
            // Create button
            item {
                ThemeCreateButton()
            }

            // Theme cards
            items(
                items = PredefinedThemes.values().toList(),
                key = { it.name }
            ) { predefinedTheme ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ThemeCardView(
                        theme = Theme.predefined(predefinedTheme),
                        themeName = predefinedTheme.displayName,
                        isSelected = isThemeSelected(predefinedTheme),
                        onSelect = {
                            viewModel.updateTheme(Theme.predefined(predefinedTheme))
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeCreateButton(
    modifier: Modifier = Modifier
) {
    var isCreateTapped by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isCreateTapped) 0.95f else 1f,
        label = "scale"
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .width(100.dp)
            .scale(scale)
            .clickable {
                scope.launch {
                    isCreateTapped = true
                    delay(100)
                    isCreateTapped = false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .dashedBorder(
                    color = MaterialTheme.colorScheme.primary,
                    width = 2.dp,
                    radius = 12.dp,
                    dashLength = 5.dp,
                    gapLength = 5.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Create theme",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Create",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(100.dp)
        )
    }
}

@Composable
private fun ThemeCardView(
    theme: Theme,
    themeName: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(100.dp)
            .clickable(onClick = onSelect),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 120.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // Diagonal split background with text
            ThemePreviewSimplified(theme = theme)
        }

        Text(
            text = themeName,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(100.dp)
        )
    }
}

/*
@Composable
private fun ThemePreview(
    theme: Theme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithContent {
                // Draw alt background (top-right triangle)
                drawRect(color = theme.altBackgroundColor.toColor())

                // Draw main background (bottom-left triangle)
                val trianglePath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, size.height)
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(
                    path = trianglePath,
                    color = theme.backgroundColor.toColor()
                )

                // Draw content
                drawContent()
            },
        contentAlignment = Alignment.Center
    ) {
        // Text content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // These will be drawn twice - once for each background
            ThemeText(
                text = "Here",
                theme = theme,
                blockType = BlockType.h1,
                fontSize = 20.sp
            )
            ThemeText(
                text = "Will go",
                theme = theme,
                blockType = BlockType.quote,
                fontSize = 16.sp
            )
            ThemeText(
                text = "Your ideas",
                theme = theme,
                blockType = BlockType.body,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ThemeText(
    text: String,
    theme: Theme,
    blockType: BlockType,
    fontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    // This is simplified - you'd need to handle the diagonal masking
    // For a proper implementation, you'd need custom drawing
    Text(
        text = text,
        fontSize = fontSize,
        style = theme.textStyles[blockType]?.getTextStyle(fontSize)
            ?: MaterialTheme.typography.bodyMedium,
        color = theme.textColor(blockType),
        modifier = modifier
    )
}

 */

// Simplified version without diagonal split
@Composable
private fun ThemePreviewSimplified(
    theme: Theme,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize()
    ) {
        // Alt background side
        Box(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
                .background(theme.altBackgroundColor.toColor()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Alt",
                    fontSize = 16.sp,
                    color = theme.textStyles[BlockType.h1]?.altTextColor?.toColor()
                        ?: Color.White
                )
                Text(
                    text = "BG",
                    fontSize = 12.sp,
                    color = theme.textStyles[BlockType.body]?.altTextColor?.toColor()
                        ?: Color.White
                )
            }
        }

        // Main background side
        Box(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
                .background(theme.backgroundColor.toColor()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Main",
                    fontSize = 16.sp,
                    color = theme.textStyles[BlockType.h1]?.textColor?.toColor()
                        ?: Color.Black
                )
                Text(
                    text = "BG",
                    fontSize = 12.sp,
                    color = theme.textStyles[BlockType.body]?.textColor?.toColor()
                        ?: Color.Black
                )
            }
        }
    }
}

// Dashed border helper (reuse from previous code)
private fun Modifier.dashedBorder(
    color: Color,
    width: androidx.compose.ui.unit.Dp = 1.dp,
    radius: androidx.compose.ui.unit.Dp = 0.dp,
    dashLength: androidx.compose.ui.unit.Dp = 4.dp,
    gapLength: androidx.compose.ui.unit.Dp = 4.dp
): Modifier = this.then(
    Modifier.drawWithContent {
        drawContent()

        val pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            phase = 0f
        )

        drawRoundRect(
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = width.toPx(),
                pathEffect = pathEffect
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius.toPx())
        )
    }
)
