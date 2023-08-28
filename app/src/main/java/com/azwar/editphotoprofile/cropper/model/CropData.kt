package com.azwar.editphotoprofile.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

@Immutable
data class CropData(
    val zoom: Float = 1f,
    val pan: Offset = Offset.Zero,
    val rotation: Float = 0f,
    val overlayRect: Rect,
    val cropRect: Rect
)
