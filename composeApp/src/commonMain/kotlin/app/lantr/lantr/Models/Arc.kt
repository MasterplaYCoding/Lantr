package app.lantr.lantr.Models

data class Arc(
    val sections: List<StorySection> = emptyList(),

    ){
    val blocks: List<StoryBlock>
        get() = sections.flatMap { it.blocks }
}