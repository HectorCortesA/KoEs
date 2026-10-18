package com.hector.koes.components.plantilla

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil3.compose.AsyncImage

import com.hector.koes.components.Calendar.HeatCalendar
import com.hector.koes.model.Profile
import com.hector.koes.resources.Res
import com.hector.koes.viewModel.UserProgressManager

import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun ShareTemplateView(
    profile: Profile,
    modifier: Modifier = Modifier
) {
    val dailyWordCounts by UserProgressManager
        .dailyWordCounts
        .collectAsState()

    ShareTemplateContent(
        userName = profile.nameProfile,
        wordCounts = dailyWordCounts,
        modifier = modifier
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ShareTemplateContent(
    userName: String,
    wordCounts: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .background(Color(0xFFB7CAD7))
    ) {
        val width = maxWidth
        val height = maxHeight
        val scale = width.value / 360f

        // IMAGEN SVG
        AsyncImage(
            model = Res.getUri("files/peoplenormal.svg"),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(width * 0.64f)
                .height(height * 0.42f),
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd
        )

        // NOMBRE Y LOGOTIPO
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = width * 0.047f,
                    top = height * 0.051f
                )
        ) {
            Text(
                text = "KoEs",
                color = Color.Black,
                fontSize = (22f * scale).sp,
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

        // CALENDARIO ORIGINAL DE KOES
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = width * 0.052f,
                    bottom = height * 0.030f
                )
                .width(width * 0.555f)
                .clip(
                    RoundedCornerShape(
                        (5f * scale).dp
                    )
                )
                .background(Color(0xFFF1F5F8))
                .padding(
                    horizontal = width * 0.024f,
                    vertical = height * 0.014f
                )
        ) {
            HeatCalendar(
                modifier = Modifier.fillMaxWidth(),
                wordCounts = wordCounts
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun ShareTemplateViewPreview() {
    ShareTemplateContent(
        userName = "Hector Cortes",
        wordCounts = mapOf(
            0 to 1,
            13 to 1
        )
    )
}