package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.hector.koes.components.modal.SharedModal
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
    val isPreview = LocalInspectionMode.current
    var showShareModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // Navbar
        Navbar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 10.dp),
            onNavigate = onNavigate
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    top = 100.dp,
                    start = 21.dp,
                    end = 21.dp
                )
                .verticalScroll(scrollState)
        ) {

            // Tarjetas superiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                // Tarjeta Racha
                Box(
                    modifier = Modifier
                        .width(145.dp)
                        .height(179.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {

                    Text(
                        text = "Racha",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Text(
                        text = "2",
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

                    // SVG solamente al ejecutar la app
                    if (!isPreview) {
                        AsyncImage(
                            model = Res.getUri(
                                "files/mask_fire.svg"
                            ),
                            contentDescription = "Racha",
                            modifier = Modifier
                                .width(115.dp)
                                .height(140.dp)
                                .align(Alignment.BottomStart),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(40.dp)
                )

                // Tarjeta Palabras aprendidas
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
                            text = "10",
                            color = Color.Black,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Palabras aprendidas",
                            modifier = Modifier
                                .padding(top = 10.dp),
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Calendario
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {

                HeatCalendar(
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            // Botón glass para abrir cámara
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .width(111.dp)
                        .height(35.dp)
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = Color.White.copy(
                                alpha = 0.18f
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(
                                        alpha = 0.70f
                                    ),
                                    Color.White.copy(
                                        alpha = 0.15f
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(
                                12.dp
                            )
                        )
                        .clickable {
                            showShareModal = !showShareModal
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Compartir",
                        color = Color(0xFF333333),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }

        // Modal de compartir posicionado abajo de la pantalla
        if (showShareModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable { showShareModal = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                ) {
                    SharedModal(
                        onCameraClick = {
                            showShareModal = false
                            onNavigate("cameraView")
                        }
                    )
                }
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