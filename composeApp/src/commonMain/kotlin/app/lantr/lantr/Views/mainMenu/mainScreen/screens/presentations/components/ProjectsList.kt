package app.lantr.lantr.Views.mainMenu.mainScreen.screens.presentations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.Arc
import app.lantr.lantr.Models.PredefinedBlockType
import app.lantr.lantr.Models.Presentation
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.StoryBlock
import app.lantr.lantr.Models.StorySection
import app.lantr.lantr.Models.StorySectionType


@Composable
fun ProjectsList(
    projectName: String = "",
    isPhone: Boolean,
    onOpenPresentation: (Presentation) -> Unit = {}
) {

    val density = LocalDensity.current
    var widthDp by remember { mutableStateOf(0.dp) }

    val items = remember {
        listOf(
            "Project 1" to createTestPresentation(),
            "Project 2" to createTestPresentation(),
            "Project 3" to createTestPresentation(),
            "Project 4" to createTestPresentation(),
        )
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .onSizeChanged { size ->
                widthDp = with(density) { size.width.toDp() }
            },
        horizontalArrangement = Arrangement.spacedBy(17.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { (name, presentation) ->
            if (projectName == "" || name.contains(projectName, ignoreCase = true))
                DefaultThumbnail(
                    name = name,
                    widthSize = if (isPhone) (widthDp - 18.dp) / 2 else 174.dp,
                    onClick = { onOpenPresentation(presentation) }
                )
        }
    }
}

private fun createTestPresentation(): Presentation {
    val slide1 = Slide()
    val slide2 = Slide()
    val slide3 = Slide()

    val testBlock = StoryBlock.predefined(
        type = PredefinedBlockType.HIDDEN_ENEMY,
        mainIdea = "Test main idea",
        slides = listOf(slide1, slide2, slide3)
    )

    val testSection = StorySection(
        type = StorySectionType.hook,
        blocks = listOf(testBlock)
    )

    val testArc = Arc(sections = listOf(testSection))

    return Presentation(
        title = "Test Presentation",
        arc = testArc
    )
}