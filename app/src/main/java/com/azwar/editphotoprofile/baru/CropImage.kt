package com.azwar.editphotoprofile.baru

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.azwar.editphotoprofile.R
import kotlin.math.max
import kotlin.math.min

@Composable
fun CropImage(
    bitmap: ImageBitmap,
    cropRect: MutableState<Rect>,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
//        Image(
//            bitmap = bitmap,
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxWidth()
//                .pointerInput(Unit) {
//                    detectTransformGestures { _, pan, zoom, _ ->
//                        cropRect.value = calculateNewCropRect(cropRect.value, pan, zoom)
//                    }
//                }
//                .background(Color.Black)
//        )
        DrawCropOverlay(cropRect)
    }
}

@Composable
fun DrawCropOverlay(cropRect: MutableState<Rect>) {
    val cropModifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
            translationX = cropRect.value.left
            translationY = cropRect.value.top
            scaleX = cropRect.value.width / size.width
            scaleY = cropRect.value.height / size.height
        }

    var startPos by remember { mutableStateOf(Offset(0f, 0f)) }
    Layout(content = { /*TODO*/ } ){measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {

        }
    }
    Box(
        modifier = cropModifier.then(
            Modifier.pointerInput(Unit) {

                detectTransformGestures { _, pan, zoom, startZoom ->
                    if (zoom != 1f) {
                        val newWidth = cropRect.value.width / zoom
                        val newHeight = cropRect.value.height / zoom
                        val diffWidth = (cropRect.value.width - newWidth) / 2
                        val diffHeight = (cropRect.value.height - newHeight) / 2

                        val newLeft = cropRect.value.left + diffWidth
                        val newTop = cropRect.value.top + diffHeight
                        val newRight = cropRect.value.right - diffWidth
                        val newBottom = cropRect.value.bottom - diffHeight

                        cropRect.value = Rect(newLeft, newTop, newRight, newBottom)
                    } else if (pan != Offset(0f, 0f)) {
                        cropRect.value = calculateNewCropRect(cropRect.value, pan - startPos, 1f)
                    }

                    if (pan != Offset(0f, 0f) || zoom != 1f) {
                        startPos = pan
                    }
                }
            }
        )
    ) {
        // Draw the cropping overlay here (e.g., grid lines)
    }
}

fun calculateNewCropRect(
    cropRect: Rect,
    pan: Offset,
    zoom: Float
): Rect {
    val newLeft = (cropRect.left - pan.x) / zoom
    val newTop = (cropRect.top - pan.y) / zoom
    val newRight = (cropRect.right - pan.x) / zoom
    val newBottom = (cropRect.bottom - pan.y) / zoom

    return Rect(
        left = max(newLeft, 0f),
        top = max(newTop, 0f),
        right = min(newRight, 1f),
        bottom = min(newBottom, 1f)
    )
}

@Composable
@Preview
fun CropImagePreview() {
    val cropRect = remember { mutableStateOf(Rect(0.2f, 0.2f, 0.8f, 0.8f)) }
    // Provide your ImageBitmap here
    val imageBitmap = ImageBitmap(100, 100)

    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_kiminonawa)
    CropImage(
        bitmap = bitmap.asImageBitmap(),
        cropRect = cropRect
    )
}