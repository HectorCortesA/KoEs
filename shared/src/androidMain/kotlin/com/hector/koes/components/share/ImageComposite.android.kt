
package com.hector.koes.components.share

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
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
        val compositor: (ByteArray?, List<OverlayItem>, Float, Float, Map<Int, Int>) -> ByteArray? = { photoBytes, overlayItems, previewWidth, previewHeight, dailyWordCounts ->
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

                // Coordenadas normalizadas: (0,0) es la esquina superior izquierda.
                // previewWidth y previewHeight son las dimensiones reales, en px, del editor.
                val validPreviewWidth = previewWidth.takeIf { it > 0f } ?: 1080f
                val validPreviewHeight = previewHeight.takeIf { it > 0f } ?: 1920f
                val scaleX = exportWidth / validPreviewWidth
                val scaleY = exportHeight / validPreviewHeight
                val displayMetrics = android.content.res.Resources.getSystem().displayMetrics
                val exportTextSize = if (previewWidth > 0f) {
                    22f * displayMetrics.scaledDensity * scaleY
                } else 54f
                val calWidth = if (previewWidth > 0f) {
                    minOf(260f * displayMetrics.density, validPreviewWidth * 0.72f) * scaleX
                } else 500f


                canvas.save()
                canvas.clipRect(0f, 0f, exportWidth, exportHeight)
                overlayItems.forEach { item ->
                    canvas.save()
                    val x = item.normalizedX.coerceIn(0f, 1f) * exportWidth
                    val y = item.normalizedY.coerceIn(0f, 1f) * exportHeight
                    if (item.isCalendar) {
                        // Dibuja el Mini Calendario con las mismas proporciones y colores
                        // que HeatCalendar.kt, sin modificar ese componente de Compose.
                        drawMiniHeatCalendar(
                            canvas = canvas,
                            left = x,
                            top = y,
                            width = calWidth,
                            previewToExport = scaleX,
                            density = displayMetrics.density,
                            wordCounts = dailyWordCounts
                        )
                    } else {
                        val textPaint = Paint().apply {
                            isAntiAlias = true
                            color = Color.WHITE
                            textSize = exportTextSize
                            isFakeBoldText = true
                            setShadowLayer(8f, 2f, 2f, Color.BLACK)
                        }

                        val drawX = x
                        val drawY = y

                        canvas.drawText(
                            item.text,
                            drawX,
                            drawY - textPaint.fontMetrics.ascent,
                            textPaint
                        )
                    }
                    canvas.restore()
                }
                canvas.restore()

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


/** Versión estática para exportación del componente HeatCalendar.kt. */
internal fun drawMiniHeatCalendar(
    canvas: Canvas,
    left: Float,
    top: Float,
    width: Float,
    previewToExport: Float,
    density: Float,
    wordCounts: Map<Int, Int>
) {
    val cols = 12
    val rows = 5
    // Equivalentes a 16.dp de padding, 8.dp de separación y 10.dp de esquinas.
    val pxPerDp = density * previewToExport
    val padding = 16f * pxPerDp
    val gap = 8f * pxPerDp
    val cellWidth = (width - 2f * padding - (cols - 1) * gap) / cols
    if (cellWidth <= 0f) return
    val cellHeight = cellWidth * 27f / 23f
    val height = 2f * padding + rows * cellHeight + (rows - 1) * gap

    val background = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 204 // Color.White.copy(alpha = 0.8f)
    }
    canvas.drawRoundRect(
        RectF(left, top, left + width, top + height),
        10f * pxPerDp, 10f * pxPerDp, background
    )

    val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B3D2DC")
    }
    val petalPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val angles = doubleArrayOf(-90.0, -18.0, 54.0, 126.0, 198.0)

    for (row in 0 until rows) {
        for (col in 0 until cols) {
            val index = col * rows + row
            val count = wordCounts[index] ?: 0
            val cellLeft = left + padding + col * (cellWidth + gap)
            val cellTop = top + padding + row * (cellHeight + gap)
            val cellRect = RectF(cellLeft, cellTop, cellLeft + cellWidth, cellTop + cellHeight)
            if (count <= 0) {
                canvas.drawRoundRect(cellRect, 3f * pxPerDp, 3f * pxPerDp, emptyPaint)
            } else {
                val colors = when {
                    count >= 10 -> intArrayOf(
                        Color.parseColor("#7A287A"), Color.parseColor("#6B1863"),
                        Color.parseColor("#F7CFE1")
                    )
                    count >= 5 -> intArrayOf(
                        Color.parseColor("#C058A8"), Color.parseColor("#A84893"),
                        Color.parseColor("#F7CFE1")
                    )
                    else -> intArrayOf(
                        Color.parseColor("#F7CFE1"), Color.parseColor("#E882AD"),
                        Color.parseColor("#C75E5B")
                    )
                }
                // Flower ocupa el 85 % de la celda, igual que AnimatedFlower.
                val flowerSize = minOf(cellWidth, cellHeight) * 0.85f
                val cx = cellRect.centerX()
                val cy = cellRect.centerY()
                val petalRadius = flowerSize * 0.28f
                val petalDistance = flowerSize * 0.26f
                for (i in angles.indices) {
                    val angle = angles[i] * PI / 180.0
                    petalPaint.color = if (i % 2 == 0) colors[0] else colors[1]
                    canvas.drawCircle(
                        cx + (petalDistance * cos(angle)).toFloat(),
                        cy + (petalDistance * sin(angle)).toFloat(),
                        petalRadius, petalPaint
                    )
                }
                centerPaint.color = colors[2]
                canvas.drawCircle(cx, cy, flowerSize * 0.16f, centerPaint)
            }
        }
    }
}
