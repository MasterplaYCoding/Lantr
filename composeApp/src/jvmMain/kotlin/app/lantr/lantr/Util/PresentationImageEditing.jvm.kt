package app.lantr.lantr.Util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.File
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.nio.file.Files
import java.util.UUID
import kotlin.math.roundToInt
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Rect
import org.jetbrains.skia.SamplingMode
import org.jetbrains.skia.Surface
import org.jetbrains.skia.Image as SkiaImage

private object DesktopPresentationImageWorkspace {
    private val rootDirectory = Files.createTempDirectory("lantr-presentation-images-").toFile().apply {
        deleteOnExit()
    }

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

        val imageInfo = loadInfo(currentFile.absolutePath) ?: return null
        return PreparedPresentationImage(
            imagePath = currentFile.absolutePath,
            suggestedHeight = recommendedPresentationImageHeight(imageInfo.height.toFloat())
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
        val sourceImage = decodeImage(imageEntry.originalPath) ?: return null
        val croppedImage = cropImage(sourceImage, cropRect)
        val rotatedImage = rotateImage(croppedImage, rotationQuarterTurns)
        val editedFile = uniqueImageFile(imageEntry.presentationId, "png")
        val encoded = rotatedImage.encodeToData(EncodedImageFormat.PNG, 100) ?: return null
        editedFile.writeBytes(encoded.bytes)
        imageEntries[editedFile.absolutePath] = imageEntry

        return PreparedPresentationImage(
            imagePath = editedFile.absolutePath,
            suggestedHeight = recommendedPresentationImageHeight(rotatedImage.height.toFloat())
        )
    }

    fun loadInfo(imagePath: String): PresentationImageInfo? {
        val image = decodeImage(imagePath) ?: return null
        return PresentationImageInfo(width = image.width, height = image.height)
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
            deleteOnExit()
        }
    }

    private fun uniqueOriginalFile(presentationId: String, extension: String): File {
        return File(originalDirectory(presentationId), "original_${UUID.randomUUID()}.$extension").apply {
            parentFile?.mkdirs()
            deleteOnExit()
        }
    }

    private fun currentDirectory(presentationId: String): File {
        return File(rootDirectory, "$presentationId/current").apply {
            mkdirs()
            deleteOnExit()
        }
    }

    private fun originalDirectory(presentationId: String): File {
        return File(rootDirectory, "$presentationId/originals").apply {
            mkdirs()
            deleteOnExit()
        }
    }

    private fun decodeImage(imagePath: String): SkiaImage? {
        return runCatching {
            SkiaImage.makeFromEncoded(File(imagePath).readBytes())
        }.getOrNull()
    }

    private fun cropImage(image: SkiaImage, cropRect: NormalizedCropRect): SkiaImage {
        val boundedRect = cropRect.clamp()
        val left = (image.width * boundedRect.leftFraction).roundToInt().coerceIn(0, image.width - 1)
        val top = (image.height * boundedRect.topFraction).roundToInt().coerceIn(0, image.height - 1)
        val right = (image.width * boundedRect.rightFraction).roundToInt().coerceIn(left + 1, image.width)
        val bottom = (image.height * boundedRect.bottomFraction).roundToInt().coerceIn(top + 1, image.height)
        val cropWidth = (right - left).coerceAtLeast(1)
        val cropHeight = (bottom - top).coerceAtLeast(1)
        val surface = Surface.makeRasterN32Premul(cropWidth, cropHeight)
        surface.canvas.drawImageRect(
            image,
            Rect.makeLTRB(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat()),
            Rect.makeWH(cropWidth.toFloat(), cropHeight.toFloat()),
            SamplingMode.DEFAULT,
            null,
            true
        )
        return surface.makeImageSnapshot()
    }

    private fun rotateImage(image: SkiaImage, rotationQuarterTurns: Int): SkiaImage {
        val normalizedTurns = ((rotationQuarterTurns % 4) + 4) % 4
        if (normalizedTurns == 0) {
            return image
        }

        val targetWidth = if (normalizedTurns % 2 == 0) image.width else image.height
        val targetHeight = if (normalizedTurns % 2 == 0) image.height else image.width
        val surface = Surface.makeRasterN32Premul(targetWidth, targetHeight)
        val canvas = surface.canvas

        canvas.translate(targetWidth / 2f, targetHeight / 2f)
        canvas.rotate(normalizedTurns * 90f)
        canvas.translate(-image.width / 2f, -image.height / 2f)
        canvas.drawImageRect(
            image,
            Rect.makeXYWH(0f, 0f, image.width.toFloat(), image.height.toFloat()),
            Rect.makeXYWH(0f, 0f, image.width.toFloat(), image.height.toFloat()),
            SamplingMode.DEFAULT,
            null,
            true
        )

        return surface.makeImageSnapshot()
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
    return DesktopPresentationImageWorkspace.prepareImportedImage(sourcePath, presentationId)
}

actual fun registerPresentationImages(
    presentationId: String,
    imagePaths: List<String>
) {
    imagePaths.forEach { imagePath ->
        DesktopPresentationImageWorkspace.register(presentationId, imagePath)
    }
}

actual fun loadPresentationImageInfo(imagePath: String): PresentationImageInfo? {
    return DesktopPresentationImageWorkspace.loadInfo(imagePath)
}

actual fun loadPresentationImageBitmap(imagePath: String): ImageBitmap? {
    return runCatching {
        SkiaImage.makeFromEncoded(File(imagePath).readBytes()).toComposeImageBitmap()
    }.getOrNull()
}

actual fun applyPresentationImageEdits(
    imagePath: String,
    cropRect: NormalizedCropRect,
    rotationQuarterTurns: Int
): PreparedPresentationImage? {
    return DesktopPresentationImageWorkspace.applyEdits(
        imagePath = imagePath,
        cropRect = cropRect,
        rotationQuarterTurns = rotationQuarterTurns
    )
}

actual fun savePresentationImageBitmapToPresentation(
    bitmap: ImageBitmap,
    presentationId: String
): PreparedPresentationImage? {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= 0 || height <= 0) {
        return null
    }

    val pixels = IntArray(width * height)
    bitmap.readPixels(
        buffer = pixels,
        startX = 0,
        startY = 0,
        width = width,
        height = height,
        bufferOffset = 0,
        stride = width
    )

    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    bufferedImage.setRGB(0, 0, width, height, pixels, 0, width)

    val tempFile = File.createTempFile("lantr-krop-", ".png").apply {
        deleteOnExit()
    }
    ImageIO.write(bufferedImage, "png", tempFile)

    return preparePresentationImageForPresentation(
        sourcePath = tempFile.absolutePath,
        presentationId = presentationId
    )
}

actual fun savePresentationImageBytesToPresentation(
    imageBytes: ByteArray,
    presentationId: String
): PreparedPresentationImage? {
    val tempFile = File.createTempFile("lantr-krop-", ".png").apply {
        writeBytes(imageBytes)
        deleteOnExit()
    }
    return preparePresentationImageForPresentation(
        sourcePath = tempFile.absolutePath,
        presentationId = presentationId
    )
}
