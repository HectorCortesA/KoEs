package com.hector.koes.components.share

import androidx.compose.runtime.Composable
import com.hector.koes.View.OverlayItem

data class ImageTransform(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float,
    val renderedWidth: Float,
    val renderedHeight: Float
)

fun calculateImageTransform(
    containerWidth: Float,
    containerHeight: Float,
    imageWidth: Float,
    imageHeight: Float
): ImageTransform {
    if (containerWidth <= 0f || containerHeight <= 0f || imageWidth <= 0f || imageHeight <= 0f) {
        return ImageTransform(1f, 0f, 0f, containerWidth, containerHeight)
    }
    val scaleX = containerWidth / imageWidth
    val scaleY = containerHeight / imageHeight
    val scale = maxOf(scaleX, scaleY)

    val renderedWidth = imageWidth * scale
    val renderedHeight = imageHeight * scale

    val offsetX = (containerWidth - renderedWidth) / 2f
    val offsetY = (containerHeight - renderedHeight) / 2f

    return ImageTransform(scale, offsetX, offsetY, renderedWidth, renderedHeight)
}

expect fun getImageDimensions(photoBytes: ByteArray?): Pair<Float, Float>

@Composable
expect fun rememberImageCompositor(): (photoBytes: ByteArray?, overlayItems: List<OverlayItem>, previewWidth: Float, previewHeight: Float) -> ByteArray?
