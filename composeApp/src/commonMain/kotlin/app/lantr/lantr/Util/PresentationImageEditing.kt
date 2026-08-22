package app.lantr.lantr.Util

import androidx.compose.ui.graphics.ImageBitmap

data class PreparedPresentationImage(
    val imagePath: String,
    val suggestedHeight: Float
)

data class PresentationImageInfo(
    val width: Int,
    val height: Int
)

data class NormalizedCropRect(
    val leftFraction: Float = 0f,
    val topFraction: Float = 0f,
    val rightFraction: Float = 1f,
    val bottomFraction: Float = 1f
)

expect fun preparePresentationImageForPresentation(
    sourcePath: String,
    presentationId: String
): PreparedPresentationImage?

expect fun registerPresentationImages(
    presentationId: String,
    imagePaths: List<String>
)

expect fun loadPresentationImageInfo(imagePath: String): PresentationImageInfo?

expect fun loadPresentationImageBitmap(imagePath: String): ImageBitmap?

expect fun applyPresentationImageEdits(
    imagePath: String,
    cropRect: NormalizedCropRect,
    rotationQuarterTurns: Int
): PreparedPresentationImage?

expect fun savePresentationImageBitmapToPresentation(
    bitmap: ImageBitmap,
    presentationId: String
): PreparedPresentationImage?

expect fun savePresentationImageBytesToPresentation(
    imageBytes: ByteArray,
    presentationId: String
): PreparedPresentationImage?

fun recommendedPresentationImageHeight(originalHeight: Float): Float {
    if (originalHeight <= 0f) {
        return 320f
    }

    return originalHeight.coerceAtMost(400f)
}
