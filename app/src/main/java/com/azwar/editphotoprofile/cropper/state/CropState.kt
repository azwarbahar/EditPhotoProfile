package com.azwar.editphotoprofile.cropper.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import com.azwar.editphotoprofile.cropper.model.CropProperties

@Composable
fun rememberCropState(
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    cropProperties: CropProperties,
    vararg keys: Any?
): CropState {
    val aspectRatio = cropProperties.aspectRatio
    val overlayRatio = cropProperties.overlayRatio
    val maxZoom = cropProperties.maxZoom
    val fling = cropProperties.fling
    val zoomable = cropProperties.zoomable
    val pannable = cropProperties.pannable
    val rotatable = cropProperties.rotatable

    return remember(*keys) {
        StaticCropState(
            imageSize = imageSize,
            containerSize = containerSize,
            drawAreaSize = drawAreaSize,
            aspectRatio = aspectRatio,
            overlayRatio = overlayRatio,
            maxZoom = maxZoom,
            fling = fling,
            zoomable = zoomable,
            pannable = pannable,
            rotatable = rotatable,
            limitPan = false
        )
    }
}