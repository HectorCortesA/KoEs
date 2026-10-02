package com.hector.koes.components.share

import androidx.compose.runtime.Composable
import com.hector.koes.View.OverlayItem

actual fun getImageDimensions(photoBytes: ByteArray?): Pair<Float, Float> {
    return Pair(1080f, 1920f)
}

@Composable
actual fun rememberImageCompositor(): (photoBytes: ByteArray?, overlayItems: List<OverlayItem>, previewWidth: Float, previewHeight: Float, dailyWordCounts: Map<Int, Int>) -> ByteArray? {
    return { photoBytes, _, _, _, _ ->
        photoBytes
    }
}
