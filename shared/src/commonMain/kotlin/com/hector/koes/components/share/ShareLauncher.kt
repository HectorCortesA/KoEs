package com.hector.koes.components.share

import androidx.compose.runtime.Composable

@Composable
expect fun rememberShareLauncher(): (text: String, imageBytes: ByteArray?) -> Unit
