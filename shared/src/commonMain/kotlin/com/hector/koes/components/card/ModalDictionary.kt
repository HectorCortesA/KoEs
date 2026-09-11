package com.hector.koes.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.String

@Composable
fun ModalDictionary(
    modifier: Modifier = Modifier,
    wordSpanish: String,
    wordCorea: String,
    romanization: String,
    pronunciation: String,
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(719.dp) // Volvemos a altura fija para control total
            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
    ) {
        // Capa de fondo con Glass
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0x80FFFFFF))
                .blur(15.dp) // Aumentamos el blur para que se note
        )

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding() // Mantiene el contenido sobre la barra de navegación pero el fondo baja
        ) {
            Text(
                text = wordSpanish,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 53.dp),
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
                            fontSize = 16.sp,
                        )
                        Text(
                            text = wordCorea,
                            modifier = Modifier.padding(top = 5.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(start = 25.dp)) {
                        Text(
                            text = "Romanización",
                            fontSize = 16.sp,
                        )
                        Text(
                            text = romanization,
                            modifier = Modifier.padding(top = 5.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
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
        }
    }
}

@Preview(showBackground = true)

@Composable
fun ModalDictionaryPreview(){
    ModalDictionary(
        wordSpanish = "Hola",
        wordCorea = "안녕하세요",
        romanization = "annyeonghaseyo",
        pronunciation = "an-nyeong-ha-se-yo"
    )

}

