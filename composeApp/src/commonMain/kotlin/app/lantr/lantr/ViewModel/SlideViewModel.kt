package app.lantr.lantr.ViewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import app.lantr.lantr.Models.BlockType
import app.lantr.lantr.Models.ContentAlignment
import app.lantr.lantr.Models.ContentArrangement
import app.lantr.lantr.Models.ContentBlock
import app.lantr.lantr.Models.ImageBlock
import app.lantr.lantr.Models.Themes.RGBColor
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.TextBlock
import app.lantr.lantr.Models.models_UI.isImageType
import app.lantr.lantr.Services.CanvasLayout
import app.lantr.lantr.Services.CanvasLayoutService
import app.lantr.lantr.Services.MarkdownParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class  SlideViewModel(
    private val markdownParser: MarkdownParser,
    private val layoutService: CanvasLayoutService
) : ViewModel() {

    private val _focusedContentBlockId = MutableStateFlow<UUID?>(null)
    val focusedContentBlockId: StateFlow<UUID?> = _focusedContentBlockId.asStateFlow()

    private val _canvasLayout = MutableStateFlow<CanvasLayout?>(null)
    val canvasLayout: StateFlow<CanvasLayout?> = _canvasLayout.asStateFlow()

    fun updateCanvasLayout(availableSize: Size, density: Float = 1f, isDesktop: Boolean = false) {
        _canvasLayout.value = layoutService.calculateLayout(availableSize, density, isDesktop)
    }

    // Block Management
    fun addContentBlock(slide: Slide, type: BlockType, afterBlockId: UUID? = null): Slide {
        val newBlock: ContentBlock = when {
            type.isImageType -> ImageBlock(
                imagePath = "",
                imageHeight = 400f
            )
            else -> TextBlock(content = "", type = type)
        }

        val updatedBlocks = if (afterBlockId != null) {
            val index = slide.blocks.indexOfFirst { it.id == afterBlockId }
            if (index >= 0) {
                slide.blocks.toMutableList().apply { add(index + 1, newBlock) }
            } else {
                slide.blocks + newBlock
            }
        } else {
            slide.blocks + newBlock
        }

        focusContentBlock(newBlock.id)
        return slide.copy(blocks = updatedBlocks)
    }

    fun removeContentBlock(slide: Slide, blockId: UUID): Slide {
        val updatedBlocks = slide.blocks.filterNot { it.id == blockId }
        if (_focusedContentBlockId.value == blockId) {
            unfocusContentBlock()
        }
        return slide.copy(blocks = updatedBlocks)
    }

    fun removeFocusedContentBlock(slide: Slide): Slide {
        val focusedId = _focusedContentBlockId.value ?: return slide
        return removeContentBlock(slide, focusedId)
    }

    fun moveContentBlock(slide: Slide, blockId: UUID, direction: MoveDirection): Slide {
        val currentIndex = slide.blocks.indexOfFirst { it.id == blockId }
        if (currentIndex < 0) return slide

        val newIndex = when (direction) {
            MoveDirection.UP -> if (currentIndex > 0) currentIndex - 1 else return slide
            MoveDirection.DOWN -> if (currentIndex < slide.blocks.size - 1) currentIndex + 1 else return slide
        }

        val mutableBlocks = slide.blocks.toMutableList()
        val block = mutableBlocks.removeAt(currentIndex)
        mutableBlocks.add(newIndex, block)

        return slide.copy(blocks = mutableBlocks)
    }

    fun reorderContentBlock(slide: Slide, blockId: UUID, newIndex: Int): Slide {
        val currentIndex = slide.blocks.indexOfFirst { it.id == blockId }
        if (currentIndex < 0 || newIndex < 0 || newIndex >= slide.blocks.size || newIndex == currentIndex) {
            return slide
        }

        val mutableBlocks = slide.blocks.toMutableList()
        val block = mutableBlocks.removeAt(currentIndex)
        mutableBlocks.add(newIndex, block)

        return slide.copy(blocks = mutableBlocks)
    }

    // Content Updates
    fun updateTextBlockContent(slide: Slide, blockId: UUID, content: String): Slide {
        val blockIndex = slide.blocks.indexOfFirst { it.id == blockId }
        if (blockIndex < 0) return slide

        val block = slide.blocks[blockIndex] as? TextBlock ?: return slide
        val parseResult = markdownParser.parse(content)

        val updatedBlock = block.copy(
            content = parseResult.cleanedContent,
            type = if (parseResult.wasTransformed) parseResult.detectedType else block.type
        )

        val updatedBlocks = slide.blocks.toMutableList()
        updatedBlocks[blockIndex] = updatedBlock

        return slide.copy(blocks = updatedBlocks)
    }

    fun updateTextBlockType(slide: Slide, blockId: UUID?, type: BlockType): Slide {
        val blockIndex = slide.blocks.indexOfFirst { it.id == blockId }
        if (blockIndex < 0) return slide

        val block = slide.blocks[blockIndex] as? TextBlock ?: return slide
        val updatedBlock = block.copy(type = type)

        val updatedBlocks = slide.blocks.toMutableList()
        updatedBlocks[blockIndex] = updatedBlock

        return slide.copy(blocks = updatedBlocks)
    }

    fun updateContentBlockAlignment(slide: Slide, blockId: UUID, alignment: ContentAlignment): Slide {
        val blockIndex = slide.blocks.indexOfFirst { it.id == blockId }
        if (blockIndex < 0) return slide

        val block = slide.blocks[blockIndex]
        val updatedBlock = when (block) {
            is TextBlock -> block.copy(contentAlignment = alignment)
            is ImageBlock -> block.copy(contentAlignment = alignment)
            else -> {}
        }

        val updatedBlocks = slide.blocks.toMutableList()
        updatedBlocks[blockIndex] = updatedBlock as ContentBlock

        return slide.copy(blocks = updatedBlocks)
    }

    fun updateImageBlockHeight(slide: Slide, blockId: UUID, height: Float): Slide {
        val blockIndex = slide.blocks.indexOfFirst { it.id == blockId }
        if (blockIndex < 0) return slide

        val block = slide.blocks[blockIndex] as? ImageBlock ?: return slide
        val updatedBlock = block.copy(imageHeight = height)

        val updatedBlocks = slide.blocks.toMutableList()
        updatedBlocks[blockIndex] = updatedBlock

        return slide.copy(blocks = updatedBlocks)
    }

    fun updateImageBlockRadius(slide: Slide, blockId: UUID, radius: Float): Slide {
        val blockIndex = slide.blocks.indexOfFirst { it.id == blockId }
        if (blockIndex < 0) return slide

        val block = slide.blocks[blockIndex] as? ImageBlock ?: return slide
        val updatedBlock = block.copy(cornerRadius = radius)

        val updatedBlocks = slide.blocks.toMutableList()
        updatedBlocks[blockIndex] = updatedBlock

        return slide.copy(blocks = updatedBlocks)
    }

    fun updateImageBlockPath(slide: Slide, blockId: UUID, imagePath: String): Slide {
        return updateImageBlock(slide, blockId) { block ->
            block.copy(imagePath = imagePath)
        }
    }

    fun updateImageBlockAsset(
        slide: Slide,
        blockId: UUID,
        imagePath: String,
        imageHeight: Float
    ): Slide {
        return updateImageBlock(slide, blockId) { block ->
            block.copy(
                imagePath = imagePath,
                imageHeight = imageHeight
            )
        }
    }

    fun replaceImageBlock(slide: Slide, updatedBlock: ImageBlock): Slide {
        val blockIndex = slide.blocks.indexOfFirst { it.id == updatedBlock.id }
        if (blockIndex < 0) return slide

        val updatedBlocks = slide.blocks.toMutableList()
        updatedBlocks[blockIndex] = updatedBlock

        return slide.copy(blocks = updatedBlocks)
    }

    private fun updateImageBlock(
        slide: Slide,
        blockId: UUID,
        transform: (ImageBlock) -> ImageBlock
    ): Slide {
        val blockIndex = slide.blocks.indexOfFirst { it.id == blockId }
        if (blockIndex < 0) return slide

        val block = slide.blocks[blockIndex] as? ImageBlock ?: return slide
        val updatedBlocks = slide.blocks.toMutableList()
        updatedBlocks[blockIndex] = transform(block)
        return slide.copy(blocks = updatedBlocks)
    }

    // Focus Management
    fun focusContentBlock(id: UUID) {
        println("SlideViewModel: Focusing block with ID: $id")
        _focusedContentBlockId.value = id
        println("SlideViewModel: focusedContentBlockId is now: ${_focusedContentBlockId.value}")
    }

    fun focusPreviousBlock(slide: Slide) {
        val focusedId = _focusedContentBlockId.value ?: return
        val currentIndex = slide.blocks.indexOfFirst { it.id == focusedId }
        if (currentIndex > 0) {
            _focusedContentBlockId.value = slide.blocks[currentIndex - 1].id
        }
    }

    fun focusNextBlock(slide: Slide) {
        val focusedId = _focusedContentBlockId.value ?: return
        val currentIndex = slide.blocks.indexOfFirst { it.id == focusedId }
        if (currentIndex >= 0 && currentIndex < slide.blocks.size - 1) {
            _focusedContentBlockId.value = slide.blocks[currentIndex + 1].id
        }
    }

    fun unfocusContentBlock() {
        _focusedContentBlockId.value = null
    }

    fun getFocusedContentBlock(slide: Slide): ContentBlock? {
        val focusedId = _focusedContentBlockId.value ?: return null
        return slide.blocks.firstOrNull { it.id == focusedId }
    }

    // Slide Arrangement
    fun updateSlideContentArrangement(slide: Slide, arrangement: ContentArrangement): Slide {
        return slide.copy(contentArrangement = arrangement)
    }

    // Slide Color
    fun updateSlideOverrideColor(slide: Slide, color: RGBColor?): Slide {
        return slide.copy(overrideColor = color)
    }

    // Text Block Color
    fun updateTextBlockOverrideColor(slide: Slide, blockId: UUID, color: RGBColor?): Slide {
        val blockIndex = slide.blocks.indexOfFirst { it.id == blockId }
        if (blockIndex < 0) return slide

        val block = slide.blocks[blockIndex] as? TextBlock ?: return slide
        val updatedBlock = block.copy(overrideColor = color)

        val updatedBlocks = slide.blocks.toMutableList()
        updatedBlocks[blockIndex] = updatedBlock

        return slide.copy(blocks = updatedBlocks)
    }

    // Toolbar Helpers
    fun setFocusedContentBlockType(slide: Slide, type: BlockType): Slide {
        return if (_focusedContentBlockId.value != null) {
            updateTextBlockType(slide, _focusedContentBlockId.value!!, type)
        } else {
            addContentBlock(slide, type)
        }
    }

    fun setFocusedContentBlockAlignment(slide: Slide, alignment: ContentAlignment): Slide {
        val focusedId = _focusedContentBlockId.value ?: return slide
        return updateContentBlockAlignment(slide, focusedId, alignment)
    }

    fun setSlideContentArrangement(slide: Slide, arrangement: ContentArrangement): Slide {
        return updateSlideContentArrangement(slide, arrangement)
    }

    fun setSlideOverrideColor(slide: Slide, color: RGBColor?): Slide {
        return updateSlideOverrideColor(slide, color)
    }

    // Toolbar helper to set override color for currently focused text block
    // Follows same pattern as slide override color
    @Composable
    fun setTextBlockOverrideColor(slide: Slide, color: RGBColor?): Slide {
        val focusedId = focusedContentBlockId.collectAsState().value ?: return slide
        return updateTextBlockOverrideColor(slide, focusedId, color)
    }

    fun isFocusedContentBlockOfType(slide: Slide, type: BlockType): Boolean {
        return getFocusedContentBlock(slide)?.type == type
    }

    fun isFocusedContentBlockAligned(slide: Slide, alignment: ContentAlignment): Boolean {
        return getFocusedContentBlock(slide)?.contentAlignment == alignment
    }

    fun isSlideContentArranged(slide: Slide, arrangement: ContentArrangement): Boolean {
        return slide.contentArrangement == arrangement
    }

    /*
    // Drag and Drop
    fun createContentBlockTransferable(blockId: UUID, slide: Slide): ContentBlockTransferable {
        val blockIndex = slide.blocks.indexOfFirst { it.id == blockId }
        val block = slide.blocks.getOrNull(blockIndex)

        return ContentBlockTransferable(
            blockId = blockId,
            sourceSlideId = slide.id,
            sourceBlockIndex = blockIndex.coerceAtLeast(0),
            blockType = block?.type ?: BlockType.body
        )
    }

    fun handleContentBlockDrop(
        transferable: ContentBlockTransferable,
        slide: Slide,
        targetIndex: Int
    ): Slide {
        if (slide.id != transferable.sourceSlideId) {
            return slide // Cannot drop block from different slide
        }

        return reorderBlock(slide, transferable.sourceBlockIndex, targetIndex)
    }



    private fun reorderBlock(slide: Slide, sourceIndex: Int, targetIndex: Int): Slide {
        if (sourceIndex < 0 || sourceIndex >= slide.blocks.size ||
            targetIndex < 0 || targetIndex > slide.blocks.size ||
            sourceIndex == targetIndex) {
            return slide
        }

        val mutableBlocks = slide.blocks.toMutableList()
        val movedBlock = mutableBlocks.removeAt(sourceIndex)

        val insertionIndex = if (targetIndex > sourceIndex) targetIndex - 1 else targetIndex
        mutableBlocks.add(insertionIndex, movedBlock)

        return slide.copy(blocks = mutableBlocks)
    }


     */
}

enum class MoveDirection { UP, DOWN }
