package app.lantr.lantr.Util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.util.UUID
import kotlin.math.roundToInt

private object AndroidPresentationImageWorkspace {
    private data class ImageEntry(
        val presentationId: String,
        val originalPath: String
    )

    private val imageEntries = linkedMapOf<String, ImageEntry>()

    fun prepareImportedImage(sourcePath: String, presentationId: String): PreparedPresentationImage? {
        val sourceFile = File(sourcePath)
        if (!sourceFile.exists() || !sourceFile.isFile) {
            return null
        }

        val extension = sourceFile.extension.ifBlank { "png" }
        val currentFile = uniqueImageFile(presentationId, extension)
        val originalFile = uniqueOriginalFile(presentationId, extension)
        sourceFile.copyTo(currentFile, overwrite = true)
        sourceFile.copyTo(originalFile, overwrite = true)
        imageEntries[currentFile.absolutePath] = ImageEntry(
            presentationId = presentationId,
            originalPath = originalFile.absolutePath
        )

        val info = loadInfo(currentFile.absolutePath) ?: return null
        return PreparedPresentationImage(
            imagePath = currentFile.absolutePath,
            suggestedHeight = recommendedPresentationImageHeight(info.height.toFloat())
        )
    }

    fun register(presentationId: String, imagePath: String) {
        if (imagePath.isBlank() || imageEntries.containsKey(imagePath)) {
            return
        }

        val currentFile = File(imagePath)
        if (!currentFile.exists() || !currentFile.isFile) {
            return
        }

        val extension = currentFile.extension.ifBlank { "png" }
        val originalFile = uniqueOriginalFile(presentationId, extension)
        currentFile.copyTo(originalFile, overwrite = true)
        imageEntries[imagePath] = ImageEntry(
            presentationId = presentationId,
            originalPath = originalFile.absolutePath
        )
    }

    fun applyEdits(
        imagePath: String,
        cropRect: NormalizedCropRect,
        rotationQuarterTurns: Int
    ): PreparedPresentationImage? {
        val currentFile = File(imagePath)
        if (!currentFile.exists() || !currentFile.isFile) {
            return null
        }

        val imageEntry = imageEntries[imagePath] ?: registerFallback(imagePath)
        val originalBitmap = BitmapFactory.decodeFile(imageEntry.originalPath) ?: return null
        val boundedRect = cropRect.clamp()
        val left = (originalBitmap.width * boundedRect.leftFraction).roundToInt().coerceIn(0, originalBitmap.width - 1)
        val top = (originalBitmap.height * boundedRect.topFraction).roundToInt().coerceIn(0, originalBitmap.height - 1)
        val right = (originalBitmap.width * boundedRect.rightFraction).roundToInt().coerceIn(left + 1, originalBitmap.width)
        val bottom = (originalBitmap.height * boundedRect.bottomFraction).roundToInt().coerceIn(top + 1, originalBitmap.height)
        val cropWidth = (right - left).coerceAtLeast(1)
        val cropHeight = (bottom - top).coerceAtLeast(1)

        val croppedBitmap = Bitmap.createBitmap(originalBitmap, left, top, cropWidth, cropHeight)
        val rotatedBitmap = croppedBitmap.rotateByQuarterTurns(rotationQuarterTurns)
        val editedFile = uniqueImageFile(imageEntry.presentationId, "png")
        editedFile.outputStream().use { output ->
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        imageEntries[editedFile.absolutePath] = imageEntry

        return PreparedPresentationImage(
            imagePath = editedFile.absolutePath,
            suggestedHeight = recommendedPresentationImageHeight(rotatedBitmap.height.toFloat())
        )
    }

    fun loadInfo(imagePath: String): PresentationImageInfo? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(imagePath, options)
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            return null
        }
        return PresentationImageInfo(width = options.outWidth, height = options.outHeight)
    }

    private fun registerFallback(imagePath: String): ImageEntry {
        val file = File(imagePath)
        val presentationId = "orphan"
        val extension = file.extension.ifBlank { "png" }
        val originalFile = uniqueOriginalFile(presentationId, extension)
        file.copyTo(originalFile, overwrite = true)
        return ImageEntry(
            presentationId = presentationId,
            originalPath = originalFile.absolutePath
        ).also { imageEntries[imagePath] = it }
    }

    private fun uniqueImageFile(presentationId: String, extension: String): File {
        return File(currentDirectory(presentationId), "image_${UUID.randomUUID()}.$extension").apply {
            parentFile?.mkdirs()
        }
    }

    private fun uniqueOriginalFile(presentationId: String, extension: String): File {
        return File(originalDirectory(presentationId), "original_${UUID.randomUUID()}.$extension").apply {
            parentFile?.mkdirs()
        }
    }

    private fun currentDirectory(presentationId: String): File {
        return File(baseDirectory(), "$presentationId/current").apply { mkdirs() }
    }

    private fun originalDirectory(presentationId: String): File {
        return File(baseDirectory(), "$presentationId/originals").apply { mkdirs() }
    }

    private fun baseDirectory(): File {
        return File(AndroidLtrnCoordinator.cacheDirectory ?: error("Cache directory unavailable"), "presentation-images")
            .apply { mkdirs() }
    }

    private fun Bitmap.rotateByQuarterTurns(rotationQuarterTurns: Int): Bitmap {
        val normalizedTurns = ((rotationQuarterTurns % 4) + 4) % 4
        if (normalizedTurns == 0) {
            return this
        }

        val matrix = Matrix().apply {
            postRotate(normalizedTurns * 90f)
        }

        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun NormalizedCropRect.clamp(): NormalizedCropRect {
        val left = leftFraction.coerceIn(0f, 0.95f)
        val top = topFraction.coerceIn(0f, 0.95f)
        val right = rightFraction.coerceIn(left + 0.05f, 1f)
        val bottom = bottomFraction.coerceIn(top + 0.05f, 1f)
        return copy(
            leftFraction = left,
            topFraction = top,
            rightFraction = right,
            bottomFraction = bottom
        )
    }
}

