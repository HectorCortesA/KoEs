package com.hector.koes.components.Profile

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(onResult: (String?) -> Unit): () -> Unit
