package com.hector.koes.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CardHangul(
    hangul: String,
    romanization: String,
    pronunciation: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .width(80.dp)
            .height(80.dp)
            .background(
                color = Color(0xFFA0B7C4),
                shape = RoundedCornerShape(10.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = hangul,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Texto responsivo: se ajusta en una sola línea y reduce su impacto visual
        // si el contenido es muy largo, usando un tamaño de fuente más pequeño de base.
        Text(
            text = "$romanization ($pronunciation)",
            fontSize = 12.sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(72.dp) // Un poco menos que el ancho del card
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardHangulPreview() {
    CardHangul(
        hangul = "ㄱ",
        romanization = "giyeok",
        pronunciation = "g/k"
    )
}