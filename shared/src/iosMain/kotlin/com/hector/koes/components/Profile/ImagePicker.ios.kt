package com.hector.koes.components.Profile

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePickerLauncher(onResult: (String?) -> Unit): () -> Unit {
    return {
        onResult(null)
    }
}
