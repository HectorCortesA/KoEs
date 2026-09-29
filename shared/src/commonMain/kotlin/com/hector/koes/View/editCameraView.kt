package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hector.koes.components.Calendar.HeatCalendar
import com.hector.koes.components.camera.CameraPreview
import com.hector.koes.components.share.rememberImageCompositor
import com.hector.koes.components.share.rememberShareLauncher
import com.hector.koes.ui.theme.Background
import com.hector.koes.viewModel.UserProgressManager
import kotlin.random.Random

data class OverlayItem(
    val id: Long = Random.nextLong(),
    val text: String,
    var normalizedFontSize: Float = 0.045f,
    val isCalendar: Boolean = false
)

@Composable
fun EditCameraView(
    onNavigate: (String) -> Unit = {},
    photoBytes: ByteArray? = null
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val streak by UserProgressManager.currentStreak.collectAsState()
    val wordsLearned by UserProgressManager.wordsLearnedCount.collectAsState()
    val dailyWordCounts by UserProgressManager.dailyWordCounts.collectAsState()

    var overlayItems by remember {
        mutableStateOf(
            listOf(
                OverlayItem(text = "KoEs")
            )
        )
    }

    val shareLauncher = rememberShareLauncher()
    val compositor = rememberImageCompositor()
    val density = LocalDensity.current

    val availableBadges = listOf(
        "Racha: $streak días",
        "$wordsLearned Palabras aprendidas",
        "Mini Calendario",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Barra superior con selector desplegable y botón de cerrar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 21.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Selector con estilo glass
                Box {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(45.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.18f))
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.70f),
                                        Color.White.copy(alpha = 0.15f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { isDropdownExpanded = true }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Añadir elemento ▾",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        containerColor = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.width(220.dp)
                    ) {
                        availableBadges.forEach { badge ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = badge,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF222222)
                                    )
                                },
                                onClick = {
                                    val isCal = badge.contains("Calendario")
                                    overlayItems = overlayItems + OverlayItem(
                                        text = if (isCal) "Calendario" else badge,
                                        normalizedFontSize = if (isCal) 0.25f else 0.045f,
                                        isCalendar = isCal
                                    )
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Botón cancelar / regresar
                Box(
                    modifier = Modifier
                        .width(59.dp)
                        .height(55.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.50f))
                        .clickable { onNavigate("score") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Área de edición estricta 9:16 con columna de elementos estática en la esquina inferior derecha
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .aspectRatio(9f / 16f)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(23.dp)
                    )
                    .clip(RoundedCornerShape(23.dp))
                    .background(Color(0xFF050505)),
                contentAlignment = Alignment.Center
            ) {
                if (photoBytes != null) {
                    AsyncImage(
                        model = photoBytes,
                        contentDescription = "Foto editada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    CameraPreview(modifier = Modifier.fillMaxSize())
                }

                // Columna de elementos en la esquina inferior derecha (con calendario y textos más grandes)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    overlayItems.forEach { item ->
                        val fontSizeSp = 22.sp

                        if (item.isCalendar) {
                            Box(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.90f))
                                    .padding(10.dp)
                            ) {
                                HeatCalendar(
                                    modifier = Modifier.fillMaxWidth(),
                                    wordCounts = dailyWordCounts
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item.text,
                                    color = Color.White,
                                    fontSize = fontSizeSp,
                                    fontWeight = FontWeight.Bold,
                                    style = TextStyle(
                                        shadow = Shadow(
                                            color = Color.Black,
                                            offset = Offset(2f, 2f),
                                            blurRadius = 6f
                                        )
                                    )
                                )

                                if (item.text != "KoEs") {
                                    Text(
                                        text = "✕",
                                        color = Color.Red,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable {
                                                overlayItems = overlayItems.filter { it.id != item.id }
                                            }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botón Compartir
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(111.dp)
                    .height(35.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.70f), Color.White.copy(alpha = 0.15f))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        val finalPhotoBytes = compositor(
                            photoBytes,
                            overlayItems,
                            0f,
                            0f
                        )

                        if (finalPhotoBytes != null) {
                            shareLauncher("¡Mira mi avance en KoEs! 🇰🇷✨", finalPhotoBytes)
                        }
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
    }
}

@Preview(showBackground = true)
@Composable
fun EditScoreViewPreview() {
    EditCameraView()
}
