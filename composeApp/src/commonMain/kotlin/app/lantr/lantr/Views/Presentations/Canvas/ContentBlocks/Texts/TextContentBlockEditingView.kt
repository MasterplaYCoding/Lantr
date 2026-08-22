package app.lantr.lantr.Views.Presentations.Canvas.ContentBlocks.Texts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.BlockType
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.TextBlock
import app.lantr.lantr.Models.models_UI.isListType
import app.lantr.lantr.Models.models_UI.placeholderText
import app.lantr.lantr.Models.models_UI.textAlignment
import app.lantr.lantr.Services.CanvasLayout
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.ViewModel.SlideViewModel
import app.lantr.lantr.Models.models_UI.canvasTextColor
import app.lantr.lantr.Models.models_UI.getSystemFont
import kotlinx.coroutines.delay

@Composable
fun TextContentBlockEditingView(
    textContentBlock: TextBlock,
    slide: Slide,
    layout: CanvasLayout,
    viewModel: SlideViewModel,
    modifier: Modifier = Modifier,
    onSlideUpdate: (Slide) -> Unit
) {
    var editingText by remember(textContentBlock.id) {
        mutableStateOf(
            if (textContentBlock.type.isListType) {
                formatTextForEditing(textContentBlock)
            } else {
                textContentBlock.content
            }
        )
    }

    val focusRequester = remember { FocusRequester() }

    // Auto-focus on appear (Android/iOS behavior)
    LaunchedEffect(textContentBlock.id) {
        if (!PlatformUtil.isDesktop) {
            delay(100)
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        value = editingText,
        onValueChange = { newValue ->
            handleTextChange(
                oldValue = editingText,
                newValue = newValue,
                textContentBlock = textContentBlock,
                slide = slide,
                viewModel = viewModel,
                onEditingTextChange = { editingText = it },
                onSlideUpdate = onSlideUpdate
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                // Consume arrow key events to prevent them from leaving the text field
                when {
                    keyEvent.key == Key.DirectionLeft ||
                    keyEvent.key == Key.DirectionRight ||
                    keyEvent.key == Key.DirectionUp ||
                    keyEvent.key == Key.DirectionDown -> {
                        // Return true to consume the event and keep it within the text field
                        true
                    }
                    else -> false
                }
            },
        textStyle = textContentBlock.type.getSystemFont().copy(
            color = textContentBlock.type.canvasTextColor,
            textAlign = textContentBlock.contentAlignment.textAlignment
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Default
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                handleSubmit(
                    editingText = editingText,
                    textContentBlock = textContentBlock,
                    slide = slide,
                    viewModel = viewModel,
                    onEditingTextChange = { editingText = it },
                    focusRequester = focusRequester,
                    onSlideUpdate = onSlideUpdate
                )
            }
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            if (editingText.isEmpty()) {
                Text(
                    text = textContentBlock.type.placeholderText,
                    style = textContentBlock.type.getSystemFont().copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = textContentBlock.contentAlignment.textAlignment
                    )
                )
            }
            innerTextField()
        }
    )
}

// MARK: - Text Handling

private fun handleTextChange(
    oldValue: String,
    newValue: String,
    textContentBlock: TextBlock,
    slide: Slide,
    viewModel: SlideViewModel,
    onEditingTextChange: (String) -> Unit,
    onSlideUpdate: (Slide) -> Unit
) {
    // Check for instant markdown transformation
    if (!textContentBlock.type.isListType) {
        checkForInstantMarkdownTransform(oldValue, newValue)?.let { result ->
            onEditingTextChange(result.cleanedContent)
            var updatedSlide = viewModel.updateTextBlockContent(
                slide = slide,
                blockId = textContentBlock.id,
                content = result.cleanedContent
            )
            updatedSlide = viewModel.updateTextBlockType(
                slide = updatedSlide,
                blockId = textContentBlock.id,
                type = result.detectedType
            )
            onSlideUpdate(updatedSlide)
            return
        }
    }

    // Process input for list formatting if needed
    val processedText = processInput(newValue, textContentBlock.type)
    if (processedText != newValue) {
        onEditingTextChange(processedText)
    } else {
        onEditingTextChange(newValue)
    }

    val updatedSlide = viewModel.updateTextBlockContent(
        slide = slide,
        blockId = textContentBlock.id,
        content = if (processedText != newValue) processedText else newValue
    )
    onSlideUpdate(updatedSlide)
}

