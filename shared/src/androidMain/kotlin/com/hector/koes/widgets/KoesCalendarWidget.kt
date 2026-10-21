
package com.hector.koes.widgets

import android.content.Context
import android.content.Intent
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
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ========================================
// WIDGET CALENDARIO KOES
// ========================================

class KoesCalendarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {

        // Obtener el progreso real del usuario
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

        // Crear el calendario
        val bitmap = drawCalendarBitmap(counts)

        // Intent para abrir MainActivity
        // Sin importar directamente la clase
        val launchIntent = Intent().apply {

            setClassName(
                context.packageName,
                "com.hector.koes.MainActivity"
            )

            action = Intent.ACTION_MAIN

            addCategory(
                Intent.CATEGORY_LAUNCHER
            )

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // Mostrar el widget
        provideContent {

            CalendarWidgetImage(
                bitmap = bitmap,
                launchIntent = launchIntent
            )
        }
    }
}

// ========================================
// RECEIVER DEL WIDGET
// ========================================

class KoesCalendarWidgetReceiver :
    GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget =
        KoesCalendarWidget()
}

// ========================================
// IMAGEN INTERACTIVA DEL WIDGET
// ========================================

@Composable
private fun CalendarWidgetImage(
    bitmap: Bitmap,
    launchIntent: Intent
) {

    Image(
        provider = ImageProvider(bitmap),

        contentDescription =
            "KOES, calendario de actividad de 60 días",

        contentScale = ContentScale.Fit,

        modifier = GlanceModifier
            .fillMaxSize()

            // Abrir KOES al tocar el widget
            .clickable(
                actionStartActivity(launchIntent)
            )
    )
}

// ========================================
// DIBUJAR CALENDARIO
// ========================================

private fun drawCalendarBitmap(
    counts: List<Int>
): Bitmap {

    // Dimensiones de la imagen
    val w = 600f
    val h = 300f

    val bitmap = Bitmap.createBitmap(
        w.toInt(),
        h.toInt(),
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    canvas.drawColor(Color.TRANSPARENT)

    // ========================================
    // DIMENSIONES DE LA CUADRÍCULA
    // ========================================

    val columns = 12
    val rows = 5

    val padding = 20f
    val gap = 10f

    val cellW = (
            w - 2f * padding -
                    (columns - 1) * gap
            ) / columns

    val cellH = cellW * 27f / 23f

    val actualHeight =
        2f * padding +
                rows * cellH +
                (rows - 1) * gap

    val offsetY = (h - actualHeight) / 2f

    // ========================================
    // FONDO BLANCO
    // ========================================

    val backgroundPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        color = Color.WHITE
    }

    canvas.drawRoundRect(
        RectF(
            0f,
            0f,
            w,
            h
        ),
        20f,
        20f,
        backgroundPaint
    )

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

    val angles = doubleArrayOf(
        -90.0,
        -18.0,
        54.0,
        126.0,
        198.0
    )

    // ========================================
    // GENERAR LAS 60 CELDAS
    // ========================================

    for (col in 0 until columns) {

        for (row in 0 until rows) {

            val index = col * rows + row

            val count = counts.getOrElse(index) {
                0
            }

            val left =
                padding +
                        col * (cellW + gap)

            val top =
                offsetY +
                        padding +
                        row * (cellH + gap)

            // =================================
            // CELDA SIN ACTIVIDAD
            // =================================

            if (count <= 0) {

                canvas.drawRoundRect(
                    RectF(
                        left,
                        top,
                        left + cellW,
                        top + cellH
                    ),
                    3f,
                    3f,
                    emptyPaint
                )

            } else {

                // =================================
                // COLORES SEGÚN ACTIVIDAD
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
                // PROPORCIONES DE LA FLOR
                // =================================

                val flowerSize = min(
                    cellW,
                    cellH
                ) * 0.85f

                val centerX =
                    left + cellW / 2f

                val centerY =
                    top + cellH / 2f

                val petalRadius =
                    flowerSize * 0.28f

                val distance =
                    flowerSize * 0.26f

                // =================================
                // DIBUJAR CINCO PÉTALOS
                // =================================

                for (i in angles.indices) {

                    val radians =
                        angles[i] * PI / 180.0

                    petalPaint.color =
                        colors[
                            if (i % 2 == 0) 0 else 1
                        ]

                    val petalX =
                        centerX +
                                (
                                        distance * cos(radians)
                                        ).toFloat()

                    val petalY =
                        centerY +
                                (
                                        distance * sin(radians)
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
