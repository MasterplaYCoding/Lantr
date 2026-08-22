package app.lantr.lantr.Util

import androidx.compose.runtime.mutableStateListOf
import app.lantr.lantr.Models.Presentation
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

data class PresentationWindowSession(
    val presentation: Presentation,
    val filePath: String? = null
)

object PresentationWindowState {
    val openPresentations = mutableStateListOf<PresentationWindowSession>()

    fun open(presentation: Presentation, filePath: String? = null) {
        if (openPresentations.none { it.presentation.id == presentation.id }) {
            openPresentations.add(PresentationWindowSession(presentation, filePath))
        }
    }

    fun open(session: PresentationWindowSession) {
        if (openPresentations.none { it.presentation.id == session.presentation.id }) {
            openPresentations.add(session)
        }
    }

    fun openAll(sessions: List<PresentationWindowSession>) {
        sessions.forEach(::open)
    }

    fun close(session: PresentationWindowSession) {
        openPresentations.remove(session)
    }
}

actual val isDesktopPlatform: Boolean = true

actual fun requestOpenPresentationWindow(presentation: Presentation) {
    PresentationWindowState.open(presentation)
}

actual fun requestOpenLtrnPresentationWindow() {
    chooseLtrnFileToOpen()?.let { file ->
        runCatching {
            LtrnArchiveService.loadPresentation(file)
        }.onSuccess { presentation ->
            PresentationWindowState.open(presentation, file.absolutePath)
        }.onFailure { throwable ->
            throwable.printStackTrace()
        }
    }
}

actual fun requestSaveLtrnPresentation(
    presentation: Presentation,
    currentFilePath: String?,
    onSaved: (String?) -> Unit
) {
    val targetFile = currentFilePath?.let(::File) ?: chooseLtrnFileToSave(presentation.title)
    if (targetFile == null) {
        onSaved(null)
        return
    }

    val savedPath = runCatching {
        LtrnArchiveService.savePresentation(targetFile, presentation)
        targetFile.absolutePath
    }.onFailure { throwable ->
        throwable.printStackTrace()
    }.getOrNull()

    onSaved(savedPath)
}

actual fun requestSaveLtrnPresentationAs(
    presentation: Presentation,
    suggestedTitle: String,
    onSaved: (String?) -> Unit
) {
    val targetFile = chooseLtrnFileToSave(suggestedTitle)
    if (targetFile == null) {
        onSaved(null)
        return
    }

    val savedPath = runCatching {
        LtrnArchiveService.savePresentation(targetFile, presentation)
        targetFile.absolutePath
    }.onFailure { throwable ->
        throwable.printStackTrace()
    }.getOrNull()

    onSaved(savedPath)
}

fun loadLtrnSessionsFromArgs(args: Array<String>): List<PresentationWindowSession> {
    return loadLtrnSessionsFromPaths(args.ltrnFilePaths())
}

fun loadLtrnSessionsFromPaths(paths: List<String>): List<PresentationWindowSession> {
    return paths.asSequence()
        .map(::File)
        .filter { it.exists() && it.isFile && it.extension.equals("ltrn", ignoreCase = true) }
        .mapNotNull { file ->
            runCatching {
                PresentationWindowSession(
                    presentation = LtrnArchiveService.loadPresentation(file),
                    filePath = file.absolutePath
                )
            }.onFailure { throwable ->
                throwable.printStackTrace()
            }.getOrNull()
        }
        .toList()
}

private fun chooseLtrnFileToOpen(): File? {
    val dialog = FileDialog(Frame(), "Open Lantr Presentation", FileDialog.LOAD).apply {
        file = "*.ltrn"
        isVisible = true
    }

    val selectedFile = dialog.file ?: return null
    val selectedDir = dialog.directory ?: return null

    val result = File(selectedDir, selectedFile)

    return if (result.name.lowercase().endsWith(".ltrn")) {
        result
    } else {
        null
    }
}

private fun chooseLtrnFileToSave(suggestedTitle: String): File? {
    val chooser = JFileChooser().apply {
        dialogTitle = "Save .ltrn Presentation"
        fileFilter = FileNameExtensionFilter("Lantr Presentation (*.ltrn)", "ltrn")
        isAcceptAllFileFilterUsed = false
        selectedFile = File("${sanitizeFileName(suggestedTitle)}.ltrn")
    }

    return if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile?.withLtrnExtension()
    } else {
        null
    }
}

private fun File.withLtrnExtension(): File {
    return if (extension.equals("ltrn", ignoreCase = true)) {
        this
    } else {
        File(parentFile ?: File("."), "$name.ltrn")
    }
}

private fun sanitizeFileName(value: String): String {
    return value.ifBlank { "presentation" }
        .replace(Regex("[\\\\/:*?\"<>|]"), "_")
        .trim()
        .ifBlank { "presentation" }
}
