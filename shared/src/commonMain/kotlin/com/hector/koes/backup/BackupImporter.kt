package com.hector.koes.backup

import androidx.compose.runtime.Composable

@Composable
expect fun rememberBackupImporter(onResult: (String?) -> Unit): () -> Unit
