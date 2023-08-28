package com.azwar.editphotoprofile.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

interface CropOutline {
    val id: Int
    val title: String
}

interface CropShape : CropOutline {
    val shape: Shape
}

@Immutable
data class RectCropShape(
    override val id: Int,
    override val title: String,
) : CropShape {
    override val shape: Shape = RectangleShape
}