package app.lantr.lantr.ViewModel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lantr.lantr.Models.Presentation
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.Themes.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PresentationViewModel(
     initialPresentation: Presentation = Presentation()
) : ViewModel() {

    private val _presentation = MutableStateFlow(initialPresentation)
    val presentation: StateFlow<Presentation> = _presentation.asStateFlow()

    private val _selectedSlideIndex = MutableStateFlow(0)
    val selectedSlideIndex: StateFlow<Int> = _selectedSlideIndex.asStateFlow()

    private val _selectedSlideHasOverflow = MutableStateFlow(false)
    val selectedSlideHasOverflow: StateFlow<Boolean> = _selectedSlideHasOverflow.asStateFlow()

    val allSlides: StateFlow<List<Slide>> = presentation.map { it.arc.blocks.flatMap { block -> block.slides } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedSlide: StateFlow<Slide?> = combine(allSlides, selectedSlideIndex) { slides, index ->
        slides.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    /*
    fun loadPresentation(presentationId: UUID) {
        viewModelScope.launch {
            repository.getPresentation(presentationId)?.let { presentation ->
                _presentation.value = presentation
            }
        }
    }
    */

    fun selectSlide(index: Int) {
        val maxIndex = allSlides.value.size - 1
        if (index in 0..maxIndex) {
            _selectedSlideIndex.value = index
        }
    }

    fun selectSlide(slideId: String) {
        val slides = allSlides.value
        val index = slides.indexOfFirst { it.id == slideId }
        if (index >= 0) {
            _selectedSlideIndex.value = index
        }
    }

    fun selectNextSlide(): Boolean {
        val currentIndex = _selectedSlideIndex.value
        val maxIndex = allSlides.value.size - 1
        return if (currentIndex < maxIndex) {
            _selectedSlideIndex.value = currentIndex + 1
            true
        } else false
    }

    fun selectPreviousSlide(): Boolean {
        val currentIndex = _selectedSlideIndex.value
        return if (currentIndex > 0) {
            _selectedSlideIndex.value = currentIndex - 1
            true
        } else false
    }

    fun updateSelectedSlide(updatedSlide: Slide) {
        updateSlideInPresentation(updatedSlide)
    }

    fun addSlideAfterCurrent(usesAltScheme: Boolean = false) {
        val currentSlide = selectedSlide.value ?: return
        val newSlide = Slide(usesAltScheme = usesAltScheme)
        addSlideAfterSlide(currentSlide, newSlide)
    }

    fun deleteSlide(slideId: String): Boolean {
        val (slide, location) = findSlideLocation(slideId) ?: return false
        val allSlidesList = allSlides.value
        val deletingIndex = allSlidesList.indexOfFirst { it.id == slideId }

        if (allSlidesList.size <= 1) return false

        // Remove slide from presentation structure
        val updatedPresentation = removeSlideFromPresentation(slideId)
        _presentation.value = updatedPresentation

        // Update selection after deletion
        updateSelectionAfterDeletion(deletingIndex)

        return true
    }

    fun moveSlide(sourceSlideId: String, targetBlockId: String, targetIndex: Int? = null): Boolean {
        // Implementation for cross-block slide movement
        // Similar to Swift implementation but adapted for Kotlin
        return try {
            val updatedPresentation = performSlideMove(sourceSlideId, targetBlockId, targetIndex)
            _presentation.value = updatedPresentation
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setSlideOverflow(hasOverflow: Boolean) {
        _selectedSlideHasOverflow.value = hasOverflow
    }

    fun updateTheme(theme: Theme) {
        val updatedPresentation = _presentation.value.copy(theme = theme).apply {
            updateModifiedDate()
        }
        _presentation.value = updatedPresentation

    }

    // Private helper methods
    private fun addSlideAfterSlide(targetSlide: Slide, newSlide: Slide) {
        val currentPresentation = _presentation.value

        // Find the block containing the target slide and add the new slide after it
        val updatedSections = currentPresentation.arc.sections.map { section ->
            val updatedBlocks = section.blocks.map { block ->
                val targetIndex = block.slides.indexOfFirst { it.id == targetSlide.id }
                if (targetIndex >= 0) {
                    // Found the target slide in this block
                    val updatedSlides = block.slides.toMutableList().apply {
                        add(targetIndex + 1, newSlide)
                    }
                    block.copy(slides = updatedSlides)
                } else {
                    block
                }
            }
            section.copy(blocks = updatedBlocks)
        }

        val updatedArc = currentPresentation.arc.copy(sections = updatedSections)
        val updatedPresentation = currentPresentation.copy(arc = updatedArc).apply {
            updateModifiedDate()
        }

        _presentation.value = updatedPresentation

        // Select the newly added slide
        val newSlidesList = updatedPresentation.arc.blocks.flatMap { it.slides }
        val newSlideIndex = newSlidesList.indexOfFirst { it.id == newSlide.id }
        if (newSlideIndex >= 0) {
            _selectedSlideIndex.value = newSlideIndex
        }
    }

    private fun updateSlideInPresentation(updatedSlide: Slide) {
        val currentPresentation = _presentation.value

        // Find and update the slide in the arc structure
        val updatedSections = currentPresentation.arc.sections.map { section ->
            val updatedBlocks = section.blocks.map { block ->
                val updatedSlides = block.slides.map { slide ->
                    if (slide.id == updatedSlide.id) updatedSlide else slide
                }
                block.copy(slides = updatedSlides)
            }
            section.copy(blocks = updatedBlocks)
        }

        val updatedArc = currentPresentation.arc.copy(sections = updatedSections)
        val updatedPresentation = currentPresentation.copy(arc = updatedArc)

        _presentation.value = updatedPresentation
    }

    private fun findSlideLocation(slideId: String): Pair<Slide, SlideLocation>? {
        // Locate slide within the presentation structure
        // Return slide and its hierarchical location
        return null // Implementation needed
    }

    private fun updateSelectionAfterDeletion(deletedIndex: Int) {
        val newSlideCount = allSlides.value.size
        when {
            newSlideCount == 0 -> _selectedSlideIndex.value = 0
            deletedIndex <= _selectedSlideIndex.value -> {
                val newIndex = maxOf(0, _selectedSlideIndex.value - 1)
                _selectedSlideIndex.value = minOf(newIndex, newSlideCount - 1)
            }
            else -> {
                _selectedSlideIndex.value = minOf(_selectedSlideIndex.value, newSlideCount - 1)
            }
        }
    }

    private fun removeSlideFromPresentation(slideId: String): Presentation {
        // Remove slide and return updated presentation
        // Implementation needed
        return _presentation.value
    }

    private fun performSlideMove(sourceSlideId: String, targetBlockId: String, targetIndex: Int?): Presentation {
        // Perform slide movement and return updated presentation
        // Implementation needed
        return _presentation.value
    }
}

data class SlideLocation(
    val sectionIndex: Int,
    val blockIndex: Int,
    val slideIndex: Int,
    val blockId: String
)