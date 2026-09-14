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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

private val vowelsVertical = listOf(
    Hangul("ㅏ", "a", "a"),
    Hangul("ㅑ", "ya", "ya"),
    Hangul("ㅓ", "eo", "eo"),
    Hangul("ㅕ", "yeo", "yeo"),
    Hangul("ㅣ", "i", "i")
)

private val vowelsHorizontal = listOf(
    Hangul("ㅗ", "o", "o"),
    Hangul("ㅛ", "yo", "yo"),
    Hangul("ㅜ", "u", "u"),
    Hangul("ㅠ", "yu", "yu"),
    Hangul("ㅡ", "eu", "eu")
)

private val vowelsVerticalDouble = listOf(
    Hangul("ㅐ", "ae", "ae"),
    Hangul("ㅒ", "yae", "yae"),
    Hangul("ㅔ", "e", "e"),
    Hangul("ㅖ", "ye", "ye")
)

private val vowelsDoubleMixed = listOf(
    Hangul("ㅘ", "wa", "wa"),
    Hangul("ㅙ", "wae", "wae"),
    Hangul("ㅚ", "oe", "oe"),
    Hangul("ㅝ", "wo", "wo"),
    Hangul("ㅞ", "we", "we"),
    Hangul("ㅟ", "wi", "wi"),
    Hangul("ㅢ", "ui", "ui")
)


@Composable
fun HangulView(
    onNavigate: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        Navbar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 10.dp),
            onNavigate = onNavigate
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 80.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Hangul".uppercase(),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            consonants.chunked(3).forEach { rowItems ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Vocales verticales".uppercase(),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Aquí después agregas tus cards de vocales
            vowelsVertical.chunked(3).forEach { rowItems ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
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
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Vocales horizontales".uppercase(),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            vowelsHorizontal.chunked(3).forEach { rowItems ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Vocales verticales dobles".uppercase(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            vowelsVerticalDouble.chunked(3).forEach { rowItems ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Vocales mixtas dobles".uppercase(),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            vowelsDoubleMixed.chunked(3).forEach { rowItems ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.height(40.dp))

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HangulViewPreview() {
    HangulView()
}