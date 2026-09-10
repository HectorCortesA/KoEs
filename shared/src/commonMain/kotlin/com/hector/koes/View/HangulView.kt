package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hector.koes.components.card.CardHangul
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.model.Hangul
import com.hector.koes.ui.theme.Background

private val consonants = listOf(
    Hangul("ㄱ", "giyeok", "g/k"),
    Hangul("ㄴ", "nieun", "n"),
    Hangul("ㄷ", "digeut", "d/t"),
    Hangul("ㄹ", "rieul", "r/l"),
    Hangul("ㅁ", "mieum", "m"),
    Hangul("ㅂ", "bieup", "b/p"),
    Hangul("ㅅ", "siot", "s"),
    Hangul("ㅇ", "ieung", "ng"),
    Hangul("ㅈ", "jieut", "j"),
    Hangul("ㅊ", "chieut", "ch"),
    Hangul("ㅋ", "kieuk", "k"),
    Hangul("ㅌ", "tieut", "t")
)

@Composable
fun HangulView(
    onNavigate: (String) -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        Navbar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 60.dp),
            onNavigate = onNavigate
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(BiasAlignment(0f, -0.15f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Hangul",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            consonants.chunked(3).forEach { rowItems ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    rowItems.forEach { item ->
                        CardHangul(
                            hangul = item.hangul,
                            romanization = item.romanization,
                            pronunciation = item.pronunciation
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HangulViewPreview() {
    HangulView()
}