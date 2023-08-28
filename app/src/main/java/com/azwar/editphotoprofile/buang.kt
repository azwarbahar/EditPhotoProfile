package com.azwar.editphotoprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize

class buang {
}

@Composable
fun SurfaceWithCircleHole(
    circleSize: Dp,
    imageSize: IntSize,
    imageOffset: Size
) {
    // Calculate circle position within the image based on the offset
    val circlePosition = Offset(
        x = imageSize.width / 2f + imageOffset.width,
        y = imageSize.height / 2f + imageOffset.height
    )

//    // Calculate the rectangle for the circle hole
//    val circleRect = circlePosition.toRect() // Assuming you want a rectangle centered at circlePosition
//    val imageRect = Offset(0f, 0f).toRect().size(imageSize)
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // Draw the surface with hole
//        drawIntoCanvas { canvas ->
//            canvas.drawRoundRect(imageRect, 8.dp.toPx(), 8.dp.toPx(), ...)
//            canvas.drawRect(circleRect, ...)
//        }
//    }
}


//@Composable
//fun SurfaceWithRectangleHole(onCanvasDrawn: (canvasWidthPx: Int) -> Unit) {
//
//    val ktpMarginHorizontalPx = 16.dp.dpToPx().toInt()
//
//    val DARK_SURFACE_COLOR_ALPHA = 0.75f
//
//    Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
//
//        val darkSurfaceWidthPx = drawContext.size.width.toInt()
//        val darkSurfaceHeightPx = drawContext.size.height.toInt()
//        val ktpViewportWidthPx = darkSurfaceWidthPx - 2 * ktpMarginHorizontalPx
//        val ktpViewportHeightPx = (ktpViewportWidthPx / 800f / 600f).toInt()
//        val ktpMarginVerticalPx = (darkSurfaceHeightPx - ktpViewportHeightPx) / 2
//
//        onCanvasDrawn(darkSurfaceWidthPx)
//
//        drawIntoCanvas { blackOverlayCanvas ->
//
//            val bitmap = Bitmap.createBitmap(darkSurfaceWidthPx, darkSurfaceHeightPx, Bitmap.Config.ARGB_8888)
//                .apply { eraseColor(Color.TRANSPARENT) }
//            val canvasBitmap = Canvas(bitmap)
//            val eraser = Paint().apply {
//                color = Color.TRANSPARENT
//                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
//            }
//
//            canvasBitmap.drawColor(Black.copy(alpha = DARK_SURFACE_COLOR_ALPHA).toArgb())
//            canvasBitmap.drawRect(
//                ktpMarginHorizontalPx.toFloat(),
//                ktpMarginVerticalPx.toFloat(),
//                darkSurfaceWidthPx.toFloat() - ktpMarginHorizontalPx.toFloat(),
//                darkSurfaceHeightPx.toFloat() - ktpMarginVerticalPx.toFloat(),
//                eraser
//            )
//
//            blackOverlayCanvas.nativeCanvas.drawBitmap(bitmap, 1f, 0f, null)
//        }
//    })
//}




// Overlay with SurfaceWithCircleHole
//            SurfaceWithCircleHole(
//                circleSize = 100.dp, // Adjust size as needed
//                imageSize = IntSize(300, 300), // Size of the image
//                imageOffset = offset.toSize() // Current offset
//            )




@Composable
fun CropScreen() {
    val imageUrl = R.drawable.img_yourname
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        CenterCropImage(
            imageUrl = imageUrl,
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxSize()
        )
    }
}




@Composable
fun CenterCropImage(
    imageUrl: Int,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    val density = LocalDensity.current.density

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset = if (scale > 1f) {
                        val maxOffsetX = (scale - 1) * (size.width / 2)
                        val maxOffsetY = (scale - 1) * (size.height / 2)
                        Offset(
                            (offset.x + pan.x * density).coerceIn(-maxOffsetX, maxOffsetX),
                            (offset.y + pan.y * density).coerceIn(-maxOffsetY, maxOffsetY)
                        )
                    } else {
                        Offset(0f, 0f)
                    }
                }
            }
    ) {
        Image(
            painter = painterResource(id = imageUrl),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
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