private fun handleSubmit(
    editingText: String,
    textContentBlock: TextBlock,
    slide: Slide,
    viewModel: SlideViewModel,
    onEditingTextChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onSlideUpdate: (Slide) -> Unit
) {
    val lastChar = editingText.lastOrNull()

    if (lastChar == '\n' && !textContentBlock.type.isListType) {
        val updatedSlide = viewModel.addContentBlock(
            slide = slide,
            type = BlockType.body,
            afterBlockId = textContentBlock.id
        )
        onSlideUpdate(updatedSlide)
    } else if (textContentBlock.type.isListType) {
        handleListSubmit(
            editingText = editingText,
            textContentBlock = textContentBlock,
            slide = slide,
            viewModel = viewModel,
            onEditingTextChange = onEditingTextChange,
            focusRequester = focusRequester,
            onSlideUpdate = onSlideUpdate
        )
    } else {
        val updatedSlide = viewModel.addContentBlock(
            slide = slide,
            type = BlockType.body,
            afterBlockId = textContentBlock.id
        )
        onSlideUpdate(updatedSlide)
    }
}

// MARK: - Instant Markdown Detection

private data class InstantMarkdownResult(
    val detectedType: BlockType,
    val cleanedContent: String
)

private fun checkForInstantMarkdownTransform(
    oldValue: String,
    newValue: String
): InstantMarkdownResult? {
    if (newValue.length <= oldValue.length || !newValue.endsWith(" ")) return null

    val trimmedNew = newValue.trim()

    return when {
        newValue.startsWith("# ") && !oldValue.startsWith("# ") -> {
            InstantMarkdownResult(
                detectedType = BlockType.h1,
                cleanedContent = trimmedNew.drop(1).trim()
            )
        }
        newValue.startsWith("## ") && !oldValue.startsWith("## ") -> {
            InstantMarkdownResult(
                detectedType = BlockType.h2,
                cleanedContent = trimmedNew.drop(2).trim()
            )
        }
        newValue.startsWith("### ") && !oldValue.startsWith("### ") -> {
            InstantMarkdownResult(
                detectedType = BlockType.h3,
                cleanedContent = trimmedNew.drop(3).trim()
            )
        }
        newValue.startsWith("- ") && !oldValue.startsWith("- ") -> {
            InstantMarkdownResult(
                detectedType = BlockType.bulletList,
                cleanedContent = newValue
            )
        }
        newValue.matches(Regex("^\\d+\\. .*")) && !oldValue.matches(Regex("^\\d+\\. .*")) -> {
            InstantMarkdownResult(
                detectedType = BlockType.numberedList,
                cleanedContent = newValue
            )
        }
        newValue.startsWith(">> ") && !oldValue.startsWith(">> ") -> {
            InstantMarkdownResult(
                detectedType = BlockType.quote,
                cleanedContent = trimmedNew.drop(2).trim()
            )
        }
        newValue.startsWith("// ") && !oldValue.startsWith("// ") -> {
            InstantMarkdownResult(
                detectedType = BlockType.presenterNotes,
                cleanedContent = trimmedNew.drop(2).trim()
            )
        }
        else -> null
    }
}

// MARK: - List Handling

