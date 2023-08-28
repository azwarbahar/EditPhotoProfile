package com.azwar.editphotoprofile.cropper.image

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize

@Composable
fun ImageWithConstraints(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentScale: ContentScale = Fit,
    contentDescription: String? = null,
    drawImage: Boolean = true,
    content: @Composable ImageScope.() -> Unit
) {
    val semantics = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else Modifier

    BoxWithConstraints(
        modifier = Modifier
            .then(semantics),
        contentAlignment = Alignment.Center,
    ) {

        val bitmapWidth = imageBitmap.width
        val bitmapHeight = imageBitmap.height

        val (boxWidth: Int, boxHeight: Int) = getParentSize(bitmapWidth, bitmapHeight)

        val srcSize = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
        val dstSize = Size(boxWidth.toFloat(), boxHeight.toFloat())

        val scaleFactor = contentScale.computeScaleFactor(srcSize, dstSize)

        val imageWidth = bitmapWidth * scaleFactor.scaleX
        val imageHeight = bitmapHeight * scaleFactor.scaleY

        val bitmapRect = getScaledBitmapRect(
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            bitmapWidth = bitmapWidth,
            bitmapHeight = bitmapHeight
        )

        ImageLayout(
            constraints = constraints,
            imageBitmap = imageBitmap,
            bitmapRect = bitmapRect,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            boxWidth = boxWidth,
            boxHeight = boxHeight,
            drawImage = drawImage,
            content = content
        )
    }
}

@Composable
private fun ImageLayout(
    constraints: Constraints,
    imageBitmap: ImageBitmap,
    bitmapRect: IntRect,
    imageWidth: Float,
    imageHeight: Float,
    boxWidth: Int,
    boxHeight: Int,
    drawImage: Boolean = true,
    content: @Composable ImageScope.() -> Unit
) {
    val density = LocalDensity.current
    val canvasWidthInDp: Dp
    val canvasHeightInDp: Dp

    with(density) {
        canvasWidthInDp = imageWidth.coerceAtMost(boxWidth.toFloat()).toDp()
        canvasHeightInDp = imageHeight.coerceAtMost(boxHeight.toFloat()).toDp()
    }
    val imageScopeImpl = ImageScopeImpl(
        density = density,
        constraints = constraints,
        imageWidth = canvasWidthInDp,
        imageHeight = canvasHeightInDp,
        rect = bitmapRect
    )
    if (drawImage) {
        Canvas(modifier = Modifier.clipToBounds()) {
            val canvasWidth = size.width.toInt()
            val canvasHeight = size.height.toInt()
            translate(
                top = (-imageWidth.toInt() + canvasHeight) / 2f,
                left = (-imageWidth.toInt() + canvasWidth) / 2f,
            ) {
                drawImage(
                    imageBitmap,
                    srcSize = IntSize(imageBitmap.width, imageBitmap.height),
                    dstSize = IntSize(imageWidth.toInt(), imageWidth.toInt()),
                    alpha = DefaultAlpha,
                    filterQuality = DefaultFilterQuality
                )
            }
        }
    }
    imageScopeImpl.content()
}




// ImageContentScaleUtils.kt
internal fun BoxWithConstraintsScope.getParentSize(bitmapWidth: Int, bitmapHeight: Int): IntSize {
    val hasBoundedDimens = constraints.hasBoundedWidth && constraints.hasBoundedHeight
    val hasFixedDimens = constraints.hasFixedWidth && constraints.hasFixedHeight
    val boxWidth: Int = if (hasBoundedDimens || hasFixedDimens) {
        constraints.maxWidth
    } else {
        constraints.minWidth.coerceAtLeast(bitmapWidth)
    }
    val boxHeight: Int = if (hasBoundedDimens || hasFixedDimens) {
        constraints.maxHeight
    } else {
        constraints.minHeight.coerceAtLeast(bitmapHeight)
    }
    return IntSize(boxWidth, boxHeight)
}

// ImageContentScaleUtils.kt
internal fun getScaledBitmapRect(
    boxWidth: Int,
    boxHeight: Int,
    imageWidth: Float,
    imageHeight: Float,
    bitmapWidth: Int,
    bitmapHeight: Int
): IntRect {
    val scaledBitmapX = boxWidth / imageWidth
    val scaledBitmapY = boxHeight / imageHeight
    val topLeft = IntOffset(
        x = (bitmapWidth * (imageWidth - boxWidth) / imageWidth / 2)
            .coerceAtLeast(0f).toInt(),
        y = (bitmapHeight * (imageHeight - boxHeight) / imageHeight / 2)
            .coerceAtLeast(0f).toInt()
    )
    val size = IntSize(
        width = (bitmapWidth * scaledBitmapX).toInt().coerceAtMost(bitmapWidth),
        height = (bitmapHeight * scaledBitmapY).toInt().coerceAtMost(bitmapHeight)
    )
    return IntRect(offset = topLeft, size = size)
}