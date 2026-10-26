package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.material3.MaterialTheme
import com.hector.koes.components.camera.CameraPreview
import com.hector.koes.ui.theme.*

@Composable
fun CameraView(
    onNavigate: (String) -> Unit = {},
    onPhotoApproved: (ByteArray) -> Unit = {}
) {
    var isFrontCamera by remember {
        mutableStateOf(false)
    }

    var captureTrigger by remember {
        mutableLongStateOf(0L)
    }

    var capturedPhotoBytes by remember {
        mutableStateOf<ByteArray?>(null)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Contenedor adaptable de la cámara (wrapper no recortado)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .aspectRatio(9f / 16f)
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        ) {

            // Área principal de cámara / preview de foto (con clip redondeado)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(90.dp),
                        spotColor = ShadowColorToken,
                        ambientColor = ShadowColorToken
                    )
                    .clip(
                        RoundedCornerShape(90.dp)
                    )
                    .background(
                        color = CameraPreviewBackgroundColor,
                        shape = RoundedCornerShape(90.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (capturedPhotoBytes != null) {

                    // Preview de la fotografía capturada
                    AsyncImage(
                        model = capturedPhotoBytes,
                        contentDescription = "Foto capturada",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    // Cámara en vivo
                    CameraPreview(
                        modifier = Modifier
                            .fillMaxSize(),
                        isFrontCamera = isFrontCamera,
                        captureTrigger = captureTrigger,
                        onPhotoCaptured = { bytes ->
                            capturedPhotoBytes = bytes
                        }
                    )
                }
            }

            // Botón para voltear cámara (frontal / trasera) colocado en el contenedor wrapper (fuera del clip del card)
            if (capturedPhotoBytes == null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-16).dp, y = 16.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = ShadowColorToken,
                            ambientColor = ShadowColorToken
                        )
                        .clip(CircleShape)
                        .background(
                            color = GlassBackgroundLight
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    GlassBorderTopLight,
                                    GlassBorderBottomDark
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
                            isFrontCamera = !isFrontCamera
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↻",
                        fontSize = 22.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Panel inferior
        val isDark = isSystemInDarkTheme()
        val bottomPanelBg = if (isDark) Color(0xF2292929) else GlassDropdownBackground
        val buttonBg = if (isDark) Color(0x804D6578) else CameraViewButtonContainerColor

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .align(Alignment.BottomCenter)
                .clip(
                    RoundedCornerShape(
                        topStart = 30.dp,
                        topEnd = 30.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(bottomPanelBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {

            // ESTADO 1: Cámara activa
            if (capturedPhotoBytes == null) {

                Button(
                    onClick = {
                        captureTrigger += 1L
                    },
                    modifier = Modifier
                        .size(86.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBg
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(CardBackground)
                    )
                }

            } else {

                // ESTADO 2: Foto capturada
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Botón REPETIR
                    Button(
                        onClick = {
                            capturedPhotoBytes = null
                            captureTrigger = 0L
                        },
                        modifier = Modifier
                            .size(86.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonBg
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {

                        Text(
                            text = "↻",
                            color = if (isDark) Color.White else TextPrimary,
                            fontSize = 28.sp
                        )
                    }

                    // Espacio entre botones
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                    )

                    // Botón APROBAR
                    Button(
                        onClick = {
                            capturedPhotoBytes?.let { photo ->
                                onPhotoApproved(photo)
                            }
                        },
                        modifier = Modifier
                            .size(86.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonBg
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {

                        Text(
                            text = "✓",
                            color = if (isDark) Color.White else TextPrimary,
                            fontSize = 30.sp
                        )
                    }
                }
            }
          }
        }

        // Botón X (Regresar / Cerrar) en la esquina derecha del panel inferior
        Box(
            modifier = Modifier
                .size(54.dp)
                .align(Alignment.BottomEnd)
                .offset(
                    x = (-24).dp,
                    y = (-123).dp
                )
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    spotColor = ShadowColorToken,
                    ambientColor = ShadowColorToken
                )
                .clip(CircleShape)
                .background(
                    color = GlassBackgroundLight
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassBorderColorStart,
                            GlassBorderColorEnd
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
                fontSize = 22.sp,
                color = CameraCloseButtonTextColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 402,
    heightDp = 874
)
@Composable
fun CameraViewPreview() {
    CameraView()
}
