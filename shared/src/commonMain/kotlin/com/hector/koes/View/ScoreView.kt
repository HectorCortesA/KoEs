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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hector.koes.components.Calendar.HeatCalendar
import com.hector.koes.components.navbar.Navbar
import com.hector.koes.resources.Res
import com.hector.koes.ui.theme.Background
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ScoreView(
    onNavigate: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val wordlearnnumber: Int
    val numberracha: Int

    // Detecta si estás viendo el Canvas / Preview
    val isPreview = LocalInspectionMode.current

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

        // Row y calendario viven en el MISMO Column, que además hace el
        // scroll. Así el calendario siempre cae justo debajo del Row (con
        // el padding(top = 20.dp) de más abajo) sin importar la altura real
        // del Row ni el tamaño de pantalla — nada de offsets adivinados.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 100.dp, start = 21.dp, end = 21.dp)
                .verticalScroll(scrollState)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .width(145.dp)
                        .height(179.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {

                    // Título
                    Text(
                        text = "Racha",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal
                    )

                    // Número
                    Text(
                        text = "2", //numberracha.toString(),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(
                                top = 50.dp,
                                end = 35.dp
                            ),
                        color = Color.Black,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Fuego: alineado a la esquina inferior izquierda, sin
                    // offset, y el Box padre ahora tiene .clip(...) — así
                    // queda pegado a esa esquina pero nunca se sale del Box.
                    if (!isPreview) {
                        AsyncImage(
                            model = Res.getUri("files/mask_fire.svg"),
                            contentDescription = "Racha",
                            modifier = Modifier
                                .width(115.dp)
                                .height(140.dp)
                                .align(Alignment.BottomStart),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
                Spacer(modifier = Modifier.width(40.dp))
                Box(
                    modifier = Modifier
                        .width(145.dp)
                        .height(179.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "10", // wordlearnnumber.toString()
                            color = Color.Black,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Palabras aprendidas",
                            modifier = Modifier.padding(top = 10.dp),
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                HeatCalendar(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
fun ScorePreview() {
    ScoreView()
}