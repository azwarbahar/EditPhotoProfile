package com.azwar.editphotoprofile

import android.R.attr.translationX
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import kotlin.math.roundToInt


class LagiCoba {
}

@Preview
@Composable
fun CroppingApp() {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var circleCenter by remember { mutableStateOf(Offset(0f, 0f)) }
    var circleRadius by remember { mutableStateOf(0f) }
    var croppedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val context = LocalContext.current
    val imageBitmap = BitmapFactory.decodeResource(context.resources,
        R.drawable.ss
    ).asImageBitmap()
//        painterResource(id = R.drawable.img_yourname).toBitmap().asImageBitmap()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .layout { measurable, constraints ->
                val size = constraints.maxWidth
                val placeable = measurable.measure(constraints)
                circleRadius = size / 2f
                circleCenter = Offset(placeable.width / 2f, placeable.height / 2f)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }
    ) {
        if (imageBitmap != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale *= zoom
                            offset = if (scale > 1f) {
                                Offset(
                                    (offset.x + pan.x * scale).coerceIn(
                                        -imageBitmap.width * (scale - 1) / 2,
                                        imageBitmap.width * (scale - 1) / 2
                                    ),
                                    (offset.y + pan.y * scale).coerceIn(
                                        -imageBitmap.height * (scale - 1) / 2,
                                        imageBitmap.height * (scale - 1) / 2
                                    )
                                )
                            } else {
                                Offset(0f, 0f)
                            }
                        }
                    }
            ) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .offset { IntOffset(circleCenter.x.roundToInt(), circleCenter.y.roundToInt()) }
                .clip(CircleShape)
        ) {
            drawRect(Color.Black.copy(alpha = 0.5f))
            drawCircle(Color.Transparent, radius = circleRadius)
        }

        Button(
            onClick = {
                croppedBitmap = Bitmap.createBitmap(
                    imageBitmap.asAndroidBitmap(),
                    (circleCenter.x - circleRadius).toInt(),
                    (circleCenter.y - circleRadius).toInt(),
                    (circleRadius * 2).toInt(),
                    (circleRadius * 2).toInt()
                ).asImageBitmap()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Crop", fontSize = 18.sp)
        }

        croppedBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }
    }
}



@Composable
fun cropToSquare(imageBitmap: ImageBitmap, scaleFactor: Float): ImageBitmap {
    val width = imageBitmap.width
    val height = imageBitmap.height
    val size = (width.coerceAtMost(height) * scaleFactor).roundToInt()

    return with(LocalDensity.current) {
        val outputBitmap = createBitmap(size, size)

        outputBitmap.applyCanvas {
            drawBitmap(
                imageBitmap.asAndroidBitmap(),
                ((width - size) / 2).toFloat(),
                ((height - size) / 2).toFloat(),
                null
            )
        }

        outputBitmap.asImageBitmap()
    }
}

@Composable
fun CropImageWithZoomAndPan(bitmap: Bitmap) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val density = LocalDensity.current.density

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                scale *= zoom
                offsetX += pan.x * scale / density
                offsetY += pan.y * scale / density

            }
        }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.None,
            alignment = Alignment.Center
        )
    }
}
