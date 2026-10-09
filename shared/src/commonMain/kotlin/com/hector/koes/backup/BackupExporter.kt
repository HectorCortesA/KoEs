package com.hector.koes.backup

import androidx.compose.runtime.Composable

@Composable
expect fun rememberBackupExporter(onResult: (Boolean) -> Unit): (jsonString: String, defaultFileName: String) -> Unit
