package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.ui.theme.Background

@Composable
fun Home() {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    val suggestion = "티몬체"
    val text = textFieldValue.text

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Navbar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )

        // Contenedor Palabras - Posicionado un poco más arriba del centro
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(BiasAlignment(0f, -0.4f)), // Bias negativo para subirlo
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hola",
                color = Color.Black,
                style = TextStyle(fontSize = 18.sp)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                val visualText = buildAnnotatedString {
                    for (i in text.indices) {
                        val isCorrect = i < suggestion.length && text[i] == suggestion[i]
                        withStyle(style = SpanStyle(color = if (isCorrect) Color.Black else Color.Red)) {
                            append(text[i])
                        }
                    }
                    if (text.length < suggestion.length) {
                        withStyle(style = SpanStyle(color = Color.Black.copy(alpha = 0.1f))) {
                            append(suggestion.substring(text.length))
                        }
                    }
                }

                BasicTextField(
                    value = textFieldValue,
                    onValueChange = {
                        if (it.text.length <= suggestion.length) {
                            textFieldValue = it
                        }
                    },
                    textStyle = TextStyle(
                        color = Color.Transparent,
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(Color.Black),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = visualText,
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun HomePreview() {
    Home()
}
