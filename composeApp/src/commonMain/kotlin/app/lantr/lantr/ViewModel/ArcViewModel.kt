package app.lantr.lantr.ViewModel

import androidx.lifecycle.ViewModel
import app.lantr.lantr.Models.Arc
import app.lantr.lantr.Models.PredefinedBlockType
import app.lantr.lantr.Models.StoryBlock
import app.lantr.lantr.Models.StorySection
import app.lantr.lantr.Models.StorySectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * Arc-focused view model that manages story sections and blocks within an arc
 * Follows clean data flow pattern with StateFlow
 * Provides comprehensive section and block management functionality
 */
class ArcViewModel(
    initialArc: Arc = Arc()
) : ViewModel() {

    private val _arc = MutableStateFlow(initialArc)
    val arc: StateFlow<Arc> = _arc.asStateFlow()

    // MARK: - Story Section Management

    /**
     * Adds a new story section to the arc
     * @param sectionType The type of section to create
     * @param afterSectionId Optional ID of section after which to insert (null = append to end)
     */
    fun addStorySection(
        sectionType: StorySectionType,
        afterSectionId: UUID? = null
    ) {
        _arc.update { currentArc ->
            val newSection = StorySection(type = sectionType, blocks = emptyList())

            if (afterSectionId != null) {
                val index = currentArc.sections.indexOfFirst { it.id == afterSectionId }
                if (index != -1) {
                    currentArc.copy(
                        sections = currentArc.sections.toMutableList().apply {
                            add(index + 1, newSection)
                        }
                    )
                } else {
                    currentArc.copy(sections = currentArc.sections + newSection)
                }
            } else {
                currentArc.copy(sections = currentArc.sections + newSection)
            }
        }
    }

    /**
     * Removes a story section from the arc
     * @param sectionId The ID of the section to remove
     * @return True if removal was successful, false if section not found
     */
    fun removeStorySection(sectionId: UUID): Boolean {
        var success = false
        _arc.update { currentArc ->
            val index = currentArc.sections.indexOfFirst { it.id == sectionId }
            if (index != -1) {
                success = true
                println("SECTION REMOVED: $sectionId")
                currentArc.copy(
                    sections = currentArc.sections.toMutableList().apply {
                        removeAt(index)
                    }
                )
            } else {
                println("ARC ERROR: Could not find section with ID $sectionId")
                currentArc
            }
        }
        return success
    }

    // MARK: - Story Block Management

    /**
     * Adds a new story block to a specific section within the arc
     * @param sectionId The ID of the section to add the block to
     * @param blockType The type of story block to create
     * @param afterBlockId Optional ID of block after which to insert (null = append to end)
     */
    fun addStoryBlock(
        sectionId: UUID,
        blockType: PredefinedBlockType,
        afterBlockId: UUID? = null
    ) {
        _arc.update { currentArc ->
            val sectionIndex = currentArc.sections.indexOfFirst { it.id == sectionId }
            if (sectionIndex == -1) {
                println("ARC ERROR: Could not find section with ID $sectionId")
                return@update currentArc
            }

            val newBlock = StoryBlock.predefined(blockType)
            val section = currentArc.sections[sectionIndex]

            val updatedBlocks = if (afterBlockId != null) {
                val blockIndex = section.blocks.indexOfFirst { it.id == afterBlockId }
                if (blockIndex != -1) {
                    section.blocks.toMutableList().apply {
                        add(blockIndex + 1, newBlock)
                    }
                } else {
                    section.blocks + newBlock
                }
            } else {
                section.blocks + newBlock
            }

            println("STORY BLOCK ADDED: ${newBlock.id} to section $sectionId")

            currentArc.copy(
                sections = currentArc.sections.toMutableList().apply {
                    set(sectionIndex, section.copy(blocks = updatedBlocks))
                }
            )
        }
    }

    fun insertStorySection(
        sectionType: StorySectionType,
        targetIndex: Int
    ) {
        _arc.update { currentArc ->
            val boundedIndex = targetIndex.coerceIn(0, currentArc.sections.size)
            val newSection = StorySection(type = sectionType, blocks = emptyList())

            currentArc.copy(
                sections = currentArc.sections.toMutableList().apply {
                    add(boundedIndex, newSection)
                }
            )
        }
    }

    fun insertStoryBlock(
        sectionId: UUID,
        blockType: PredefinedBlockType,
        targetIndex: Int
    ) {
        _arc.update { currentArc ->
            val sectionIndex = currentArc.sections.indexOfFirst { it.id == sectionId }
            if (sectionIndex == -1) {
                println("ARC ERROR: Could not find section with ID $sectionId")
                return@update currentArc
            }

            val section = currentArc.sections[sectionIndex]
            val boundedIndex = targetIndex.coerceIn(0, section.blocks.size)
            val updatedBlocks = section.blocks.toMutableList().apply {
                add(boundedIndex, StoryBlock.predefined(blockType))
            }

            currentArc.copy(
                sections = currentArc.sections.toMutableList().apply {
                    set(sectionIndex, section.copy(blocks = updatedBlocks))
                }
            )
        }
    }

    /**
     * Removes a story block from its section within the arc
     * @param sectionId The ID of the section containing the block
     * @param blockId The ID of the block to remove
     * @return True if removal was successful, false otherwise
     */
    fun removeStoryBlock(sectionId: UUID, blockId: UUID): Boolean {
        var success = false
        _arc.update { currentArc ->
            val sectionIndex = currentArc.sections.indexOfFirst { it.id == sectionId }
            if (sectionIndex == -1) {
                println("ARC ERROR: Could not find section with ID $sectionId")
                return@update currentArc
            }

            val section = currentArc.sections[sectionIndex]
            val blockIndex = section.blocks.indexOfFirst { it.id == blockId }

            if (blockIndex == -1) {
                println("ARC ERROR: Could not find block with ID $blockId in section $sectionId")
                return@update currentArc
            }

            success = true
            println("STORY BLOCK REMOVED: $blockId from section $sectionId")

            currentArc.copy(
                sections = currentArc.sections.toMutableList().apply {
                    set(sectionIndex, section.copy(
                        blocks = section.blocks.toMutableList().apply {
                            removeAt(blockIndex)
                        }
                    ))
                }
            )
        }
        return success
    }

    // MARK: - Story Block Content Updates

    /**
     * Updates the main idea text for a specific story block within the arc
     * @param sectionId The ID of the section containing the block
     * @param blockId The ID of the block to update
     * @param mainIdea The new main idea text
     */
    fun updateStoryBlockMainIdea(
        sectionId: UUID,
        blockId: UUID,
        mainIdea: String
    ) {
        _arc.update { currentArc ->
            val sectionIndex = currentArc.sections.indexOfFirst { it.id == sectionId }
            if (sectionIndex == -1) {
                println("ARC ERROR: Could not find section with ID $sectionId")
                return@update currentArc
            }

            val section = currentArc.sections[sectionIndex]
            val blockIndex = section.blocks.indexOfFirst { it.id == blockId }

            if (blockIndex == -1) {
                println("ARC ERROR: Could not find block $blockId in section $sectionId")
                return@update currentArc
            }

            println("STORY BLOCK UPDATED: $blockId main idea updated")

            currentArc.copy(
                sections = currentArc.sections.toMutableList().apply {
                    set(sectionIndex, section.copy(
                        blocks = section.blocks.toMutableList().apply {
                            set(blockIndex, get(blockIndex).copy(mainIdea = mainIdea))
                        }
                    ))
                }
            )
        }
    }

    fun moveStoryBlock(
        sourceSectionId: UUID,
        sourceBlockId: UUID,
        sourceIndex: Int,
        targetSectionId: UUID,
        targetIndex: Int
    ) {
        _arc.update { currentArc ->
            val sourceSectionIndex = currentArc.sections.indexOfFirst { it.id == sourceSectionId }
            val targetSectionIndex = currentArc.sections.indexOfFirst { it.id == targetSectionId }

            if (sourceSectionIndex == -1 || targetSectionIndex == -1) {
                println("BLOCK MOVE ERROR: Invalid source or target section")
                return@update currentArc
            }

            val sourceSection = currentArc.sections[sourceSectionIndex]
            if (sourceIndex !in sourceSection.blocks.indices) {
                println("BLOCK MOVE ERROR: Invalid source block index $sourceIndex")
                return@update currentArc
            }

            val sourceBlock = sourceSection.blocks[sourceIndex]
            if (sourceBlock.id != sourceBlockId) {
                println("BLOCK MOVE ERROR: Source block validation failed")
                return@update currentArc
            }

            if (sourceSectionId == targetSectionId) {
                return@update reorderBlockInSection(
                    arc = currentArc,
                    sectionIndex = sourceSectionIndex,
                    sourceIndex = sourceIndex,
                    targetIndex = targetIndex
                )
            }

            moveBlockBetweenSections(
                arc = currentArc,
                fromSectionIndex = sourceSectionIndex,
                toSectionIndex = targetSectionIndex,
                blockIndex = sourceIndex,
                targetIndex = targetIndex
            )
        }
    }

    private fun reorderBlockInSection(
        arc: Arc,
        sectionIndex: Int,
        sourceIndex: Int,
        targetIndex: Int
    ): Arc {
        if (sectionIndex !in arc.sections.indices) {
            println("BLOCK REORDER ERROR: Invalid section index $sectionIndex")
            return arc
        }

        val section = arc.sections[sectionIndex]
        val blocksCount = section.blocks.size

        if (sourceIndex !in 0 until blocksCount || targetIndex !in 0..blocksCount) {
            println("BLOCK REORDER ERROR: Invalid indices source=$sourceIndex target=$targetIndex")
            return arc
        }

        if (sourceIndex == targetIndex) {
            return arc
        }

        val blocks = section.blocks.toMutableList()
        val movedBlock = blocks.removeAt(sourceIndex)
        val finalInsertionIndex = if (targetIndex > sourceIndex) targetIndex - 1 else targetIndex
        blocks.add(finalInsertionIndex, movedBlock)

        return arc.copy(
            sections = arc.sections.toMutableList().apply {
                set(sectionIndex, section.copy(blocks = blocks))
            }
        )
    }

    /**
     * Moves a story block from one section to another
     */
    private fun moveBlockBetweenSections(
        arc: Arc,
        fromSectionIndex: Int,
        toSectionIndex: Int,
        blockIndex: Int,
        targetIndex: Int
    ): Arc {
        if (fromSectionIndex !in arc.sections.indices || toSectionIndex !in arc.sections.indices) {
            println("BLOCK MOVE ERROR: Invalid section indices")
            return arc
        }

        val fromSection = arc.sections[fromSectionIndex]
        if (blockIndex !in fromSection.blocks.indices) {
            println("BLOCK MOVE ERROR: Invalid block index $blockIndex")
            return arc
        }

        val toSection = arc.sections[toSectionIndex]

        // Remove from source
        val fromBlocks = fromSection.blocks.toMutableList()
        val movedBlock = fromBlocks.removeAt(blockIndex)

        // Add to target
        val toBlocks = toSection.blocks.toMutableList()
        val insertIndex = targetIndex.coerceAtMost(toBlocks.size)
        toBlocks.add(insertIndex, movedBlock)

        return arc.copy(
            sections = arc.sections.toMutableList().apply {
                set(fromSectionIndex, fromSection.copy(blocks = fromBlocks))
                set(toSectionIndex, toSection.copy(blocks = toBlocks))
            }
        )
    }

    /**
     * Reorders a story section from its current position to a new index
     * @param sourceIndex The current index of the section being moved
     * @param targetIndex The index where the section should be inserted
     */
    fun reorderSection(sourceIndex: Int, targetIndex: Int) {
        _arc.update { currentArc ->
            reorderSection(currentArc, sourceIndex, targetIndex)
        }
    }

    private fun reorderSection(
        arc: Arc,
        sourceIndex: Int,
        targetIndex: Int
    ): Arc {
        if (sourceIndex !in arc.sections.indices || targetIndex !in 0..arc.sections.size) {
            println("ARC REORDER ERROR: Invalid indices source=$sourceIndex target=$targetIndex")
            return arc
        }

        if (sourceIndex == targetIndex) {
            println("ARC REORDER: No change needed, source equals target ($sourceIndex)")
            return arc
        }

        val sections = arc.sections.toMutableList()
        val movedSection = sections.removeAt(sourceIndex)
        val finalInsertionIndex = if (targetIndex > sourceIndex) targetIndex - 1 else targetIndex
        val boundedIndex = finalInsertionIndex.coerceIn(0, sections.size)
        sections.add(boundedIndex, movedSection)

        println("SECTION REORDERED: Moved section from index $sourceIndex to $boundedIndex (target was $targetIndex)")

        return arc.copy(sections = sections)
    }
}
