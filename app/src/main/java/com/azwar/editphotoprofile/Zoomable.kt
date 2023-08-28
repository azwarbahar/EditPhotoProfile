package com.azwar.editphotoprofile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.roundToInt

class Zoomable {
}

internal data class SampledImageBitmap(val imageBitmap: ImageBitmap, val inSampleSize: Int)

internal suspend fun loadSampledImageBitmap(context: Context, uri: Uri, requireSize: IntSize): SampledImageBitmap {
    return withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(resolver.openInputStream(uri), Rect(), this)
        }
        val inSampleSize = calculateInSampleSize(IntSize(options.outWidth, options.outHeight), requireSize)
        options.apply {
            inJustDecodeBounds = false
            this.inSampleSize = inSampleSize
        }
        BitmapFactory.decodeStream(resolver.openInputStream(uri), Rect(), options)
            ?.let { SampledImageBitmap(it.asImageBitmap(), inSampleSize) }
            ?: throw IOException("Failed to decode stream.")
    }
}

internal fun calculateInSampleSize(imageSize: IntSize, requireSize: IntSize): Int {
    var inSampleSize = 1
    if (imageSize.height > requireSize.height || imageSize.width > requireSize.width) {
        val halfHeight: Int = imageSize.height / 2
        val halfWidth: Int = imageSize.width / 2
        while (halfHeight / inSampleSize >= requireSize.height && halfWidth / inSampleSize >= requireSize.width) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

fun clampOffset(offset: Offset, minScale: Float, maxScale: Float): Offset {
    val scaledWidth = (maxScale - minScale) * 0.5f
    val scaledHeight = (maxScale - minScale) * 0.5f

    val clampedX = offset.x.coerceIn(-scaledWidth, scaledWidth)
    val clampedY = offset.y.coerceIn(-scaledHeight, scaledHeight)

    return Offset(clampedX, clampedY)
}

fun clamp(value: Float, min: Float, max: Float): Float {
    return value.coerceIn(min, max)
}
@Composable
fun ZoomableImage(
    imageBitmap: ImageBitmap,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    context: Context,
    onLongPress: ((Offset) -> Unit)? = null,
    onDoubleTap: ((Offset) -> Unit)? = null,
    onOffsetChange: ((Offset) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    var layout: LayoutCoordinates? = null

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var translation by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale *= zoomChange
        translation += panChange.times(scale)
    }

    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    LaunchedEffect(offset){
        Log.e("TAG", "ZoomableImage: X = ${offset.x} dan Y = ${offset.y}", )
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .transformable(state = transformableState)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = onLongPress,
                    onDoubleTap = {
                        if (onDoubleTap != null) {
                            onDoubleTap(it)
                        }
                        val maxScale = 2f
                        val midScale = 1.5f
                        val minScale = 1f
                        if (scale < midScale) {
                            // Zoom in
                            scope.launch {
                                transformableState.animateZoomBy(midScale / scale)
                            }
                        } else {
                            scope.launch {
                                transformableState.animateZoomBy(minScale / scale)
                            }
                        }
                        val targetScale = when {
                            scale >= minScale -> midScale
                            scale >= midScale -> maxScale
                            scale >= maxScale -> minScale
                            else -> scale
                        }
                        scope.launch {
                            transformableState.animateZoomBy(targetScale / scale)
                        }
                    }
                )
            }
            .pointerInput(Unit) {

                detectTransformGestures { _, pan, zoom, _ ->

                    val newScale = maxOf(1f, minOf(scale * zoom, 3f))
//
                    val aspectRatio = size.width / size.height
                    val imageWidth = imageBitmap.width.toFloat()
                    val imageHeight = imageBitmap.height.toFloat()
                    val imageAspectRatio = imageWidth / imageHeight
//////
                    val targetScale = if (aspectRatio > imageAspectRatio) {
                        // Layar lebih lebar, kita ikuti tinggi gambar untuk mengatur skala
                        size.height / imageHeight
                    } else {
                        // Layar lebih tinggi atau memiliki aspek yang sama, kita ikuti lebar gambar untuk mengatur skala
                        size.width / imageWidth
                    }
                    val scaledImageWidth = imageWidth * newScale * targetScale
                    val scaledImageHeight = imageHeight * newScale * targetScale

                    val maxX = (scaledImageWidth - size.width) / 2
                    val minX = -maxX
                    val newOffsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))

                    val maxY = (scaledImageHeight - size.height) / 2
                    val minY = -maxY
                    val newOffsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))

                    offsetX = newOffsetX
                    offsetY = newOffsetY
                    scale = newScale
