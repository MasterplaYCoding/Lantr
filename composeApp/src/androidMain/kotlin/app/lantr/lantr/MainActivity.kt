package app.lantr.lantr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import app.lantr.lantr.Models.Presentation
import app.lantr.lantr.Util.AndroidLtrnCoordinator
import app.lantr.lantr.Util.LtrnArchiveService
import app.lantr.lantr.Views.Presentations.PresentationEditView

class MainActivity : ComponentActivity() {
    private var pendingViewUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingViewUri = intent?.ltrnDataUri()

        setContent {
            AndroidAppRoot(
                activity = this,
                initialUri = pendingViewUri
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingViewUri = intent.ltrnDataUri()
    }
}

@Composable
private fun AndroidAppRoot(
    activity: MainActivity,
    initialUri: Uri?
) {
    var openedPresentation by remember { mutableStateOf<Presentation?>(null) }
    var currentFilePath by remember { mutableStateOf<String?>(initialUri?.toString()) }
    var pendingSavePresentation by remember { mutableStateOf<Presentation?>(null) }
    var pendingSaveCallback by remember { mutableStateOf<((String?) -> Unit)?>(null) }
    var pendingImageCallback by remember { mutableStateOf<((String?) -> Unit)?>(null) }

    val openDocumentLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        if (uri != null) {
            openedPresentation = runCatching {
                LtrnArchiveService.loadPresentation(activity, uri)
            }.getOrNull()
            currentFilePath = uri.toString()
        }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(CreateDocument("application/octet-stream")) { uri ->
        val presentation = pendingSavePresentation
        val callback = pendingSaveCallback
        pendingSavePresentation = null
        pendingSaveCallback = null

        if (uri != null && presentation != null) {
            runCatching {
                LtrnArchiveService.savePresentation(activity, uri, presentation)
                currentFilePath = uri.toString()
                callback?.invoke(currentFilePath)
            }.onFailure {
                callback?.invoke(null)
            }
        } else {
            callback?.invoke(null)
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(GetContent()) { uri ->
        val callback = pendingImageCallback
        pendingImageCallback = null

        if (uri != null) {
            callback?.invoke(copyImageUriToCache(activity, uri))
        } else {
            callback?.invoke(null)
        }
    }

    DisposableEffect(Unit) {
        AndroidLtrnCoordinator.cacheDirectory = activity.cacheDir
        AndroidLtrnCoordinator.openDocumentPicker = {
            openDocumentLauncher.launch(arrayOf("*/*"))
        }
        AndroidLtrnCoordinator.openPresentation = { presentation ->
            openedPresentation = presentation
        }
        AndroidLtrnCoordinator.pickImage = { onImageSelected ->
            pendingImageCallback = onImageSelected
            pickImageLauncher.launch("image/*")
        }
        AndroidLtrnCoordinator.savePresentation = { presentation, filePath, onSaved ->
            val targetUri = filePath?.let(Uri::parse)
            if (targetUri != null) {
                runCatching {
                    LtrnArchiveService.savePresentation(activity, targetUri, presentation)
                    onSaved(targetUri.toString())
                }.onFailure {
                    onSaved(null)
                }
            } else {
                pendingSavePresentation = presentation
                pendingSaveCallback = onSaved
                createDocumentLauncher.launch("${presentation.title.ifBlank { "presentation" }}.ltrn")
            }
        }
        AndroidLtrnCoordinator.savePresentationAs = { presentation, suggestedTitle, onSaved ->
            pendingSavePresentation = presentation
            pendingSaveCallback = onSaved
            createDocumentLauncher.launch("${suggestedTitle.ifBlank { "presentation" }}.ltrn")
        }

        onDispose {
            AndroidLtrnCoordinator.cacheDirectory = null
            AndroidLtrnCoordinator.openDocumentPicker = null
            AndroidLtrnCoordinator.openPresentation = null
            AndroidLtrnCoordinator.pickImage = null
            AndroidLtrnCoordinator.savePresentation = null
            AndroidLtrnCoordinator.savePresentationAs = null
        }
    }

    LaunchedEffect(initialUri) {
        if (initialUri != null) {
            openedPresentation = runCatching {
                LtrnArchiveService.loadPresentation(activity, initialUri)
            }.getOrNull()
            currentFilePath = initialUri.toString()
        }
    }

    if (openedPresentation != null) {
        PresentationEditView(
            presentation = openedPresentation!!,
            initialFilePath = currentFilePath,
            onClose = { openedPresentation = null }
        )
    } else {
        App()
    }
}

private fun Intent?.ltrnDataUri(): Uri? {
    if (this == null) return null
    return if (action == Intent.ACTION_VIEW || action == Intent.ACTION_OPEN_DOCUMENT) data else null
}

private fun copyImageUriToCache(activity: MainActivity, uri: Uri): String? {
    val mimeType = activity.contentResolver.getType(uri).orEmpty()
    val extension = when {
        mimeType.contains("png") -> "png"
        mimeType.contains("jpeg") || mimeType.contains("jpg") -> "jpg"
        mimeType.contains("webp") -> "webp"
        mimeType.contains("heic") || mimeType.contains("heif") -> "heic"
        else -> uri.lastPathSegment?.substringAfterLast('.', "").orEmpty().lowercase()
    }.ifBlank { "img" }

    return runCatching {
        val tempFile = java.io.File.createTempFile(
            "lantr-image-",
            ".$extension",
            activity.cacheDir
        )

        activity.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }

        tempFile.absolutePath
    }.getOrNull()
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
