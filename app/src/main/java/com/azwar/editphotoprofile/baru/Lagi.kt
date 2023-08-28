package com.azwar.editphotoprofile.baru

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.azwar.editphotoprofile.R
import kotlin.math.roundToInt

@Composable
fun Lagi() {

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset = (offset * scale) + pan * scale
                }
            }
    ) {
        CropImage(
            imageModifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }

}

@Composable
fun CropImage(imageModifier: Modifier = Modifier) {
    // Load your image here
//    val image = // Load your image resource

    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_kiminonawa)
        Layout(
            modifier = imageModifier,
            content = {
                // Draw the image with proper scaling and translation
                // You can use Image() composable here to load and display the image
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        ) { measurables, constraints ->
            // Get the measured size of the image
            val imageSize = IntSize(bitmap.width, bitmap.height) // Replace with the actual image size

            // Calculate the available space for the cropped image
            val availableWidth = constraints.maxWidth
            val availableHeight = constraints.maxHeight

            // Calculate the aspect ratio of the cropped area (1:1)
            val aspectRatio = 1f

            // Calculate the size of the cropped area
            val cropWidth = minOf(availableWidth, (availableHeight * aspectRatio).roundToInt())
            val cropHeight = minOf(availableHeight, (availableWidth / aspectRatio).roundToInt())

            // Calculate the position to center the cropped area within the available space
            val cropLeft = (availableWidth - cropWidth) / 2
            val cropTop = (availableHeight - cropHeight) / 2

            // Set the position and size of the cropped image
            layout(cropWidth, cropHeight) {
                // Place the cropped image at the desired position and size within the layout
                // You can use drawImage() or other composable to draw the cropped image
                measurables.forEach { measurable ->
                    val placeable = measurable.measure(constraints)
                    val xPosition = cropLeft
                    val yPosition = cropTop

                    // Position the image within the cropped area
                    placeable.placeRelative(x = xPosition, y = yPosition)
                }
            }
        }
}

@Preview
@Composable
fun PreviewCropImageScreen() {
    Lagi()
}