//
//
//
////
//                    val newScale = (scale * zoom).coerceIn(1f, 2.5f) // Adjust max zoom here
//                    val newOffset = if (newScale == 1f) Offset(0f, 0f) else offset

//                    val aspectRatio = size.width / size.height
//                    val imageWidth = imageBitmap.width.toFloat()
//                    val imageHeight = imageBitmap.height.toFloat()
//                    val imageAspectRatio = imageWidth / imageHeight
//                    val scaledImageWidth = imageWidth * newScale * size.width / imageWidth
//                    val scaledImageHeight = imageHeight * newScale * size.height / imageHeight
//
//                    scale = newScale
//                    offset = newOffset
//
//                    val newOffsetXMin = -imageWidth * (newScale - 1)
//                    val newOffsetXMax = imageWidth * (newScale - 1)
//
//                    val newOffsetYMin = -imageHeight * (newScale - 1)
//                    val newOffsetYMax = imageHeight * (newScale - 1)
//                    val topBound = -imageHeight * 0.5f
//                    val bottomBound = imageHeight * 0.5f
//
//                    if (newScale > 1f) {
//                        offsetX = clamp(
//                            newOffset.x * zoom + pan.x * newScale,
//                            newOffsetXMin,
//                            newOffsetXMax
//                        )
//                        offsetY =
//                            clamp(newOffset.y * zoom + pan.y * newScale, topBound, bottomBound)
//                        offset = Offset(offsetX, offsetY)
//                    }

//
                }
//                forEachGesture {
//                    awaitPointerEventScope {
//                        val down = awaitFirstDown(requireUnconsumed = false)
//                        drag(down.id) {
//                            if (layout == null) return@drag
//                            val maxX = layout.size.width * (scale - 1) / 2f
//                            val maxY = layout.size.height * (scale - 1) / 2f
//                            val targetTranslation = (it.positionChange() + translation)
//                            if (targetTranslation.x > -maxX && targetTranslation.x < maxX &&
//                                targetTranslation.y > -maxY && targetTranslation.y < maxY
//                            ) {
//                                translation = targetTranslation
//                                if (it.positionChange() != Offset.Zero) it.consume()
//                            }
//                        }
//                    }
//                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = contentDescription,
            modifier = Modifier
                .matchParentSize()
//                .onPlaced { layout = it }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = translation.x,
                    translationY = translation.y
                ),
//                .offset {
//                    IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
//                },
            contentScale = ContentScale.Fit
        )

        LaunchedEffect(transformableState.isTransformInProgress) {
            if (!transformableState.isTransformInProgress) {
                if (scale < 1f) {
                    val originScale = scale
                    val originTranslation = translation
                    AnimationState(initialValue = 0f).animateTo(
                        1f,
                        SpringSpec(stiffness = Spring.StiffnessLow)
                    ) {
                        scale = originScale + (1 - originScale) * this.value
                        translation = originTranslation * (1 - this.value)
                    }
                } else {
                    if (layout == null) return@LaunchedEffect
                    val maxX = layout.size.width * (scale - 1) / 2f
                    val maxY = layout.size.height * (scale - 1) / 2f
                    val target = Offset(
                        translation.x.coerceIn(-maxX, maxX),
                        translation.y.coerceIn(-maxY, maxY)
                    )
                    AnimationState(
                        typeConverter = Offset.VectorConverter,
                        initialValue = translation
                    ).animateTo(target, SpringSpec(stiffness = Spring.StiffnessLow)) {
                        translation = this.value
                    }
                }
            }
        }
    }
}