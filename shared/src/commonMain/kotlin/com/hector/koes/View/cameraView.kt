package com.hector.koes.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hector.koes.components.camera.CameraPreview
import com.hector.koes.ui.theme.Background

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
            .background(Background)
    ) {

        // Contenedor adaptable de la cámara
        Box(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .aspectRatio(361f / 676f)
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        ) {

            // Área principal de cámara / preview de foto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(90.dp),
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x40000000)
                    )
                    .clip(
                        RoundedCornerShape(90.dp)
                    )
                    .background(
                        color = Color(0xFF050505),
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

            // Botón superior derecho
            // Solo aparece mientras está activa la cámara
            if (capturedPhotoBytes == null) {

                Box(
                    modifier = Modifier
                        .size(59.dp)
                        .align(Alignment.TopEnd)
                        .offset(
                            x = 12.dp,
                            y = (-10).dp
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .clip(CircleShape)
                        .background(
                            color = Color(0x80FFFFFF)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.8f),
                                    Color.White.copy(alpha = 0.2f)
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
                        text = "←",
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                }
            }
        }

        // Panel inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(139.dp)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color(0x80D9D9D9),
                    shape = RoundedCornerShape(
                        topStart = 30.dp,
                        topEnd = 30.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                ),
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
                        containerColor = Color(0x80FFFFFF)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White)
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
                            containerColor = Color(0x80FFFFFF)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {

                        Text(
                            text = "↻",
                            color = Color.Black,
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
                            containerColor = Color(0x80FFFFFF)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {

                        Text(
                            text = "✓",
                            color = Color.Black,
                            fontSize = 30.sp
                        )
                    }
                }
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
fun CameraViewPreview() {
    CameraView()
}