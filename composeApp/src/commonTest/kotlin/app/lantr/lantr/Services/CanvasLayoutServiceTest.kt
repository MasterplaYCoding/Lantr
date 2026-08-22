package app.lantr.lantr.Services

import androidx.compose.ui.geometry.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CanvasLayoutServiceTest {
    private val service = CanvasLayoutService()

    @Test
    fun desktopUsesWideLayoutAboveThreshold() {
        val layout = service.calculateLayout(
            availableSize = Size(900f, 600f),
            density = 1f,
            isDesktop = true
        )

        assertTrue(layout.isUsingWideLayout)
        assertEquals(846f, layout.availableSize.width)
        assertEquals(554f, layout.availableSize.height)
    }

    @Test
    fun androidRequiresLandscapeAndEnoughWidth() {
        val portrait = service.calculateLayout(Size(800f, 1_000f), isDesktop = false)
        val landscape = service.calculateLayout(Size(800f, 600f), isDesktop = false)

        assertFalse(portrait.isUsingWideLayout)
        assertTrue(landscape.isUsingWideLayout)
    }

    @Test
    fun contentAreaNeverCollapsesBelowMinimum() {
        val layout = service.calculateLayout(Size(20f, 20f))

        assertEquals(Size(100f, 100f), layout.availableSize)
    }
}
