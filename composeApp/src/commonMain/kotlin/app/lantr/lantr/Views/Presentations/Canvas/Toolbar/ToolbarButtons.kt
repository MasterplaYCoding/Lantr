package app.lantr.lantr.Views.Presentations.Canvas.Toolbar

import app.lantr.lantr.generated.resources.Res
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.generated.resources.backspace
import app.lantr.lantr.generated.resources.match_case
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


// Color extensions
fun Color.Companion.fromHex(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    val colorLong = cleanHex.toLong(16)
    return Color(
        red = ((colorLong shr 16) and 0xFF) / 255f,
        green = ((colorLong shr 8) and 0xFF) / 255f,
        blue = (colorLong and 0xFF) / 255f
    )
}

// Theme colors
object ToolbarColors {
    val simpleBackground = Color.White
    val simpleForeground = Color.Black
    val controlBackground = Color(0xFFF5F5F5)
    val controlHovered = Color(0xFFE0E0E0)
    val accentColor = Color(0xFF2196F3)
    val curtainRed = Color(0xFFE57373)
}

// Main ToolbarButtonGroup composable
@Composable
fun ToolbarButtonGroup(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val buttonHeight = if (PlatformUtil.isDesktop) 34.dp else 40.dp

    Row(
        modifier = modifier
            .height(buttonHeight)
            .clip(CircleShape),
            //.background(Color()),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

// Main ToolbarButton composable
@Composable
fun ToolbarButton(
    icon: DrawableResource? = null,
    title: String? = null,
    isActive: Boolean = false,
    isDisabled: Boolean = false,
    helpText: String,
    tintColor: Color? = null,
    backgroundColor: Color? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonSize = if (PlatformUtil.isDesktop) 34.dp else 40.dp
    val fontSize = if (PlatformUtil.isDesktop) 14.sp else 16.sp

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = tween(100)
    )

    val backgroundBrush: Color = when {
        backgroundColor != null-> {
            backgroundColor
        }
        else -> Color(0xffF1F1F1)
    }

    val foregroundColor = when {
        isActive -> {
            if (tintColor != null && tintColor.luminance() < 0.5f) {
                Color.White
            } else if (tintColor != null) {
                Color.Black
            } else {
                ToolbarColors.simpleBackground
            }
        }
        else -> ToolbarColors.simpleForeground
    }

    val corners : Dp = when {
        isActive -> 20.dp
        else -> 8.dp
    }

    val TintColor : Color = when {
        tintColor != null -> tintColor
        else -> Color.Black
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(corners))
            .background(color = backgroundBrush)
            .clickable(
                enabled = !isDisabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .hoverable(interactionSource),
        contentAlignment = Alignment.Center
    ) {
        if (title == null && icon!=null) {
            // Icon only
            Box(
                modifier = Modifier.size(buttonSize),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(icon),
                    contentDescription = "",
                    modifier = Modifier,
                    tint = TintColor,
                )
            }
        } else {
            // Icon with label
            Row(
                modifier = Modifier
                    .height(buttonSize)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(icon!=null) {
                    Icon(
                        painterResource(icon),
                        contentDescription = "",
                        modifier = Modifier,
                        tint = TintColor,
                    )
                }
                if(title!=null) {
                    Text(
                        text = title,
                        color = TintColor,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ToolbarDeleteButton composable
@Composable
fun ToolbarDeleteButton(
    isDisabled: Boolean = false,
    helpText: String,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonHeight = if (PlatformUtil.isDesktop) 34.dp else 40.dp
    val buttonWidth = if (PlatformUtil.isDesktop) 60.dp else 64.dp
    val fontSize = if (PlatformUtil.isDesktop) 14.sp else 16.sp

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = tween(100)
    )

    val gradientColors = listOf(
        Color.fromHex("DF2348"),
        ToolbarColors.curtainRed
    )

    Box(
        modifier = modifier
            .size(width = buttonWidth, height = buttonHeight)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(
                enabled = !isDisabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = action
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(Res.drawable.backspace),
            contentDescription = "",
            modifier = Modifier,
            tint = MaterialTheme.colorScheme.background,
        )
    }
}

// ToolbarTextStyleButton composable
@Composable
fun ToolbarTextStyleButton(
    currentStyleName: String,
    showStylePopover: Boolean,
    onToggle: () -> Unit,
    // sheetManager: SheetManager? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val buttonHeight = if (PlatformUtil.isDesktop) 34.dp else 40.dp
    val buttonWidth = if (PlatformUtil.isDesktop) 34.dp else 40.dp
    val fontSize = if (PlatformUtil.isDesktop) 14.sp else 16.sp
    val cornerRadius = if (PlatformUtil.isDesktop) 17.dp else 20.dp

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Row(
        modifier = modifier
            .height(buttonHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onToggle()
                // sheetManager?.willPresent()
            }
            .hoverable(interactionSource)
    ) {
        // Left part with icon (gradient background)
        Box(
            modifier = Modifier
                .size(width = buttonWidth, height = buttonHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = cornerRadius,
                        bottomStart = cornerRadius,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(Res.drawable.match_case),
                contentDescription = "",
                modifier = Modifier,
                tint = MaterialTheme.colorScheme.background,
            )
        }

        // Right part with text (normal background)
        Box(
            modifier = Modifier
                .height(buttonHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = cornerRadius,
                        bottomEnd = cornerRadius
                    )
                )
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentStyleName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = fontSize,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // Style popover
    if (showStylePopover) {
        Popup(
            onDismissRequest = {
                onToggle()
                // sheetManager?.didDismiss()
            },
            alignment = Alignment.TopStart
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.widthIn(min = 260.dp, max = 300.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    content = content
                )
            }
        }
    }
}


// Helper function to get icon placeholders
private fun getIconPlaceholder(iconName: String): String {
    return when (iconName) {
        "plus" -> "+"
        "xmark" -> "×"
        "text" -> "T"
        "note" -> "📝"
        "image", "photo" -> "🖼"
        "arc" -> "⤵"
        "slides" -> "▢"
        "align_top" -> "⬆"
        "align_center" -> "⬌"
        "align_bottom" -> "⬇"
        "align_left" -> "⬅"
        "align_right" -> "➡"
        "palette" -> "🎨"
        "crop" -> "✂"
        "markup" -> "✏"
        "photo_replace" -> "🔄"
        "textformat" -> "Aa"
        "delete" -> "⌫"
        else -> "•"
    }
}

// Additional helper components for animations and effects
@Composable
fun animateButtonScale(isPressed: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = tween(durationMillis = 100)
    ).value
}