package app.lantr.lantr.Models.models_UI

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import app.lantr.lantr.Models.ContentAlignment

val ContentAlignment.textAlignment: TextAlign
    get() = when(this) {
        ContentAlignment.center -> TextAlign.Center
        ContentAlignment.leading -> TextAlign.Start
        ContentAlignment.trailing -> TextAlign.End
    }

val ContentAlignment.composeAlignment: Alignment
    get() = when (this) {
        ContentAlignment.leading -> Alignment.Start
        ContentAlignment.center -> Alignment.Center
        ContentAlignment.trailing -> Alignment.End
    } as Alignment