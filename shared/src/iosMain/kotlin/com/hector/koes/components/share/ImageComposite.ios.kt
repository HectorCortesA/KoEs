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
                val calHeight = calWidth * 360.0 / 500.0

                overlayItems.forEach { item ->
                    val x = item.normalizedX.coerceIn(0f, 1f).toDouble() * OUT_W
                    val y = item.normalizedY.coerceIn(0f, 1f).toDouble() * OUT_H
                    if (item.isCalendar) {
                        val rect = CGRectMake(x, y, calWidth, calHeight)
                        UIColor.whiteColor.colorWithAlphaComponent(0.9).setFill()
                        UIBezierPath.bezierPathWithRoundedRect(rect, cornerRadius = 32.0).fill()
                        val padding = 24.0
                        val cols = 12
                        val rows = 5
                        val cellW = (calWidth - padding * 2) / cols
                        val cellH = (calHeight - padding * 2) / rows
                        for (col in 0 until cols) for (row in 0 until rows) {
                            val index = col * rows + row
                            val left = x + padding + col * cellW + 2
                            val top = y + padding + row * cellH + 2
                            val w = cellW - 4
                            val h = cellH - 4
                            if ((dailyWordCounts[index] ?: 0) > 0) {
                                UIColor(red = 0.753, green = 0.345, blue = 0.659, alpha = 1.0).setFill()
                                val r = cellW * 0.45
                                UIBezierPath.bezierPathWithOvalInRect(CGRectMake(left + w / 2 - r, top + h / 2 - r, 2 * r, 2 * r)).fill()
                                UIColor(red = 0.969, green = 0.812, blue = 0.882, alpha = 1.0).setFill()
                                UIBezierPath.bezierPathWithOvalInRect(CGRectMake(left + w / 2 - r * 0.4, top + h / 2 - r * 0.4, 0.8 * r, 0.8 * r)).fill()
                            } else {
                                UIColor(red = 0.702, green = 0.824, blue = 0.863, alpha = 1.0).setFill()
                                UIBezierPath.bezierPathWithRoundedRect(CGRectMake(left, top, w, h), cornerRadius = 6.0).fill()
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
