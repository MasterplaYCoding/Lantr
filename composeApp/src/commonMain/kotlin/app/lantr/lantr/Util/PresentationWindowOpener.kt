package app.lantr.lantr.Util

import app.lantr.lantr.Models.Presentation

expect val isDesktopPlatform: Boolean

expect fun requestOpenPresentationWindow(presentation: Presentation)

expect fun requestOpenLtrnPresentationWindow()

expect fun requestSaveLtrnPresentation(
    presentation: Presentation,
    currentFilePath: String?,
    onSaved: (String?) -> Unit = {}
)

expect fun requestSaveLtrnPresentationAs(
    presentation: Presentation,
    suggestedTitle: String,
    onSaved: (String?) -> Unit = {}
)
