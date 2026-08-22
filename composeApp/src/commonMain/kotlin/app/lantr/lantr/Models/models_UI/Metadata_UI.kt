package app.lantr.lantr.Models.models_UI

import app.lantr.lantr.Models.PresentationDuration

val PresentationDuration.systemIconName: String
    get() = when (this) {
        PresentationDuration.THIRTY_SECONDS -> "bolt.fill"
        PresentationDuration.FIVE_MINUTES -> "play.circle.fill"
        PresentationDuration.FIFTEEN_MINUTES -> "mic.fill"
        PresentationDuration.THIRTY_MINUTES -> "chart.bar.horizontal.page.fill"
        PresentationDuration.OVER_ONE_HOUR -> "person.3.sequence.fill"
    }