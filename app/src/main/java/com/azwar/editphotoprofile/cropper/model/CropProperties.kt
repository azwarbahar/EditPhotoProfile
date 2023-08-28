package com.azwar.editphotoprofile.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


object CropDefaults {

    fun properties(
        handleSize: Float,
        maxZoom: Float = 5f,
        contentScale: ContentScale = Fit,
        cropOutlineProperty: CropOutlineProperty,
        aspectRatio: AspectRatio =  AspectRatio(1 / 1f),
        overlayRatio: Float = 1f,
        pannable: Boolean = true,
        fling: Boolean = false,
        zoomable: Boolean = true,
        rotatable: Boolean = false,
        fixedAspectRatio: Boolean = false,
        requiredSize: IntSize? = null,
        minDimension: IntSize? = null,
    ): CropProperties {
        return CropProperties(
            handleSize = handleSize,
            contentScale = contentScale,
            cropOutlineProperty = cropOutlineProperty,
            maxZoom = maxZoom,
            aspectRatio = aspectRatio,
            overlayRatio = overlayRatio,
            pannable = pannable,
            fling = fling,
            zoomable = zoomable,
            rotatable = rotatable,
            fixedAspectRatio = fixedAspectRatio,
            requiredSize = requiredSize,
            minDimension = minDimension,
        )
    }

    fun style(
        backgroundColor: Color =  Color.Transparent
    ): CropStyle {
        return CropStyle(
            backgroundColor = backgroundColor
        )
    }
}

@Immutable
data class CropProperties internal constructor(
    val handleSize: Float,
    val contentScale: ContentScale,
    val cropOutlineProperty: CropOutlineProperty,
    val aspectRatio: AspectRatio,
    val overlayRatio: Float,
    val pannable: Boolean,
    val fling: Boolean,
    val rotatable: Boolean,
    val zoomable: Boolean,
    val maxZoom: Float,
    val fixedAspectRatio: Boolean = false,
    val requiredSize: IntSize? = null,
    val minDimension: IntSize? = null,
)

@Immutable
data class CropStyle internal constructor(
    val backgroundColor: Color,
)

@Immutable
data class CropOutlineProperty(
    val outlineType: OutlineType,
    val cropOutline: CropOutline
)

enum class OutlineType {
    Rect, RoundedRect, CutCorner, Oval, Polygon, Custom, ImageMask
}