private fun handleListSubmit(
    editingText: String,
    textContentBlock: TextBlock,
    slide: Slide,
    viewModel: SlideViewModel,
    onEditingTextChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onSlideUpdate: (Slide) -> Unit
) {
    val lines = editingText.split("\n")
    val lastLine = lines.lastOrNull() ?: ""

    val isEmptyListItem = isEmptyListLine(lastLine, textContentBlock.type)

    if (isEmptyListItem && lines.size > 1) {
        // Remove empty item and create new paragraph block
        val trimmedLines = lines.dropLast(1)
        val newText = trimmedLines.joinToString("\n")
        onEditingTextChange(newText)
        var updatedSlide = viewModel.updateTextBlockContent(
            slide = slide,
            blockId = textContentBlock.id,
            content = newText
        )
        updatedSlide = viewModel.addContentBlock(
            slide = updatedSlide,
            type = BlockType.body,
            afterBlockId = textContentBlock.id
        )
        onSlideUpdate(updatedSlide)
    } else {
        // Add new list item
        addNewListItem(
            editingText = editingText,
            textContentBlock = textContentBlock,
            slide = slide,
            viewModel = viewModel,
            onEditingTextChange = onEditingTextChange,
            focusRequester = focusRequester,
            onSlideUpdate = onSlideUpdate
        )
    }
}

private fun addNewListItem(
    editingText: String,
    textContentBlock: TextBlock,
    slide: Slide,
    viewModel: SlideViewModel,
    onEditingTextChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onSlideUpdate: (Slide) -> Unit
) {
    val lines = editingText.split("\n")

    val newText = when (textContentBlock.type) {
        BlockType.bulletList -> "$editingText\n- "
        BlockType.numberedList -> {
            val nextNumber = lines.size + 1
            "$editingText\n$nextNumber. "
        }
        else -> editingText
    }

    onEditingTextChange(newText)
    val updatedSlide = viewModel.updateTextBlockContent(
        slide = slide,
        blockId = textContentBlock.id,
        content = newText
    )
    onSlideUpdate(updatedSlide)

    // Request focus again
    focusRequester.requestFocus()
}

// MARK: - Text Processing

private fun formatTextForEditing(textContentBlock: TextBlock): String {
    return when (textContentBlock.type) {
        BlockType.bulletList -> formatBulletList(textContentBlock.content)
        BlockType.numberedList -> formatNumberedList(textContentBlock.content)
        else -> textContentBlock.content
    }
}

private fun processInput(input: String, type: BlockType): String {
    return when (type) {
        BlockType.bulletList -> processBulletListInput(input)
        BlockType.numberedList -> processNumberedListInput(input)
        else -> input
    }
}

private fun formatBulletList(text: String): String {
    val lines = text.split("\n")
    return lines.joinToString("\n") { line ->
        when {
            line.isEmpty() -> "- "
            line.startsWith("- ") -> line
            else -> "- $line"
        }
    }
}

private fun formatNumberedList(text: String): String {
    val lines = text.split("\n")
    return lines.mapIndexed { index, line ->
        val number = index + 1
        when {
            line.isEmpty() -> "$number. "
            line.matches(Regex("^\\d+\\. .*")) -> line
            else -> "$number. $line"
        }
    }.joinToString("\n")
}

private fun processBulletListInput(input: String): String {
    val lines = input.split("\n")
    return lines.joinToString("\n") { line ->
        when {
            line == "- " || line.isEmpty() -> "- "
            !line.startsWith("- ") && line.isNotEmpty() -> "- $line"
            else -> line
        }
    }
}

private fun processNumberedListInput(input: String): String {
    val lines = input.split("\n")
    return lines.mapIndexed { index, line ->
        val number = index + 1
        when {
            line.isEmpty() || line.matches(Regex("^\\d+\\. ?\$")) -> "$number. "
            line.matches(Regex("^\\d+\\. .*")) -> line
            else -> "$number. $line"
        }
    }.joinToString("\n")
}

private fun isEmptyListLine(line: String, type: BlockType): Boolean {
    val trimmed = line.trim()
    return when (type) {
        BlockType.bulletList -> trimmed == "-" || trimmed == "- "
        BlockType.numberedList -> trimmed.matches(Regex("^\\d+\\.? ?\$"))
        else -> false
    }
}
