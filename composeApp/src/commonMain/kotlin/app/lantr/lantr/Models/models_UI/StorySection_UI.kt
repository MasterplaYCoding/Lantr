package app.lantr.lantr.Models.models_UI

import app.lantr.lantr.Models.StorySection
import app.lantr.lantr.Models.StorySectionType
import app.lantr.lantr.Theme.StoryCategoryRed
import app.lantr.lantr.Theme.StoryCategoryPurple
import app.lantr.lantr.Theme.StoryCategoryBlue
import androidx.compose.ui.graphics.Color

val StorySection.tintColor: Color
    get() = when (this.type) {
        StorySectionType.hook,
        StorySectionType.close -> StoryCategoryRed

        StorySectionType.stakes,
        StorySectionType.core -> StoryCategoryPurple

        StorySectionType.turn,
        StorySectionType.audience -> StoryCategoryBlue
    }