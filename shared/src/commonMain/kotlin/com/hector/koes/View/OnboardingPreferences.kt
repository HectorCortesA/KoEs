package com.hector.koes.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.hector.koes.components.Profile.rememberImagePickerLauncher
import com.hector.koes.resources.Res
import com.hector.koes.ui.theme.Background
import com.hector.koes.viewModel.OnboardingViewModel

@Composable
fun OnboardingPreferences(
    viewModel: OnboardingViewModel = viewModel { OnboardingViewModel() },
    onFinalize: () -> Unit = {}
) {
    var query by remember {
        mutableStateOf(viewModel.userPhotoUrl.value)
    }

    val launchImagePicker = rememberImagePickerLauncher { uri ->
        if (uri != null) {
            query = uri
            viewModel.setUserPhotoUrl(uri)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // 1. Imagen de fondo colocada en la parte izquierda de abajo
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            AsyncImage(
                model = Res.getUri("files/peopleheart.svg"),
                contentDescription = "Mascota",
                modifier = Modifier
                    .fillMaxWidth(0.75f)
            )
        }

        // 2. Contenido centrado: Avatar (selector de imagen) y botón Finalizar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Selector circular tipo glass para imagen de perfil
                Box(
                    modifier = Modifier
                        .width(174.dp)
                        .height(171.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(color = Color(0x33D9D9D9), shape = RoundedCornerShape(size = 100.dp))
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
                            shape = RoundedCornerShape(size = 100.dp)
                        )
                        .clickable {
                            launchImagePicker()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (query.isNotEmpty()) {
                        AsyncImage(
                            model = query,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(100.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "Foto",
                            color = Color.Black.copy(alpha = 0.5f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Box(
                    modifier = Modifier.height(24.dp)
                )

                // Botón Finalizar tipo glass
                Box(
                    modifier = Modifier
                        .width(103.dp)
                        .height(39.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color = Color(0x33D9D9D9))
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
                            viewModel.setUserPhotoUrl(query)
                            viewModel.completeOnboarding()
                            onFinalize()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Finalizar",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreferencesPreview() {
    OnboardingPreferences()
}
