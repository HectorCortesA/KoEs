package com.hector.koes.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hector.koes.resources.Res
import com.hector.koes.ui.theme.Background

@Composable
fun OnBoarding(
    onNext: (String) -> Unit = {}
) {
    var query by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // 1. Imagen de fondo colocada en la parte derecha de abajo (atrás del texto y botones)
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = Res.getUri("files/peoplenormal.svg"),
                contentDescription = "Mascota",
                modifier = Modifier
                    .fillMaxWidth(0.95f)
            )
        }

        // 2. Contenido principal: Título arriba, y campos centrados en el medio
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Bienvenido",
                color = Color.Black,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(0.25f))

            // Contenedor centrado en medio junto con el botón Siguiente
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = {
                        query = it
                    },
                    singleLine = true,
                    cursorBrush = SolidColor(Color.Black),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .width(268.dp)
                        .height(39.dp)
                        .background(
                            color = Color(0x33D9D9D9),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.5f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 16.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "Escribe tu nombre",
                                    color = Color.Black.copy(alpha = 0.4f),
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .width(111.dp)
                        .height(39.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33D9D9D9))
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.5f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            onNext(query)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Siguiente",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.65f))
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun OnBoardingPreview() {
    OnBoarding(
        onNext = { name ->
            println("Nombre: $name")
        }
    )
}
