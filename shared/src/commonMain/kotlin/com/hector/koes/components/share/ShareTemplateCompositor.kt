package com.hector.koes.components.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer

@Composable
expect fun rememberShareTemplateCompositor(): suspend (
    userName: String,
    learnedWords: Int,
    streakDays: Int,
    wordCounts: Map<Int, Int>
) -> ByteArray?
