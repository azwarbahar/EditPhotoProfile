package com.azwar.editphotoprofile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Preview
@Composable
fun Detail() {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    val context = LocalContext.current
    val img_bitmap = BitmapFactory.decodeResource(
        context.getResources(),
        R.drawable.ss
    )


    val density = LocalDensity.current.density
    val maxScale = 4f
    val minScale = 1f

    val showImage = remember { mutableStateOf(false) }
    val croppedBitmap = remember { mutableStateOf<Bitmap>(img_bitmap) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 2.5f)
                    val newOffset = if (newScale == 1f) Offset(0f, 0f) else offset
                    scale *= zoom
                    scale = scale.coerceIn(minScale, maxScale)
                    if (newScale > 1f) {
                        val offsetX = (newOffset.x * zoom + pan.x * newScale).coerceIn(
                            -size.width * (newScale - 1),
                            size.width * (newScale - 1)
                        )
                        val offsetY = (newOffset.y * zoom + pan.y * newScale).coerceIn(
                            -size.height * (newScale - 1),
                            size.height * (newScale - 1)
                        )
                        offset = Offset(offsetX, offsetY)
                    }
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_yourname),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit
        )
        Button(
            onClick = {
                // Crop image here and return the result

                Log.e("TAG", "Detail: X = ${offset.x} dan Y ${offset.y}", )
//                croppedBitmap.value = cropImageBitmap(img_bitmap, offset, scale)
//                showImage.value = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(text = "Crop Image")
        }
        if (showImage.value){
        Image(
            bitmap = croppedBitmap.value.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        }
    }

}
private fun cropImageBitmap(
    source: Bitmap,
    offset: Offset,
    scale: Float
): Bitmap {
//    val imageSize = source


    // Calculate the visible portion of the image
    val visibleWidth = source.width / scale
    val visibleHeight = source.height / scale
    val left = (offset.x / scale).coerceIn(0f, source.width - visibleWidth)
    val top = (offset.y / scale).coerceIn(0f, source.height - visibleHeight)

    // Calculate the crop dimensions
    val cropLeft = (left * source.width / source.width).roundToInt()
    val cropTop = (top * source.height / source.height).roundToInt()
    val cropWidth = (visibleWidth * source.width / source.width).roundToInt()
    val cropHeight = (visibleHeight * source.height / source.height).roundToInt()

    // Create a cropped bitmap
    val croppedBitmap = Bitmap.createBitmap(source,cropLeft, cropTop , source.width, source.height)
    val workingBitmap: Bitmap = Bitmap.createBitmap(croppedBitmap)
    val mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = android.graphics.Canvas(mutableBitmap)
    val srcRect = android.graphics.Rect(cropLeft, cropTop, cropLeft + cropWidth, cropTop + cropHeight)
    val dstRect = android.graphics.Rect(0, 0, croppedBitmap.width, croppedBitmap.height)
    canvas.drawBitmap(source, srcRect, dstRect, null)
    return croppedBitmap
}