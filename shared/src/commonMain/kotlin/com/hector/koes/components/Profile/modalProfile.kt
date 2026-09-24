package com.hector.koes.components.Profile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ModaProfile(
    nameProfile: String,
    photoUrl: String,
    onBack: () -> Unit = {},
    onPickImage: () -> Unit = {},
    onUpdateProfile: (String) -> Unit = {},
    onUpdateProfileWithPhoto: (String, String) -> Unit = { name, _ -> onUpdateProfile(name) }
) {

    var name by remember {
        mutableStateOf(nameProfile)
    }

    var currentPhotoUrl by remember(photoUrl) {
        mutableStateOf(photoUrl)
    }

    var isEditing by remember {
        mutableStateOf(false)
    }

    val launchImagePicker = rememberImagePickerLauncher { uri ->
        if (uri != null) {
            currentPhotoUrl = uri
            onPickImage()
        }
    }

    val scope = rememberCoroutineScope()
    val animatableOffset = remember { Animatable(0f) }

    fun handleDragEnd() {
        val currentOffset = animatableOffset.value
        if (currentOffset > 200f) {
            scope.launch {
                animatableOffset.animateTo(1000f, tween(150))
                onBack()
            }
        } else {
            scope.launch {
                animatableOffset.animateTo(0f, tween(200))
            }
        }
    }

    val scrimAlpha = (0.20f * (1f - (animatableOffset.value / 600f).coerceIn(0f, 1f)))

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // Capa semitransparente encima del fondo (al hacer clic cierra el modal)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = scrimAlpha)
                )
                .clickable { onBack() }
        )

        // Modal deslizable
        Box(
            modifier = Modifier
                .width(402.dp)
                .height(495.dp)
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, animatableOffset.value.roundToInt()) }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = { handleDragEnd() },
                        onDragCancel = { handleDragEnd() },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = (animatableOffset.value + dragAmount).coerceAtLeast(0f)
                            scope.launch {
                                animatableOffset.snapTo(newOffset)
                            }
                        }
                    )
                }
                .background(
                    color = Color(0xF2FFFFFF),
                    shape = RoundedCornerShape(
                        topStart = 50.dp,
                        topEnd = 50.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 16.dp,
                        start = 30.dp,
                        end = 30.dp,
                        bottom = 30.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Línea superior
                Box(
                    modifier = Modifier
                        .width(45.dp)
                        .height(5.dp)
                        .background(
                            color = Color(0xFFBDBDBD),
                            shape = RoundedCornerShape(10.dp)
                        )
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = if (isEditing) {
                        "Editar perfil"
                    } else {
                        "Perfil"
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // Foto
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(
                            enabled = isEditing
                        ) {
                            launchImagePicker()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    if (currentPhotoUrl.isNotEmpty()) {

                        AsyncImage(
                            model = currentPhotoUrl,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Text(
                            text = if (isEditing) {
                                "Cambiar foto"
                            } else {
                                "Foto"
                            },
                            color = Color(0xFF333333)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(22.dp)
                )

                if (!isEditing) {

                    Text(
                        text = name,
                        color = Color(0xFF333333),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(25.dp)
                    )

                    GlassButton(
                        text = "Editar",
                        onClick = {
                            isEditing = true
                        }
                    )
                }

                if (isEditing) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Nombre",
                                fontSize = 15.sp
                            )
                        },
                        modifier = Modifier
                            .shadow(
                                elevation = 1.dp,
                                spotColor = Color(0x40000000),
                                ambientColor = Color(0x40000000),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .width(237.dp)
                            .height(60.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFEDEDED),
                            unfocusedContainerColor = Color(0xFFEDEDED),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        )
                    )
                    Spacer(
                        modifier = Modifier.height(25.dp)
                    )

                    GlassButton(
                        text = "Actualizar",
                        onClick = {
                            onUpdateProfileWithPhoto(name, currentPhotoUrl)
                            isEditing = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit = {}
) {

    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(50.dp)
            .clip(
                RoundedCornerShape(25.dp)
            )
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(25.dp),
        color = Color.Black.copy(alpha = 0.05f),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.60f),
                    Color.White.copy(alpha = 0.15f)
                )
            )
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 402,
    heightDp = 844
)
@Composable
fun ModaProfilePreview() {

    ModaProfile(
        nameProfile = "Hector Cortes",
        photoUrl = ""
    )
}