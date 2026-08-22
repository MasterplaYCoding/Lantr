package app.lantr.lantr.Util

import kotlin.test.Test
import kotlin.test.assertEquals

class PresentationImageEditingTest {
    @Test
    fun recommendedHeightCapsLargeImages() {
        assertEquals(400f, recommendedPresentationImageHeight(1_200f))
    }

    @Test
    fun recommendedHeightKeepsSmallImagesReadable() {
        assertEquals(120f, recommendedPresentationImageHeight(120f))
        assertEquals(320f, recommendedPresentationImageHeight(0f))
    }

    @Test
    fun cropRectDefaultsToTheWholeImage() {
        assertEquals(
            NormalizedCropRect(0f, 0f, 1f, 1f),
            NormalizedCropRect()
        )
    }
}
