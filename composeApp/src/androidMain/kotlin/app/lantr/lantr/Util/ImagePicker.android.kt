package app.lantr.lantr.Util

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

actual fun requestPickPresentationImage(onImageSelected: (String?) -> Unit) {
    AndroidLtrnCoordinator.requestPickImage(onImageSelected)
}

@Composable
actual fun PlatformImageFromPath(
    imagePath: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    val lastModified = runCatching { java.io.File(imagePath).lastModified() }.getOrDefault(0L)
    val imageBitmap = remember(imagePath, lastModified) {
        runCatching {
            BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
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
