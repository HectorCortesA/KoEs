package com.hector.koes.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModalDictionary(
    modifier: Modifier = Modifier,
    wordSpanish: String,
    wordCorea: String,
    romanization: String,
    pronunciation: String,
    ejemploSpanish: String,
    ejemploKoreano: String,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(519.dp)
            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
    ) {
        // Capa de fondo con Glass
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0x80FFFFFF))
                .blur(15.dp)
        )

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Botón de guardado favorito en la parte derecha de la columna
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, end = 25.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFavorite) Color.Red.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f)
                        )
                        .clickable { onToggleFavorite() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFavorite) "❤️" else "🤍",
                        fontSize = 20.sp
                    )
                }
            }

            Text(
                text = wordSpanish,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(start = 25.dp)) {
                        Text(
                            text = "Coreano",
                            fontSize = 16.sp
                        )
                        Text(
                            text = wordCorea,
                            modifier = Modifier.padding(top = 5.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(start = 25.dp)) {
                        Text(
                            text = "Romanización",
                            fontSize = 16.sp
                        )
                        Text(
                            text = romanization,
                            modifier = Modifier.padding(top = 5.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Text(
                text = "Pronunciación",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = pronunciation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = ejemploSpanish,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = ejemploKoreano,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModalDictionaryPreview() {
    ModalDictionary(
        wordSpanish = "Hola",
        wordCorea = "안녕하세요",
        romanization = "annyeonghaseyo",
        pronunciation = "an-nyeong-ha-se-yo",
        ejemploSpanish = "Hola, ¿cómo estás?",
        ejemploKoreano = "안녕하세요, 어떻게 지내세요?"
    )
}
