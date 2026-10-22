
package com.hector.koes.components.plantilla

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil3.compose.AsyncImage

import com.hector.koes.components.Calendar.HeatCalendar
import com.hector.koes.model.Profile
import com.hector.koes.resources.Res
import com.hector.koes.ui.theme.*
import com.hector.koes.util.PreferencesStorage
import com.hector.koes.viewModel.UserProgressManager

import org.jetbrains.compose.resources.ExperimentalResourceApi

// ========================================
// PLANTILLA PRINCIPAL
// ========================================

@Composable
fun ShareTemplateView(
    profile: Profile? = null,
    modifier: Modifier = Modifier,
    learnedWords: Int? = null,
    streakDays: Int? = null
) {
    val currentStreak by UserProgressManager.currentStreak.collectAsState()
    val wordsLearnedCount by UserProgressManager.wordsLearnedCount.collectAsState()
    val dailyWordCounts by UserProgressManager.dailyWordCounts.collectAsState()

    val userName = profile?.nameProfile?.ifEmpty { null }
        ?: PreferencesStorage.getUserName().ifEmpty { "Hector Cortes" }

    ShareTemplateContent(
        userName = userName,
        learnedWords = learnedWords ?: wordsLearnedCount,
        streakDays = streakDays ?: currentStreak,
        wordCounts = dailyWordCounts,
        modifier = modifier
    )
}

// ========================================
// DISEÑO DE LA PLANTILLA 9:16
// ========================================

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ShareTemplateContent(
    userName: String,
    learnedWords: Int,
    streakDays: Int,
    wordCounts: Map<Int, Int>,
    modifier: Modifier = Modifier
) {

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .background(ShareTemplateBackgroundColor)
            .clipToBounds()
    ) {

        val width = maxWidth
        val height = maxHeight

        val scale = width.value / 360f

        // =====================================
        // 1. ÁRBOL DE CEREZO
        // SVG: 846 x 1316
        // =====================================

        AsyncImage(
            model = Res.getUri(
                "files/arbolcerezo.svg"
            ),
            contentDescription = "Árbol de cerezo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(width * 0.69f)
                .aspectRatio(846f / 1316f),
            contentScale = ContentScale.Fit,
            alignment = Alignment.TopEnd
        )

        // =====================================
        // 2. LOGO KOES Y NOMBRE
        // =====================================

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = width * 0.047f,
                    top = height * 0.055f
                )
        ) {

            Text(
                text = "KoEs",
                color = Color.Black,
                fontSize = (23f * scale).sp,
                lineHeight = (26f * scale).sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = userName,
                color = Color.Black,
                fontSize = (22f * scale).sp,
                lineHeight = (26f * scale).sp,
                fontWeight = FontWeight.Normal
            )
        }

        // =====================================
        // 3. PALABRAS APRENDIDAS
        // =====================================

        Text(
            text = "$learnedWords palabras aprendidas",
            color = Color.Black,
            fontSize = (14f * scale).sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = width * 0.047f,
                    y = height * 0.195f
                )
        )

        // =====================================
        // 4. RACHA DE DÍAS
        // =====================================

        Text(
            text = "Racha de $streakDays días",
            color = Color.Black,
            fontSize = (14f * scale).sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = width * 0.047f,
                    y = height * 0.233f
                )
        )

        // =====================================
        // 5. CALENDARIO DE ACTIVIDAD
        // POSICIÓN INFERIOR DERECHA
        // =====================================


        // =====================================
        // 5. CALENDARIO DE ACTIVIDAD
        // TAMAÑO AUMENTADO
        // =====================================

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = width * 0.30f,
                    y = height * 0.620f
                )
                .width(width * 0.65f)
                .height(height * 0.205f)
                .clip(
                    RoundedCornerShape(
                        (5f * scale).dp
                    )
                )

                .padding(
                    horizontal = width * 0.012f,
                    vertical = height * 0.008f
                ),
            contentAlignment = Alignment.Center
        ) {
            HeatCalendar(
                modifier = Modifier.fillMaxWidth(),
                wordCounts = wordCounts
            )
        }




        // =====================================
        // 7. PERSONAJE KOES
        // SVG: 402 x 602
        // TAMAÑO REDUCIDO
        // =====================================

        // Se dibuja por delante del calendario
        // y las flores para mantener la
        // composición original.

        AsyncImage(
            model = Res.getUri(
                "files/peoplenormal.svg"
            ),
            contentDescription = "Personaje de KOES",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(
                    x = -width * 0.035f,
                    y = height * 0.140f
                )
                .width(width * 0.72f)
                .aspectRatio(402f / 602f)
                .graphicsLayer {
                    scaleX = -1f
                },
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomStart
        )
    }
}

// ========================================
// PREVIEW
// ========================================

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun ShareTemplateViewPreview() {

    ShareTemplateContent(
        userName = "Hector Cortes",
        learnedWords = 24,
        streakDays = 24,
        wordCounts = mapOf(
            0 to 1,
            13 to 1,
            25 to 5,
            40 to 10
        )
    )
}
