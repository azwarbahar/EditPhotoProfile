package com.azwar.editphotoprofile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.azwar.editphotoprofile.ui.theme.EditPhotoProfileTheme

@Preview
@Composable
fun CobaLagi() {
//
//    val uri_image = "https://picsum.photos/200/300"
//
//    val imageBitmap = painterResource(id = R.drawable.img_yourname)
//    val cropRatio = 1f
//
//    var zoomState by remember { mutableStateOf(1f) }
//    var offset by remember { mutableStateOf(0f to 0f) }

    val offset = remember { mutableStateOf(Offset(0f, 0f)) }

    val context = LocalContext.current

    LaunchedEffect(offset.value) {
        Log.e("TAG", "CobaLagi: x = ${offset.value.x} and y = ${offset.value.y}")
//        Toast.makeText(
//            context,
//            "x = ${offset.value.x} and y = ${offset.value.y}",
//            Toast.LENGTH_SHORT
//        ).show()
    }

    var showHasil by remember { mutableStateOf(false) }
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_kiminonawa)
    var bitmapNew: Bitmap

    EditPhotoProfileTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {

                ZoomableImage(
                    imageBitmap = bitmap.asImageBitmap(),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null,
//                    offset = offset,
                    context = context,
                    onOffsetChange = { offset.value = it }
                )

//                ZoomableBox {
//                    Image(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .graphicsLayer(
//                                scaleX = scale,
//                                scaleY = scale,
//                                translationX = offsetX,
//                                translationY = offsetY
//                            ),
//                        bitmap = bitmap.asImageBitmap(),
//                        contentDescription = null
//                    )
//                }

                SurfaceWithRectangleHole()

                val widthDp = Dp(LocalConfiguration.current.screenWidthDp.toFloat())
                val ktpViewportWidthDp = widthDp - 32.dp
                val ktpViewportHeightDp = ktpViewportWidthDp / 1f
                val strokeSizeDp = 4.dp
                val strokeCoverPadding = strokeSizeDp + strokeSizeDp
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                onClick = {
                    Log.e("TAG", "CobaLagi: $bitmap")
                    cropPictureToViewport(bitmap) {
                        bitmapNew = it
                        showHasil = true
                    }
                }
            ) {
                Text(text = "Simpan")
            }
        }

        if (showHasil) {
            Hasil(bitmap = bitmap)
        }
    }
}

@Composable
fun SurfaceWithRectangleHole() {

    val ktpMarginHorizontalPx = 16.dp.dpToPx().toInt()

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize(), onDraw = {

        val darkSurfaceWidthPx = drawContext.size.width.toInt()
        val darkSurfaceHeightPx = drawContext.size.height.toInt()
        val ktpViewportWidthPx = darkSurfaceWidthPx - 2
        val ktpViewportHeightPx = (ktpViewportWidthPx / 1f / 1f).toInt()
        val ktpMarginVerticalPx = (darkSurfaceHeightPx - ktpViewportHeightPx) / 2

        drawIntoCanvas { blackOverlayCanvas ->

            val bitmap = Bitmap.createBitmap(
                darkSurfaceWidthPx,
                darkSurfaceHeightPx,
                Bitmap.Config.ARGB_8888
            ).apply { eraseColor(Color.TRANSPARENT) }
            val canvasBitmap = android.graphics.Canvas(bitmap)
            val eraser = Paint().apply {
                color = Color.TRANSPARENT
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }

            val screenWidth = canvasBitmap.width.toFloat()
            val screenHeight = canvasBitmap.height.toFloat()

            val radius = if (screenWidth < screenHeight) {
                screenWidth / 2
            } else {
                screenHeight / 2
            }
            val centerX = screenWidth / 2
            val centerY = screenHeight / 2

            canvasBitmap.drawColor(Black.copy(alpha = 0.75f).toArgb())
            canvasBitmap.drawCircle(centerX, centerY, radius, eraser)

            blackOverlayCanvas.nativeCanvas.drawBitmap(bitmap, 0F, 0F, null)
        }
    })
}

//fun cropPictureToViewport(
//    bitmap: Bitmap,
//    ktpMarginHorizontalPx: Int,
//    darkSurfaceWidthPx: Int,
//    onPictureCropped: (Bitmap) -> Unit,
//) {
//    val fileToPreviewRatio = bitmap.width.toDouble() / darkSurfaceWidthPx
//
//    val ktpFileViewportWidthPx = bitmap.width - 2 * ktpMarginHorizontalPx * fileToPreviewRatio
//    val ktpFileViewportHeightPx = ktpFileViewportWidthPx / 1f / 1f
//    val croppedBitmap = Bitmap.createBitmap(
//        bitmap,
//        (ktpMarginHorizontalPx * fileToPreviewRatio).toInt(),
//        ((bitmap.height - ktpFileViewportHeightPx) / 2).toInt(),
//        ktpFileViewportWidthPx.toInt(),
//        ktpFileViewportHeightPx.toInt()
//    )

//    if (croppedBitmap.width > IMAGE_WIDTH_FOR_API) {
//        val scaleDownRatio = IMAGE_WIDTH_FOR_API.toFloat() / croppedBitmap.width.toFloat()
//        val matrix = Matrix().apply { postScale(scaleDownRatio, scaleDownRatio) }
//        val resizedBitmap = Bitmap.createBitmap(
//            croppedBitmap, ZERO, ZERO, croppedBitmap.width, croppedBitmap.height, matrix, false
//        )
//        croppedBitmap.recycle()
//        onPictureCropped(resizedBitmap)
//    } else onPictureCropped(croppedBitmap)
//}
