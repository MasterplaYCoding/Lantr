package app.lantr.lantr.Models.models_UI

import app.lantr.lantr.Models.BlockType

val BlockType.displayName: String
    get() = when (this) {
        BlockType.h1 -> "Heading 1"
        BlockType.h2 -> "Heading 2"
        BlockType.h3 -> "Heading 3"
        BlockType.body -> "Body Text"
        BlockType.quote -> "Quote"
        BlockType.bulletList -> "Bullet List"
        BlockType.numberedList -> "Numbered List"
        BlockType.presenterNotes -> "Presenter Notes"
        BlockType.image -> "Image"
    }

val BlockType.iconName: String
    get() = when (this) {
        BlockType.h1 -> "textformat.size.larger"
        BlockType.h2 -> "textformat.size"
        BlockType.h3 -> "textformat.size.smaller"
        BlockType.body -> "text.alignleft"
        BlockType.quote -> "quote.opening"
        BlockType.bulletList -> "list.bullet"
        BlockType.numberedList -> "list.number"
        BlockType.presenterNotes -> "note.text"
        BlockType.image -> "photo"
    }

val BlockType.placeholderText: String
    get() = when (this) {
        BlockType.h1 -> "Enter a title..."
        BlockType.h2 -> "Enter a heading..."
        BlockType.h3 -> "Enter a subtitle..."
        BlockType.body -> "Start typing here..."
        BlockType.quote -> "Enter a quote or callout..."
        BlockType.bulletList -> "- Start your list..."
        BlockType.numberedList -> "1. Start your numbered list..."
        BlockType.presenterNotes -> "Write your speaker notes..."
        BlockType.image -> "Select an image..."
    }

val BlockType.isListType: Boolean
    get() = this == BlockType.bulletList || this == BlockType.numberedList

val BlockType.isImageType: Boolean
    get() = this == BlockType.image
