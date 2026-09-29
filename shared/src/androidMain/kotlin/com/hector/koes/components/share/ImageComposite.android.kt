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
actual fun rememberImageCompositor(): (photoBytes: ByteArray?, overlayItems: List<OverlayItem>, previewWidth: Float, previewHeight: Float) -> ByteArray? {
    return remember {
        val compositor: (ByteArray?, List<OverlayItem>, Float, Float) -> ByteArray? = { photoBytes, overlayItems, _, _ ->
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

                // Render items in a column at bottom-right corner
                var startY = exportHeight - 100f - (overlayItems.size * 70f)
                val marginX = 60f

                overlayItems.forEach { item ->
                    canvas.save()
                    if (item.isCalendar) {
                        val calWidth = 320f
                        val calHeight = 220f
                        val calX = exportWidth - calWidth - marginX
                        val calY = startY.coerceAtLeast(100f)
                        val bgPaint = Paint().apply {
                            color = Color.WHITE
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }
                        val rect = RectF(calX, calY, calX + calWidth, calY + calHeight)
                        canvas.drawRoundRect(rect, 24f, 24f, bgPaint)

                        val calTextPaint = Paint().apply {
                            color = Color.BLACK
                            textSize = 28f
                            isFakeBoldText = true
                            isAntiAlias = true
                        }
                        canvas.drawText("📅 Calendario", calX + 24f, calY + 50f, calTextPaint)
                        startY += calHeight + 20f
                    } else {
                        val textPaint = Paint().apply {
                            isAntiAlias = true
                            color = Color.WHITE
                            textSize = 42f
                            isFakeBoldText = true
                            setShadowLayer(6f, 2f, 2f, Color.BLACK)
                        }

                        val textWidth = textPaint.measureText(item.text)
                        val drawX = exportWidth - textWidth - marginX
                        val drawY = startY.coerceAtLeast(100f)

                        canvas.drawText(
                            item.text,
                            drawX,
                            drawY - textPaint.fontMetrics.ascent,
                            textPaint
                        )
                        startY += textPaint.textSize + 24f
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
