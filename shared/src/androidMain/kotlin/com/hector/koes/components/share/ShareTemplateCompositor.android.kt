package com.hector.koes.components.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import coil3.toBitmap
import com.hector.koes.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun rememberShareTemplateCompositor(): suspend (
    userName: String,
    learnedWords: Int,
    streakDays: Int,
    wordCounts: Map<Int, Int>
) -> ByteArray? {
    val context = LocalContext.current
    return remember {
        suspend { userName, learnedWords, streakDays, dailyWordCounts ->
            try {
                val exportWidth = 1080f
                val exportHeight = 1920f
                val scale = exportWidth / 360f

                val bitmap = Bitmap.createBitmap(exportWidth.toInt(), exportHeight.toInt(), Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                // 1. Background #B7CAD7
                canvas.drawColor(Color.parseColor("#B7CAD7"))

                // ImageLoader configured with SvgDecoder.Factory()
                val imageLoader = ImageLoader.Builder(context)
                    .components {
                        add(SvgDecoder.Factory())
                    }
                    .build()

                // 2. Load and draw arbolcerezo.svg (Top Right: 846 x 1316)
                try {
                    val treeBytes = Res.readBytes("files/arbolcerezo.svg")
                    val requestTree = ImageRequest.Builder(context)
                        .data(treeBytes)
                        .build()
                    val resultTree = imageLoader.execute(requestTree)
                    val treeBitmap = resultTree.image?.toBitmap()
                    if (treeBitmap != null) {
                        val treeW = exportWidth * 0.69f
                        val treeH = treeW * (1316f / 846f)
                        val treeX = exportWidth - treeW
                        canvas.drawBitmap(treeBitmap, null, RectF(treeX, 0f, exportWidth, treeH), null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // 3. Logo KoEs y Nombre
                val cleanUserName = userName.substringBefore("android.graphics")
                val startX = exportWidth * 0.047f
                val startY = exportHeight * 0.055f

                val paintKoes = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.BLACK
                    textSize = 23f * scale
                    isFakeBoldText = true
                }
                val paintName = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.BLACK
                    textSize = 22f * scale
                }
                val paintBody = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.BLACK
                    textSize = 14f * scale
                }

                canvas.drawText("KoEs", startX, startY + 55f, paintKoes)
                canvas.drawText(cleanUserName, startX, startY + 125f, paintName)

                // 4. Palabras aprendidas & Racha de días
                canvas.drawText("$learnedWords palabras aprendidas", startX, exportHeight * 0.195f + 35f, paintBody)
                canvas.drawText("Racha de $streakDays días", startX, exportHeight * 0.233f + 35f, paintBody)

                // 5. Mini HeatCalendar
                val calLeft = exportWidth * 0.30f
                val calTop = exportHeight * 0.620f
                val calWidth = exportWidth * 0.65f

                drawMiniHeatCalendar(
                    canvas = canvas,
                    left = calLeft,
                    top = calTop,
                    width = calWidth,
                    previewToExport = 1f,
                    density = 3f,
                    wordCounts = dailyWordCounts
                )

                // 6. Personaje KOES (peoplenormal.svg: 402 x 602, flipped horizontally)
                try {
                    val personBytes = Res.readBytes("files/peoplenormal.svg")
                    val requestPerson = ImageRequest.Builder(context)
                        .data(personBytes)
                        .build()
                    val resultPerson = imageLoader.execute(requestPerson)
                    val personBitmap = resultPerson.image?.toBitmap()
                    if (personBitmap != null) {
                        val personW = exportWidth * 0.72f
                        val personH = personW * (602f / 402f)
                        val personLeft = -exportWidth * 0.035f
                        val personTop = (exportHeight - personH) + exportHeight * 0.140f

                        canvas.save()
                        canvas.scale(-1f, 1f, personLeft + personW / 2f, personTop + personH / 2f)
                        canvas.drawBitmap(personBitmap, null, RectF(personLeft, personTop, personLeft + personW, personTop + personH), null)
                        canvas.restore()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
