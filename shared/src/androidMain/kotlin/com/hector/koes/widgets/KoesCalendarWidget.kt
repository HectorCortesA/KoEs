
package com.hector.koes.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

import androidx.compose.runtime.Composable

import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ========================================
// WIDGET DEL CALENDARIO KOES
// ========================================

class KoesCalendarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {

        val data = context
            .getSharedPreferences(
                "koes_calendar_widget",
                Context.MODE_PRIVATE
            )
            .getString("daily_word_counts", null)

        val counts = data
            ?.split(",")
            ?.map { it.toIntOrNull() ?: 0 }
            ?: emptyList()

        val bitmap = drawCalendarBitmap(counts)

        provideContent {
            CalendarWidgetImage(bitmap)
        }
    }
}

// ========================================
// RECEIVER
// ========================================

class KoesCalendarWidgetReceiver :
    GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget =
        KoesCalendarWidget()
}

// ========================================
// IMAGEN DEL WIDGET
// ========================================

@Composable
private fun CalendarWidgetImage(
    bitmap: Bitmap
) {

    Image(
        provider = ImageProvider(bitmap),
        contentDescription = "KOES, calendario de actividad",
        contentScale = ContentScale.Fit,
        modifier = GlanceModifier.fillMaxSize()
    )
}

// ========================================
// DIBUJAR CALENDARIO
// ========================================

private fun drawCalendarBitmap(
    counts: List<Int>
): Bitmap {

    val width = 600f
    val height = 300f

    val bitmap = Bitmap.createBitmap(
        width.toInt(),
        height.toInt(),
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    val backgroundPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        color = Color.WHITE
    }

    canvas.drawRoundRect(
        RectF(0f, 0f, width, height),
        20f,
        20f,
        backgroundPaint
    )

    // ========================================
    // DIMENSIONES DE LA CUADRÍCULA
    // ========================================

    val columns = 12
    val rows = 5

    val padding = 20f
    val gap = 10f

    val cellWidth = (
            width - (padding * 2f) -
                    ((columns - 1) * gap)
            ) / columns

    val cellHeight = cellWidth * 27f / 23f

    val calendarHeight =
        (rows * cellHeight) +
                ((rows - 1) * gap)

    val offsetY = (
            height - calendarHeight
            ) / 2f

    // ========================================
    // PINCELES
    // ========================================

    val emptyPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        color = Color.rgb(
            0xB3,
            0xD2,
            0xDC
        )
    }

    val petalPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    )

    val centerPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    )

    // Posiciones de los cinco pétalos
    val angles = doubleArrayOf(
        -90.0,
        -18.0,
        54.0,
        126.0,
        198.0
    )

    // ========================================
    // DIBUJAR LAS 60 CELDAS
    // ========================================

    for (col in 0 until columns) {

        for (row in 0 until rows) {

            val index = col * rows + row

            val count = counts.getOrElse(index) {
                0
            }

            val left =
                padding + col * (cellWidth + gap)

            val top =
                offsetY + row * (cellHeight + gap)

            if (count <= 0) {

                canvas.drawRoundRect(
                    RectF(
                        left,
                        top,
                        left + cellWidth,
                        top + cellHeight
                    ),
                    3f,
                    3f,
                    emptyPaint
                )

            } else {

                // =================================
                // COLORES SEGÚN PALABRAS APRENDIDAS
                // =================================

                val colors = when {

                    count >= 10 -> intArrayOf(
                        0xFF7A287A.toInt(),
                        0xFF6B1863.toInt(),
                        0xFFF7CFE1.toInt()
                    )

                    count >= 5 -> intArrayOf(
                        0xFFC058A8.toInt(),
                        0xFFA84893.toInt(),
                        0xFFF7CFE1.toInt()
                    )

                    else -> intArrayOf(
                        0xFFF7CFE1.toInt(),
                        0xFFE882AD.toInt(),
                        0xFFC75E5B.toInt()
                    )
                }

                // =================================
                // FLOR PROPORCIONAL A SU CELDA
                // =================================

                val flowerSize = min(
                    cellWidth,
                    cellHeight
                ) * 0.85f

                val centerX =
                    left + cellWidth / 2f

                val centerY =
                    top + cellHeight / 2f

                val petalRadius =
                    flowerSize * 0.28f

                val petalDistance =
                    flowerSize * 0.26f

                // =================================
                // DIBUJAR CINCO PÉTALOS
                // =================================

                for (i in angles.indices) {

                    val radians =
                        angles[i] * PI / 180.0

                    petalPaint.color =
                        colors[if (i % 2 == 0) 0 else 1]

                    val petalX =
                        centerX +
                                (
                                        petalDistance *
                                                cos(radians)
                                        ).toFloat()

                    val petalY =
                        centerY +
                                (
                                        petalDistance *
                                                sin(radians)
                                        ).toFloat()

                    canvas.drawCircle(
                        petalX,
                        petalY,
                        petalRadius,
                        petalPaint
                    )
                }

                // =================================
                // CENTRO DE LA FLOR
                // =================================

                centerPaint.color = colors[2]

                canvas.drawCircle(
                    centerX,
                    centerY,
                    flowerSize * 0.16f,
                    centerPaint
                )
            }
        }
    }

    return bitmap
}
