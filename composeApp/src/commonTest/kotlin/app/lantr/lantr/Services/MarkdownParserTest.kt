package app.lantr.lantr.Services

import app.lantr.lantr.Models.BlockType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarkdownParserTest {
    private val parser = MarkdownParser()

    @Test
    fun recognizesHeadingAndRemovesMarker() {
        val result = parser.parse("  ## A memorable idea  ")

        assertEquals(BlockType.h2, result.detectedType)
        assertEquals("A memorable idea", result.cleanedContent)
        assertTrue(result.wasTransformed)
    }

    @Test
    fun recognizesPresenterNotes() {
        val result = parser.parse("// Pause before revealing the result")

        assertEquals(BlockType.presenterNotes, result.detectedType)
        assertEquals("Pause before revealing the result", result.cleanedContent)
    }

    @Test
    fun preservesOrdinaryBodyContentExactly() {
        val content = "  A sentence with deliberate whitespace  "
        val result = parser.parse(content)

        assertEquals(BlockType.body, result.detectedType)
        assertEquals(content, result.cleanedContent)
        assertFalse(result.wasTransformed)
    }

    @Test
    fun recognizesNumberedAndBulletLists() {
        assertEquals(BlockType.numberedList, parser.parse("1. First point").detectedType)
        assertEquals(BlockType.bulletList, parser.parse("- Supporting point").detectedType)
    }
}
