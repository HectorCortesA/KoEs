package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.material3.MaterialTheme
import com.hector.koes.components.Calendar.HeatCalendar
import com.hector.koes.components.camera.CameraPreview
import com.hector.koes.components.share.rememberImageCompositor
import com.hector.koes.components.share.rememberShareLauncher
import com.hector.koes.ui.theme.*
import com.hector.koes.viewModel.UserProgressManager
import kotlin.math.roundToInt
import kotlin.random.Random

data class OverlayItem(
    val id: Long = Random.nextLong(),
    val text: String,
    val normalizedFontSize: Float = 0.045f,
    val isCalendar: Boolean = false,
    // Posición de la esquina superior izquierda respecto al lienzo (0 a 1).
    val normalizedX: Float = 0.45f,
    val normalizedY: Float = 0.78f
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
        mutableStateOf(listOf(OverlayItem(text = "KoEs")))
    }
    var previewSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val shareLauncher = rememberShareLauncher()
    val compositor = rememberImageCompositor()
    val availableBadges = listOf(
        "Racha: $streak días",
        "$wordsLearned Palabras aprendidas",
        "Mini Calendario"
    )

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding()
                    .padding(horizontal = 21.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Box(
                        modifier = Modifier.width(180.dp).height(45.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSystemInDarkTheme()) Color(0x80202020) else GlassBackground)
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(GlassGradientStart, GlassGradientEnd)
                                ),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { isDropdownExpanded = true }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("Añadir elemento ▾", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (isSystemInDarkTheme()) Color.White else TextSecondary)
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        containerColor = if (isSystemInDarkTheme()) Color(0xF2292929) else GlassDropdownBackground,
                        modifier = Modifier.width(220.dp)
                    ) {
                        availableBadges.forEach { badge ->
                            DropdownMenuItem(
                                text = { Text(badge, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    val isCal = badge.contains("Calendario")
                                    overlayItems = overlayItems + OverlayItem(
                                        text = if (isCal) "Calendario" else badge,
                                        normalizedFontSize = if (isCal) 0.25f else 0.045f,
                                        isCalendar = isCal,
                                        normalizedX = if (isCal) 0.08f else 0.20f,
                                        normalizedY = if (isCal) 0.55f else 0.68f
                                    )
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier.width(59.dp).height(55.dp).clip(CircleShape)
                        .background(GlassBackgroundLight)
                        .clickable { onNavigate("score") },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", fontSize = 20.sp, color = if (isSystemInDarkTheme()) Color.White else TextPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lienzo 9:16. Cada elemento arrastrable vive en sus propias coordenadas.
            Box(
                modifier = Modifier.fillMaxWidth(0.90f).aspectRatio(9f / 16f)
                    .shadow(6.dp, RoundedCornerShape(23.dp))
                    .clip(RoundedCornerShape(23.dp))
                    .background(SurfaceDark)
                    .onSizeChanged { previewSize = it },
                contentAlignment = Alignment.TopStart
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

                overlayItems.forEach { item ->
                    var itemSize by remember(item.id) { mutableStateOf(IntSize.Zero) }
                    val maxX = (previewSize.width - itemSize.width).coerceAtLeast(0)
                    val maxY = (previewSize.height - itemSize.height).coerceAtLeast(0)
                    // El valor normalizado se refiere al lienzo completo, no al rango de desplazamiento.
                    val x = (item.normalizedX * previewSize.width).roundToInt().coerceIn(0, maxX)
                    val y = (item.normalizedY * previewSize.height).roundToInt().coerceIn(0, maxY)
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(x, y) }
                            .onSizeChanged { itemSize = it }
                            .pointerInput(item.id, previewSize, itemSize) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    if (previewSize.width > 0 && previewSize.height > 0) {
                                        overlayItems = overlayItems.map { existing ->
                                            if (existing.id != item.id) existing else {
                                                val nextX = (existing.normalizedX * previewSize.width + dragAmount.x)
                                                    .coerceIn(0f, maxX.toFloat())
                                                val nextY = (existing.normalizedY * previewSize.height + dragAmount.y)
                                                    .coerceIn(0f, maxY.toFloat())
                                                existing.copy(
                                                    normalizedX = nextX / previewSize.width,
                                                    normalizedY = nextY / previewSize.height
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                    ) {
                        if (item.isCalendar) {
                            Box(
                                modifier = Modifier
                                    .width(with(density) { (previewSize.width * 0.72f).toDp() }.coerceAtMost(260.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GlassSheetContainerColor)
                                    .padding(12.dp)
                            ) {
                                HeatCalendar(
                                    modifier = Modifier.fillMaxWidth(),
                                    wordCounts = dailyWordCounts
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = item.text,
                                    color = TextWhite,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = TextStyle(shadow = Shadow(TextPrimary, Offset(2f, 2f), 6f))
                                )
                                if (item.text != "KoEs") {
                                    Text(
                                        text = "✕",
                                        color = TextRed,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable {
                                            overlayItems = overlayItems.filter { it.id != item.id }
                                        }.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .navigationBarsPadding().padding(bottom = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.width(111.dp).height(35.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSystemInDarkTheme()) Color(0x80202020) else GlassBackground)
                    .border(
                        1.dp,
                        Brush.verticalGradient(listOf(GlassGradientStart, GlassGradientEnd)),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        val finalPhotoBytes = compositor(
                            photoBytes, overlayItems,
                            previewSize.width.toFloat(), previewSize.height.toFloat(), dailyWordCounts
                        )
                        if (finalPhotoBytes != null) {
                            shareLauncher("¡Mira mi avance en KoEs!", finalPhotoBytes)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Compartir", color = if (isSystemInDarkTheme()) Color.White else TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditScoreViewPreview() {
    EditCameraView()
}
