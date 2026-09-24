package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hector.koes.components.camera.CameraPreview
import com.hector.koes.components.share.rememberShareLauncher
import com.hector.koes.ui.theme.Background
import kotlin.math.roundToInt
import kotlin.random.Random

data class OverlayItem(
    val id: Long = Random.nextLong(),
    val text: String,
    var offsetX: Float = 50f,
    var offsetY: Float = 50f
)

@Composable
fun EditCameraView(
    onNavigate: (String) -> Unit = {},
    photoBytes: ByteArray? = null
) {

    var isDropdownExpanded by remember { mutableStateOf(false) }
    var overlayItems by remember { mutableStateOf(listOf<OverlayItem>()) }
    val shareLauncher = rememberShareLauncher()

    val availableBadges = listOf(
        "🔥 Racha: 2 días",
        "📚 10 Palabras aprendidas",
        "🇰🇷 KoEs Learner",
        "⭐ Nivel Básico",
        "💬 Conversación"
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

            // Barra superior con selector desplegable glass y botón circular cancelar
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Añadir etiqueta ▾",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                        }
                    }

                    // Lista desplegable con diseño glass
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier
                            .width(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.85f))
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.90f),
                                        Color.White.copy(alpha = 0.30f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        availableBadges.forEach { badge ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = badge,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF333333)
                                    )
                                },
                                onClick = {
                                    overlayItems = overlayItems + OverlayItem(
                                        text = badge,
                                        offsetX = 30f + (overlayItems.size * 15f),
                                        offsetY = 50f + (overlayItems.size * 15f)
                                    )
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Botón circular de cancelar y regresar a ScoreView
                Box(
                    modifier = Modifier
                        .width(59.dp)
                        .height(55.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.50f))
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.70f),
                                    Color.White.copy(alpha = 0.15f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
                            onNavigate("score")
                        },
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

            // Área principal de vista previa con overlay de elementos reubicables
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .aspectRatio(359f / 620f)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(23.dp),
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x40000000)
                    )
                    .clip(RoundedCornerShape(23.dp))
                    .background(Color(0xFF050505)),
                contentAlignment = Alignment.Center
            ) {
                if (photoBytes != null) {
                    AsyncImage(
                        model = photoBytes,
                        contentDescription = "Foto a editar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Elementos/etiquetas acomodables con arrastre libre
                overlayItems.forEach { item ->
                    var itemX by remember { mutableStateOf(item.offsetX) }
                    var itemY by remember { mutableStateOf(item.offsetY) }

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(itemX.roundToInt(), itemY.roundToInt()) }
                            .pointerInput(item.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    itemX += dragAmount.x
                                    itemY += dragAmount.y
                                    item.offsetX = itemX
                                    item.offsetY = itemY
                                }
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.text,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "✕",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                modifier = Modifier.clickable {
                                    overlayItems = overlayItems.filter { it.id != item.id }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Panel de botón Compartir
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(111.dp)
                    .height(35.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = Color.White.copy(alpha = 0.18f)
                    )
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
                    .clickable {
                        shareLauncher("¡Mira mi avance en KoEs! 🇰🇷✨", photoBytes)
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

@Preview(
    showBackground = true,
    widthDp = 402,
    heightDp = 874
)
@Composable
fun EditScoreView() {
    EditCameraView()
}
