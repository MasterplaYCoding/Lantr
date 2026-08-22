package app.lantr.lantr.Util

import app.lantr.lantr.Models.Presentation
import java.io.File

actual val isDesktopPlatform: Boolean = false

actual fun requestOpenPresentationWindow(presentation: Presentation) {
    AndroidLtrnCoordinator.openPresentation(presentation)
}

actual fun requestOpenLtrnPresentationWindow() {
    AndroidLtrnCoordinator.requestOpenDocument()
}

actual fun requestSaveLtrnPresentation(
    presentation: Presentation,
    currentFilePath: String?,
    onSaved: (String?) -> Unit
) {
    AndroidLtrnCoordinator.requestSavePresentation(
        presentation = presentation,
        currentFilePath = currentFilePath,
        onSaved = onSaved
    )
}

actual fun requestSaveLtrnPresentationAs(
    presentation: Presentation,
    suggestedTitle: String,
    onSaved: (String?) -> Unit
) {
    AndroidLtrnCoordinator.requestSavePresentationAs(
        presentation = presentation,
        suggestedTitle = suggestedTitle,
        onSaved = onSaved
    )
}

object AndroidLtrnCoordinator {
    var cacheDirectory: File? = null
    var openDocumentPicker: (() -> Unit)? = null
    var savePresentation: ((Presentation, String?, (String?) -> Unit) -> Unit)? = null
    var savePresentationAs: ((Presentation, String, (String?) -> Unit) -> Unit)? = null
    var openPresentation: ((Presentation) -> Unit)? = null
    var pickImage: (((String?) -> Unit) -> Unit)? = null

    fun requestOpenDocument() {
        openDocumentPicker?.invoke()
    }

    fun requestSavePresentation(
        presentation: Presentation,
        currentFilePath: String?,
        onSaved: (String?) -> Unit
    ) {
        savePresentation?.invoke(presentation, currentFilePath, onSaved) ?: onSaved(null)
    }

    fun requestSavePresentationAs(
        presentation: Presentation,
        suggestedTitle: String,
        onSaved: (String?) -> Unit
    ) {
        savePresentationAs?.invoke(presentation, suggestedTitle, onSaved) ?: onSaved(null)
    }

    fun openPresentation(presentation: Presentation) {
        openPresentation?.invoke(presentation)
    }

    fun requestPickImage(onImageSelected: (String?) -> Unit) {
        pickImage?.invoke(onImageSelected) ?: onImageSelected(null)
    }
}
