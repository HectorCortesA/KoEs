package com.hector.koes.components.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraPreview(
    modifier: Modifier = Modifier,
    isFrontCamera: Boolean = false,
    captureTrigger: Long = 0L,
    onPhotoCaptured: (ByteArray) -> Unit = {}
)
