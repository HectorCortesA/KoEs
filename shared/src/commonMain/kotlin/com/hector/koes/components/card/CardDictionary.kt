package com.hector.koes.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardDictionary(
    wordSpanish: String,
    wordCorea: String,
    pronunciation: String,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .width(362.dp)
            .height(60.dp)
            .background(color = Color(0xFFDAE4EA), shape = RoundedCornerShape(size = 5.dp)),
        verticalAlignment = Alignment.CenterVertically
    ){
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = wordSpanish,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.width(22.dp))
        Text(
            text = wordCorea,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,

        )
        Spacer(modifier = Modifier.width(22.dp))
        Text(
            text = pronunciation,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.width(15.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CardDictionaryPreview(){
    CardDictionary(
        wordSpanish = "Hola",
        wordCorea = "안녕하세요",
        pronunciation = "annyeonghaseyo"
    )
}
