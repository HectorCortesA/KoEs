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

/** Widget mediano de Android: cuadrícula 12 × 5 sin animaciones. */
class KoesCalendarWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = context.getSharedPreferences("koes_calendar_widget", Context.MODE_PRIVATE)
            .getString("daily_word_counts", null)
        val counts = data?.split(",")?.map { it.toIntOrNull() ?: 0 } ?: emptyList()
        val bitmap = drawCalendarBitmap(counts)
        provideContent {
            CalendarWidgetImage(bitmap)
        }
    }
}

class KoesCalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = KoesCalendarWidget()
}

@Composable
private fun CalendarWidgetImage(bitmap: Bitmap) {
    Image(
        provider = ImageProvider(bitmap),
        contentDescription = "KOES, calendario de actividad de 60 días",
        contentScale = ContentScale.FillBounds,
        modifier = GlanceModifier.fillMaxSize()
    )
}

private fun drawCalendarBitmap(counts: List<Int>): Bitmap {
    // Tamaño 2:1 adecuado para el widget mediano. Se escala por el launcher.
    val w = 600f
    val h = 300f
    val bitmap = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.TRANSPARENT)

    val padding = 20f
    val gap = 10f
    val cellW = (w - 2 * padding - 11 * gap) / 12f
    val cellH = cellW * 27f / 23f
    val actualHeight = 2 * padding + 5 * cellH + 4 * gap
    val offsetY = (h - actualHeight) / 2f

    val back = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    canvas.drawRoundRect(RectF(0f, 0f, w, h), 20f, 20f, back)
    val empty = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(0xB3, 0xD2, 0xDC) }
    val petal = Paint(Paint.ANTI_ALIAS_FLAG)
    val center = Paint(Paint.ANTI_ALIAS_FLAG)
    val angles = doubleArrayOf(-90.0, -18.0, 54.0, 126.0, 198.0)

    for (col in 0 until 12) {
        for (row in 0 until 5) {
            val index = col * 5 + row
            val count = counts.getOrElse(index) { 0 }
            val left = padding + col * (cellW + gap)
            val top = offsetY + padding + row * (cellH + gap)
            if (count <= 0) {
                canvas.drawRoundRect(RectF(left, top, left + cellW, top + cellH), 3f, 3f, empty)
            } else {
                val colors = when {
                    count >= 10 -> intArrayOf(0xFF7A287A.toInt(), 0xFF6B1863.toInt(), 0xFFF7CFE1.toInt())
                    count >= 5 -> intArrayOf(0xFFC058A8.toInt(), 0xFFA84893.toInt(), 0xFFF7CFE1.toInt())
                    else -> intArrayOf(0xFFF7CFE1.toInt(), 0xFFE882AD.toInt(), 0xFFC75E5B.toInt())
                }
                val flowerSize = min(cellW, cellH) * 0.85f
                val cx = left + cellW / 2f
                val cy = top + cellH / 2f
                val petalRadius = flowerSize * 0.28f
                val distance = flowerSize * 0.26f
                for (i in angles.indices) {
                    val radians = angles[i] * PI / 180.0
                    petal.color = colors[if (i % 2 == 0) 0 else 1]
                    canvas.drawCircle(
                        cx + (distance * cos(radians)).toFloat(),
                        cy + (distance * sin(radians)).toFloat(),
                        petalRadius,
                        petal
                    )
                }
                center.color = colors[2]
                canvas.drawCircle(cx, cy, flowerSize * 0.16f, center)
            }
        }
    }
    return bitmap
}
