package com.hector.koes.components.share

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.exifinterface.media.ExifInterface
import com.hector.koes.View.OverlayItem
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

actual fun getImageDimensions(photoBytes: ByteArray?): Pair<Float, Float> {
    if (photoBytes == null || photoBytes.isEmpty()) return Pair(1080f, 1920f)
    return try {
        val inputStream = ByteArrayInputStream(photoBytes)
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size, options)
        var w = options.outWidth.toFloat()
        var h = options.outHeight.toFloat()

        if (w <= 0f || h <= 0f) {
            val bmp = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
            if (bmp != null) {
                w = bmp.width.toFloat()
                h = bmp.height.toFloat()
            }
        }
        if (w <= 0f || h <= 0f) return Pair(1080f, 1920f)

        val swap = orientation == ExifInterface.ORIENTATION_ROTATE_90 ||
                orientation == ExifInterface.ORIENTATION_ROTATE_270 ||
                orientation == ExifInterface.ORIENTATION_TRANSPOSE ||
                orientation == ExifInterface.ORIENTATION_TRANSVERSE

        if (swap) Pair(h, w) else Pair(w, h)
    } catch (e: Exception) {
        e.printStackTrace()
        Pair(1080f, 1920f)
    }
}

fun decodeAndRotateBitmap(photoBytes: ByteArray?): Bitmap {
    if (photoBytes == null || photoBytes.isEmpty()) {
        return Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.parseColor("#050505"))
        }
    }
    val bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
        ?: return Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.parseColor("#050505"))
        }

    return try {
        val inputStream = ByteArrayInputStream(photoBytes)
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> matrix.postRotate(270f)
        }
        if (orientation != ExifInterface.ORIENTATION_NORMAL && orientation != ExifInterface.ORIENTATION_UNDEFINED) {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
        bitmap
    }
}

@Composable
actual fun rememberImageCompositor(): (photoBytes: ByteArray?, overlayItems: List<OverlayItem>, previewWidth: Float, previewHeight: Float, dailyWordCounts: Map<Int, Int>) -> ByteArray? {
    return remember {
        val compositor: (ByteArray?, List<OverlayItem>, Float, Float, Map<Int, Int>) -> ByteArray? = { photoBytes, overlayItems, _, _, dailyWordCounts ->
            try {
                val baseBitmap = decodeAndRotateBitmap(photoBytes)
                val exportWidth = 1080f
                val exportHeight = 1920f

                val exportBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(exportBitmap)
                canvas.drawColor(Color.BLACK)

                val imgW = baseBitmap.width.toFloat()
                val imgH = baseBitmap.height.toFloat()

                val exportTransform = calculateImageTransform(exportWidth, exportHeight, imgW, imgH)

                val destRect = RectF(
                    exportTransform.offsetX,
                    exportTransform.offsetY,
                    exportTransform.offsetX + exportTransform.renderedWidth,
                    exportTransform.offsetY + exportTransform.renderedHeight
                )
                canvas.drawBitmap(baseBitmap, null, destRect, null)

                // Calculate total height needed for the items column to render from bottom-right upwards or downwards correctly
                var totalBlockHeight = 0f
                overlayItems.forEach { item ->
                    if (item.isCalendar) {
                        totalBlockHeight += 360f + 20f
                    } else {
                        totalBlockHeight += 70f + 16f
                    }
                }

                var currentY = (exportHeight - 140f - totalBlockHeight).coerceAtLeast(100f)
                val marginX = 60f

                overlayItems.forEach { item ->
                    canvas.save()
                    if (item.isCalendar) {
                        val calWidth = 500f
                        val calHeight = 360f
                        val calX = exportWidth - calWidth - marginX
                        val calY = currentY

                        val bgPaint = Paint().apply {
                            color = Color.WHITE
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }
                        canvas.drawRoundRect(RectF(calX, calY, calX + calWidth, calY + calHeight), 32f, 32f, bgPaint)

                        // Draw heatmap grid (12 cols x 5 rows) and flowers
                        val cols = 12
                        val rows = 5
                        val padding = 24f
                        val innerW = calWidth - (padding * 2f)
                        val innerH = calHeight - (padding * 2f)
                        val cellW = innerW / cols
                        val cellH = innerH / rows

                        val cellBgPaint = Paint().apply {
                            color = Color.parseColor("#B3D2DC")
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }
                        val petalPaint = Paint().apply {
                            color = Color.parseColor("#C058A8")
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }
                        val centerPaint = Paint().apply {
                            color = Color.parseColor("#F7CFE1")
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }

                        for (col in 0 until cols) {
                            for (row in 0 until rows) {
                                val index = col * rows + row
                                val count = dailyWordCounts[index] ?: 0
                                val cellLeft = calX + padding + (col * cellW) + 2f
                                val cellTop = calY + padding + (row * cellH) + 2f
                                val cellRight = calX + padding + ((col + 1) * cellW) - 2f
                                val cellBottom = calY + padding + ((row + 1) * cellH) - 2f

                                if (count > 0) {
                                    val cx = (cellLeft + cellRight) / 2f
                                    val cy = (cellTop + cellBottom) / 2f
                                    val radius = cellW * 0.45f
                                    canvas.drawCircle(cx, cy, radius, petalPaint)
                                    canvas.drawCircle(cx, cy, radius * 0.4f, centerPaint)
                                } else {
                                    canvas.drawRoundRect(RectF(cellLeft, cellTop, cellRight, cellBottom), 6f, 6f, cellBgPaint)
                                }
                            }
                        }

                        currentY += calHeight + 20f
                    } else {
                        val textPaint = Paint().apply {
                            isAntiAlias = true
                            color = Color.WHITE
                            textSize = 54f
                            isFakeBoldText = true
                            setShadowLayer(8f, 2f, 2f, Color.BLACK)
                        }

                        val textWidth = textPaint.measureText(item.text)
                        val drawX = exportWidth - textWidth - marginX
                        val drawY = currentY

                        canvas.drawText(
                            item.text,
                            drawX,
                            drawY - textPaint.fontMetrics.ascent,
                            textPaint
                        )
                        currentY += textPaint.textSize + 24f
                    }
                    canvas.restore()
                }

                val outputStream = ByteArrayOutputStream()
                exportBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        compositor
    }
}