actual fun preparePresentationImageForPresentation(
    sourcePath: String,
    presentationId: String
): PreparedPresentationImage? {
    return AndroidPresentationImageWorkspace.prepareImportedImage(sourcePath, presentationId)
}

actual fun registerPresentationImages(
    presentationId: String,
    imagePaths: List<String>
) {
    imagePaths.forEach { imagePath ->
        AndroidPresentationImageWorkspace.register(presentationId, imagePath)
    }
}

actual fun loadPresentationImageInfo(imagePath: String): PresentationImageInfo? {
    return AndroidPresentationImageWorkspace.loadInfo(imagePath)
}

actual fun loadPresentationImageBitmap(imagePath: String): ImageBitmap? {
    return BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
}

actual fun applyPresentationImageEdits(
    imagePath: String,
    cropRect: NormalizedCropRect,
    rotationQuarterTurns: Int
): PreparedPresentationImage? {
    return AndroidPresentationImageWorkspace.applyEdits(
        imagePath = imagePath,
        cropRect = cropRect,
        rotationQuarterTurns = rotationQuarterTurns
    )
}

actual fun savePresentationImageBitmapToPresentation(
    bitmap: ImageBitmap,
    presentationId: String
): PreparedPresentationImage? {
    val tempFile = File.createTempFile("lantr-krop-", ".png", AndroidLtrnCoordinator.cacheDirectory).apply {
        parentFile?.mkdirs()
    }

    tempFile.outputStream().use { output ->
        bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, output)
    }

    return preparePresentationImageForPresentation(
        sourcePath = tempFile.absolutePath,
        presentationId = presentationId
    )
}

actual fun savePresentationImageBytesToPresentation(
    imageBytes: ByteArray,
    presentationId: String
): PreparedPresentationImage? {
    val baseDir = AndroidLtrnCoordinator.cacheDirectory ?: return null
    val tempFile = File(baseDir, "lantr-krop-${UUID.randomUUID()}.png").apply {
        parentFile?.mkdirs()
        writeBytes(imageBytes)
    }
    return preparePresentationImageForPresentation(
        sourcePath = tempFile.absolutePath,
        presentationId = presentationId
    )
}
