package app.lantr.lantr.Views.Presentations.Canvas.ContentBlocks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Models.ContentBlock
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.ViewModel.MoveDirection
import app.lantr.lantr.ViewModel.SlideViewModel
import java.util.UUID

@Composable
fun ContentBlockControlButton(
    contentBlock: ContentBlock,
    slide: Slide,
    viewModel: SlideViewModel,
    modifier: Modifier = Modifier,
    onSlideUpdate: (Slide) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    val isFirstBlock = slide.blocks.firstOrNull()?.id == contentBlock.id
    val isLastBlock = slide.blocks.lastOrNull()?.id == contentBlock.id

    Box(modifier = modifier) {
        // Button
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 22.dp)
                .clip(CircleShape)
                .background(LantrTheme.extendedColors.goldPrimary)
                .clickable { showMenu = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Block controls",
                tint = LantrTheme.extendedColors.onGold,
                modifier = Modifier.size(16.dp)
            )
        }

        // Dropdown Menu
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Move Up") },
                leadingIcon = {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)
                },
                onClick = {
                    onSlideUpdate(
                    viewModel.moveContentBlock(
                        slide = slide,
                        blockId = contentBlock.id,
                        direction = MoveDirection.UP
                    ))
                    showMenu = false
                },
                enabled = !isFirstBlock
            )

            DropdownMenuItem(
                text = { Text("Move Down") },
                leadingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                },
                onClick = {
                    onSlideUpdate(
                    viewModel.moveContentBlock(
                        slide = slide,
                        blockId = contentBlock.id,
                        direction = MoveDirection.DOWN
                    ))
                    showMenu = false
                },
                enabled = !isLastBlock
            )
        }
    }
}