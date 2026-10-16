@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.hector.koes.components.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hector.koes.View.OverlayItem
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIImage
import platform.UIKit.UIScreen
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.drawAtPoint
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGSizeMake
import platform.posix.memcpy
import kotlinx.cinterop.useContents
import kotlin.math.min
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private fun iosColor(r: Int, g: Int, b: Int): UIColor = UIColor(
    red = r / 255.0, green = g / 255.0, blue = b / 255.0, alpha = 1.0
)

private const val OUT_W = 1080.0
private const val OUT_H = 1920.0

private fun ByteArray.asNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
}

private fun NSData.asByteArray(): ByteArray {
    val count = length.toInt()
    if (count == 0) return byteArrayOf()
    val result = ByteArray(count)
    result.usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, length) }
    return result
}

actual fun getImageDimensions(photoBytes: ByteArray?): Pair<Float, Float> {
    val image = photoBytes?.takeIf { it.isNotEmpty() }?.let { UIImage(data = it.asNSData()) }
    // UIImage.size ya toma en cuenta la orientación de la imagen para mostrarla.
    return image?.let { it.size.useContents { width.toFloat() to height.toFloat() } }
        ?: (1080f to 1920f)
}

@Composable
actual fun rememberImageCompositor(): (
    photoBytes: ByteArray?,
    overlayItems: List<OverlayItem>,
    previewWidth: Float,
    previewHeight: Float,
    dailyWordCounts: Map<Int, Int>
) -> ByteArray? = remember {
    { photoBytes, overlayItems, previewWidth, previewHeight, dailyWordCounts ->
        val image = photoBytes?.takeIf { it.isNotEmpty() }?.let { UIImage(data = it.asNSData()) }
        if (image == null) {
            null
        } else {
            UIGraphicsBeginImageContextWithOptions(CGSizeMake(OUT_W, OUT_H), true, 1.0)
            try {
                UIColor.blackColor.setFill()
                UIBezierPath.bezierPathWithRect(CGRectMake(0.0, 0.0, OUT_W, OUT_H)).fill()
                // Coincide con ContentScale.Crop del editor: escala uniforme y recorte centrado.
                val imageDimensions = image.size.useContents { width to height }
                val width: Double = imageDimensions.first
                val height: Double = imageDimensions.second
                val scale: Double = maxOf(OUT_W / width, OUT_H / height)
                val dw = width * scale
                val dh = height * scale
                image.drawInRect(CGRectMake((OUT_W - dw) / 2.0, (OUT_H - dh) / 2.0, dw, dh))

                // Las posiciones normalizadas utilizan como origen la esquina superior izquierda.
                val textSize = if (previewHeight > 0f) {
                    22.0 * UIScreen.mainScreen.scale * (OUT_H / previewHeight.toDouble())
                } else 54.0
                val calWidth: Double = if (previewWidth > 0f) {
                    min(260.0 * UIScreen.mainScreen.scale, previewWidth.toDouble() * 0.72) * (OUT_W / previewWidth.toDouble())
                } else 500.0
                // Misma geometría del HeatCalendar de Compose:
                // padding 16dp, espacios 8dp, celdas 23:27 y 12x5.
                val unit = calWidth / 260.0
                val padding = 16.0 * unit
                val gap = 8.0 * unit
                val cellW = (calWidth - 2.0 * padding - 11.0 * gap) / 12.0
                val cellH = cellW * 27.0 / 23.0
                val calHeight = 2.0 * padding + 5.0 * cellH + 4.0 * gap

                overlayItems.forEach { item ->
                    val x = item.normalizedX.coerceIn(0f, 1f).toDouble() * OUT_W
                    val y = item.normalizedY.coerceIn(0f, 1f).toDouble() * OUT_H
                    if (item.isCalendar) {
                        UIColor.whiteColor.colorWithAlphaComponent(0.8).setFill()
                        UIBezierPath.bezierPathWithRoundedRect(
                            CGRectMake(x, y, calWidth, calHeight),
                            cornerRadius = 10.0 * unit
                        ).fill()

                        for (row in 0 until 5) {
                            for (col in 0 until 12) {
                                val index = col * 5 + row
                                val count = dailyWordCounts[index] ?: 0
                                val left = x + padding + col * (cellW + gap)
                                val top = y + padding + row * (cellH + gap)
                                if (count <= 0) {
                                    iosColor(0xB3, 0xD2, 0xDC).setFill()
                                    UIBezierPath.bezierPathWithRoundedRect(
                                        CGRectMake(left, top, cellW, cellH),
                                        cornerRadius = 3.0 * unit
                                    ).fill()
                                } else {
                                    val (primary, alternate, center) = when {
                                        count >= 10 -> Triple(
                                            iosColor(0x7A, 0x28, 0x7A),
                                            iosColor(0x6B, 0x18, 0x63),
                                            iosColor(0xF7, 0xCF, 0xE1)
                                        )
                                        count >= 5 -> Triple(
                                            iosColor(0xC0, 0x58, 0xA8),
                                            iosColor(0xA8, 0x48, 0x93),
                                            iosColor(0xF7, 0xCF, 0xE1)
                                        )
                                        else -> Triple(
                                            iosColor(0xF7, 0xCF, 0xE1),
                                            iosColor(0xE8, 0x82, 0xAD),
                                            iosColor(0xC7, 0x5E, 0x5B)
                                        )
                                    }
                                    // Flower() ocupa el 85% de la celda.
                                    val flowerSize = min(cellW, cellH) * 0.85
                                    val cx = left + cellW / 2.0
                                    val cy = top + cellH / 2.0
                                    val petalRadius = flowerSize * 0.28
                                    val petalDistance = flowerSize * 0.26
                                    val angles = listOf(-90.0, -18.0, 54.0, 126.0, 198.0)
                                    angles.forEachIndexed { i, degrees ->
                                        val radians = degrees * PI / 180.0
                                        val px = cx + petalDistance * cos(radians)
                                        val py = cy + petalDistance * sin(radians)
                                        (if (i % 2 == 0) primary else alternate).setFill()
                                        UIBezierPath.bezierPathWithOvalInRect(
                                            CGRectMake(px - petalRadius, py - petalRadius,
                                                petalRadius * 2.0, petalRadius * 2.0)
                                        ).fill()
                                    }
                                    val centerRadius = flowerSize * 0.16
                                    center.setFill()
                                    UIBezierPath.bezierPathWithOvalInRect(
                                        CGRectMake(cx - centerRadius, cy - centerRadius,
                                            centerRadius * 2.0, centerRadius * 2.0)
                                    ).fill()
                                }
                            }
                        }
                    } else {
                        val attrs = mapOf<Any?, Any>(
                            NSFontAttributeName to UIFont.boldSystemFontOfSize(textSize),
                            NSForegroundColorAttributeName to UIColor.whiteColor
                        )
                        NSString.create(string = item.text).drawAtPoint(CGPointMake(x, y), withAttributes = attrs)
                    }
                }
                UIGraphicsGetImageFromCurrentImageContext()?.let { result ->
                    UIImagePNGRepresentation(result)?.asByteArray()
                }
            } finally {
                UIGraphicsEndImageContext()
            }
        }
    }
}
