package app.lantr.lantr.Util

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import org.jetbrains.skia.Image as SkiaImage

actual fun requestPickPresentationImage(onImageSelected: (String?) -> Unit) {
    val dialog = FileDialog(Frame(), "Choose Image", FileDialog.LOAD).apply {
        file = "*.png;*.jpg;*.jpeg;*.webp;*.heic"
        isVisible = true
    }

    val selectedFile = dialog.file
    val selectedDir = dialog.directory

    if (selectedFile == null || selectedDir == null) {
        onImageSelected(null)
        return
    }

    val file = File(selectedDir, selectedFile)
    val allowedExtensions = setOf("png", "jpg", "jpeg", "webp", "heic")
    onImageSelected(
        file.absolutePath.takeIf {
            file.exists() && file.isFile && file.extension.lowercase() in allowedExtensions
        }
    )
}

@Composable
actual fun PlatformImageFromPath(
    imagePath: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    val lastModified = runCatching { File(imagePath).lastModified() }.getOrDefault(0L)
    val imageBitmap = remember(imagePath, lastModified) {
        runCatching {
            SkiaImage.makeFromEncoded(File(imagePath).readBytes()).toComposeImageBitmap()
        }.getOrNull()
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}
