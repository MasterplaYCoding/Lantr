package app.lantr.lantr.Services

import app.lantr.lantr.Models.BlockType


class MarkdownParser {
    data class ParseResult(
        val detectedType: BlockType,
        val cleanedContent: String,
        val wasTransformed: Boolean
    )

    fun parse(content: String): ParseResult {
        val trimmed = content.trim()

        // Check for markdown patterns
        return when {
            trimmed.startsWith("# ") -> {
                ParseResult(
                    detectedType = BlockType.h1,
                    cleanedContent = trimmed.drop(2),
                    wasTransformed = true
                )
            }
            trimmed.startsWith("## ") -> {
                ParseResult(
                    detectedType = BlockType.h2,
                    cleanedContent = trimmed.drop(3),
                    wasTransformed = true
                )
            }
            trimmed.startsWith("### ") -> {
                ParseResult(
                    detectedType = BlockType.h3,
                    cleanedContent = trimmed.drop(4),
                    wasTransformed = true
                )
            }
            trimmed.startsWith("- ") -> {
                ParseResult(
                    detectedType = BlockType.bulletList,
                    cleanedContent = trimmed,
                    wasTransformed = true
                )
            }
            trimmed.matches(Regex("^\\d+\\. .*")) -> {
                ParseResult(
                    detectedType = BlockType.numberedList,
                    cleanedContent = trimmed,
                    wasTransformed = true
                )
            }
            trimmed.startsWith(">> ") -> {
                ParseResult(
                    detectedType = BlockType.quote,
                    cleanedContent = trimmed.drop(3),
                    wasTransformed = true
                )
            }
            trimmed.startsWith("// ") -> {
                ParseResult(
                    detectedType = BlockType.presenterNotes,
                    cleanedContent = trimmed.drop(3),
                    wasTransformed = true
                )
            }
            else -> {
                ParseResult(
                    detectedType = BlockType.body,
                    cleanedContent = content,
                    wasTransformed = false
                )
            }
        }
    }
}