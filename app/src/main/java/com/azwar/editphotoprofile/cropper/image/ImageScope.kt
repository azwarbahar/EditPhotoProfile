package com.azwar.editphotoprofile.cropper.image

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect

@Stable
interface ImageScope {
    val constraints: Constraints
    val minWidth: Dp
    val maxWidth: Dp
    val minHeight: Dp
    val maxHeight: Dp
    val imageWidth: Dp
    val imageHeight: Dp
    val rect: IntRect
}

internal data class ImageScopeImpl(
    private val density: Density,
    override val constraints: Constraints,
    override val imageWidth: Dp,
    override val imageHeight: Dp,
    override val rect: IntRect,
) : ImageScope {
    override val minWidth: Dp get() = with(density) { constraints.minWidth.toDp() }
    override val maxWidth: Dp
        get() = with(density) {
            if (constraints.hasBoundedWidth) constraints.maxWidth.toDp() else Dp.Infinity
        }
    override val minHeight: Dp get() = with(density) { constraints.minHeight.toDp() }
    override val maxHeight: Dp
        get() = with(density) {
            if (constraints.hasBoundedHeight) constraints.maxHeight.toDp() else Dp.Infinity
        }
}

@Composable
internal fun getScaledImageBitmap(
    imageWidth: Dp,
    imageHeight: Dp,
    rect: IntRect,
    bitmap: ImageBitmap,
    contentScale: ContentScale
): ImageBitmap {
    val scaledBitmap =
        remember(bitmap, rect, imageWidth, imageHeight, contentScale) {
            Bitmap.createBitmap(
                bitmap.asAndroidBitmap(),
                rect.left,
                rect.top,
                rect.width,
                rect.height
            ).asImageBitmap()
        }
    return scaledBitmap
}