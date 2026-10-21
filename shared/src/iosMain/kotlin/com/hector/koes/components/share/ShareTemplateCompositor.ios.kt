@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.hector.koes.components.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.hector.koes.resources.Res
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIImage
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.drawAtPoint
import platform.posix.memcpy
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private fun iosColor(r: Int, g: Int, b: Int): UIColor = UIColor(
    red = r / 255.0, green = g / 255.0, blue = b / 255.0, alpha = 1.0
)

private fun NSData.asByteArray(): ByteArray {
    val count = length.toInt()
    if (count == 0) return byteArrayOf()
    val result = ByteArray(count)
    result.usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, length) }
    return result
}

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun rememberShareTemplateCompositor(): suspend (
    userName: String,
    learnedWords: Int,
    streakDays: Int,
    wordCounts: Map<Int, Int>
) -> ByteArray? = remember {
    suspend { userName, learnedWords, streakDays, dailyWordCounts ->
        val outW = 1080.0
        val outH = 1920.0
        val scale = outW / 360.0

        UIGraphicsBeginImageContextWithOptions(CGSizeMake(outW, outH), true, 1.0)
        try {
            // 1. Background #B7CAD7
            iosColor(0xB7, 0xCA, 0xD7).setFill()
            UIBezierPath.bezierPathWithRect(CGRectMake(0.0, 0.0, outW, outH)).fill()

            // ImageLoader with SvgDecoder
            val imageLoader = ImageLoader.Builder(PlatformContext.INSTANCE)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()

            // 2. Load and draw arbolcerezo.svg (Top Right: 846 x 1316)
            try {
                val treeBytes = Res.readBytes("files/arbolcerezo.svg")
                val requestTree = ImageRequest.Builder(PlatformContext.INSTANCE)
                    .data(treeBytes)
                    .build()
                val resultTree = imageLoader.execute(requestTree)
                val treeImage = resultTree.image
                if (treeImage != null) {
                    val treeW = outW * 0.69
                    val treeH = treeW * (1316.0 / 846.0)
                    val treeX = outW - treeW
                    val treeY = 0.0

                    val skiaBitmap = org.jetbrains.skia.Bitmap().apply {
                        allocPixels(org.jetbrains.skia.ImageInfo.makeN32Premul(treeImage.width, treeImage.height))
                    }
                    val canvas = org.jetbrains.skia.Canvas(skiaBitmap)
                    treeImage.draw(canvas)
                    val pngBytes = org.jetbrains.skia.Image.makeFromBitmap(skiaBitmap).encodeToData(org.jetbrains.skia.EncodedImageFormat.PNG)?.bytes
                    if (pngBytes != null) {
                        val treeUiImage = pngBytes.usePinned { pinned ->
                            val nsData = NSData.create(bytes = pinned.addressOf(0), length = pngBytes.size.toULong())
                            UIImage(data = nsData)
                        }
                        treeUiImage?.drawInRect(CGRectMake(treeX, treeY, treeW, treeH))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 3. Logo KoEs y Nombre
            val cleanUserName = userName.substringBefore("android.graphics")
            val startX = outW * 0.047
            val startY = outH * 0.055

            val fontKoes = UIFont.boldSystemFontOfSize(23.0 * scale)
            val fontName = UIFont.systemFontOfSize(22.0 * scale)
            val fontBody = UIFont.systemFontOfSize(14.0 * scale)

            val textAttrsKoes = mapOf<Any?, Any>(NSFontAttributeName to fontKoes, NSForegroundColorAttributeName to UIColor.blackColor)
            val textAttrsName = mapOf<Any?, Any>(NSFontAttributeName to fontName, NSForegroundColorAttributeName to UIColor.blackColor)
            val textAttrsBody = mapOf<Any?, Any>(NSFontAttributeName to fontBody, NSForegroundColorAttributeName to UIColor.blackColor)

            NSString.create(string = "KoEs").drawAtPoint(CGPointMake(startX, startY), withAttributes = textAttrsKoes)
            NSString.create(string = cleanUserName).drawAtPoint(CGPointMake(startX, startY + 72.0), withAttributes = textAttrsName)

            // 4. Palabras aprendidas & Racha de días
            NSString.create(string = "$learnedWords palabras aprendidas").drawAtPoint(CGPointMake(startX, outH * 0.195), withAttributes = textAttrsBody)
            NSString.create(string = "Racha de $streakDays días").drawAtPoint(CGPointMake(startX, outH * 0.233), withAttributes = textAttrsBody)

            // 5. Mini HeatCalendar
            val calX = outW * 0.30
            val calY = outH * 0.62
            val calWidth = outW * 0.65
            val unit = calWidth / 260.0
            val padding = 16.0 * unit
            val gap = 8.0 * unit
            val cellW = (calWidth - 2.0 * padding - 11.0 * gap) / 12.0
            val cellH = cellW * 27.0 / 23.0
            val calHeight = 2.0 * padding + 5.0 * cellH + 4.0 * gap

            UIColor.whiteColor.colorWithAlphaComponent(0.8).setFill()
            UIBezierPath.bezierPathWithRoundedRect(
                CGRectMake(calX, calY, calWidth, calHeight),
                cornerRadius = 10.0 * unit
            ).fill()

            for (row in 0 until 5) {
                for (col in 0 until 12) {
                    val index = col * 5 + row
                    val count = dailyWordCounts[index] ?: 0
                    val left = calX + padding + col * (cellW + gap)
                    val top = calY + padding + row * (cellH + gap)
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

            // 6. Personaje KOES (peoplenormal.svg: 402 x 602, flipped horizontally)
            try {
                val personBytes = Res.readBytes("files/peoplenormal.svg")
                val requestPerson = ImageRequest.Builder(PlatformContext.INSTANCE)
                    .data(personBytes)
                    .build()
                val resultPerson = imageLoader.execute(requestPerson)
                val personImage = resultPerson.image
                if (personImage != null) {
                    val personW = outW * 0.72
                    val personH = personW * (602.0 / 402.0)
                    val personLeft = -outW * 0.035
                    val personTop = (outH - personH) + outH * 0.140

                    val skiaBitmap = org.jetbrains.skia.Bitmap().apply {
                        allocPixels(org.jetbrains.skia.ImageInfo.makeN32Premul(personImage.width, personImage.height))
                    }
                    val canvas = org.jetbrains.skia.Canvas(skiaBitmap)
                    personImage.draw(canvas)
                    val pngBytes = org.jetbrains.skia.Image.makeFromBitmap(skiaBitmap).encodeToData(org.jetbrains.skia.EncodedImageFormat.PNG)?.bytes
                    if (pngBytes != null) {
                        val personUiImage = pngBytes.usePinned { pinned ->
                            val nsData = NSData.create(bytes = pinned.addressOf(0), length = pngBytes.size.toULong())
                            UIImage(data = nsData)
                        }
                        val context = platform.UIKit.UIGraphicsGetCurrentContext()
                        if (context != null && personUiImage != null) {
                            platform.CoreGraphics.CGContextSaveGState(context)
                            platform.CoreGraphics.CGContextTranslateCTM(context, personLeft + personW / 2.0, personTop + personH / 2.0)
                            platform.CoreGraphics.CGContextScaleCTM(context, -1.0, 1.0)
                            platform.CoreGraphics.CGContextTranslateCTM(context, -(personLeft + personW / 2.0), -(personTop + personH / 2.0))
                            personUiImage.drawInRect(CGRectMake(personLeft, personTop, personW, personH))
                            platform.CoreGraphics.CGContextRestoreGState(context)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            UIGraphicsGetImageFromCurrentImageContext()?.let { result ->
                UIImagePNGRepresentation(result)?.asByteArray()
            }
        } finally {
            UIGraphicsEndImageContext()
        }
    }
}
