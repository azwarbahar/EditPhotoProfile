package com.azwar.editphotoprofile

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 5f,
    content: @Composable ZoomableBoxScope.() -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .clip(RectangleShape)
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = maxOf(minScale, minOf(scale * zoom, maxScale))

//                    val aspectRatio = size.width / size.height
//                    val imageWidth = imageBitmap.width.toFloat()
//                    val imageHeight = imageBitmap.height.toFloat()
//                    val imageAspectRatio = imageWidth / imageHeight

//                    val targetScale = if (aspectRatio > imageAspectRatio) {
//                        // Layar lebih lebar, kita ikuti tinggi gambar untuk mengatur skala
//                        size.height / imageHeight
//                    } else {
//                        // Layar lebih tinggi atau memiliki aspek yang sama, kita ikuti lebar gambar untuk mengatur skala
//                        size.width / imageWidth
//                    }



                    val maxX = (size.width * (newScale - 1)) / 2
                    val minX = -maxX
                    val newOffsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))

                    val maxY = (size.height * (newScale - 1)) / 2
                    val minY = -maxY
                    val newOffsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))

                    if (newScale <= 1.0) {
                        // Jika skala kurang dari atau sama dengan 1.0 (tidak ter-zoom), kembalikan ke posisi tengah
                        offsetX = 0f
                        offsetY = 0f
                    } else {
                        // Jika ter-zoom, pastikan gambar tetap dalam jangkauan 1:1
                        offsetX = newOffsetX
                        offsetY = newOffsetY
                    }
                    scale = newScale

//                    scale = maxOf(minScale, minOf(scale * zoom, maxScale))
//                    val maxX = (size.width * (scale - 1)) / 2
//                    val minX = -maxX
//                    offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
//                    val maxY = (size.height * (scale - 1)) / 2
//                    val minY = -maxY
//                    offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                }
            }
    ) {
        val scope = ZoomableBoxScopeImpl(scale, offsetX, offsetY)
        scope.content()
    }
}

interface ZoomableBoxScope {
    val scale: Float
    val offsetX: Float
    val offsetY: Float
}

private data class ZoomableBoxScopeImpl(
    override val scale: Float,
    override val offsetX: Float,
    override val offsetY: Float
) : ZoomableBoxScope