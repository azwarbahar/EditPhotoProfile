package com.azwar.editphotoprofile

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.azwar.editphotoprofile.ui.theme.EditPhotoProfileTheme
import java.io.ByteArrayOutputStream


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditPhotoProfileTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {

    // State to keep track of the scale factor and translation
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 2f) // Adjust max zoom here
                    val newOffset = if (newScale == 1f) Offset(0f, 0f) else offset

                    scale = newScale
                    offset = newOffset

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
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
    ) {
        // Load your image here using the Image composable
        Image(
            painterResource(id = R.drawable.img_azwar),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            contentDescription = null // Add proper description
        )

    }
}


@Composable
fun Hasil(bitmap: Bitmap) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onClick = { }) {
            Text(text = "OK")
        }
    }
}

@Composable
fun Home() {

    var offset: Offset
    val context = LocalContext.current
    var showHasil by remember { mutableStateOf(false) }
    EditPhotoProfileTheme {
        Column(modifier = Modifier.fillMaxSize()) {
           val box = Box(modifier = Modifier.weight(1f)) {
//                CropScreen()
//                ZoomableImage(
//                    painter = painterResource(id = R.drawable.img_yourname),
//                    modifier = Modifier.fillMaxSize(),
//                    contentDescription = null,
//                    onTap = {
//                        offset = it
//                    }
//                )
                Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
                    val circlePath = Path().apply {
                        addOval(Rect(center, size.minDimension / 2))
                    }
                    clipPath(circlePath, clipOp = ClipOp.Difference) {
                        drawRect(SolidColor(Black.copy(alpha = 0.7f)))
                    }
                })
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                onClick = {
                    val icon = BitmapFactory.decodeResource(context.resources,
                        R.drawable.img_yourname
                    )
//                    val bmp = BitmapDrawable(R.drawable.img_yourname)

//                    val bmp = Bitmap.createBitmap(R.drawable.img_yourname)
//                    cropImage(icon, offset, offset)
                }) {
                Text(text = "Simpan")
            }
        }
    }

    if (showHasil) {
//        Hasil()
    }
}

private fun cropImage(bitmap: Bitmap, frame: View, reference: View): ByteArray {
    val heightOriginal = frame.height
    val widthOriginal = frame.width
    val heightFrame = reference.height
    val widthFrame = reference.width
    val leftFrame = reference.left
    val topFrame = reference.top
    val heightReal = bitmap.height
    val widthReal = bitmap.width
    val widthFinal = widthFrame * widthReal / widthOriginal
    val heightFinal = heightFrame * heightReal / heightOriginal
    val leftFinal = leftFrame * widthReal / widthOriginal
    val topFinal = topFrame * heightReal / heightOriginal
    val bitmapFinal = Bitmap.createBitmap(
        bitmap,
        leftFinal, topFinal, widthFinal, heightFinal
    )
    val stream = ByteArrayOutputStream()
    bitmapFinal.compress(
        Bitmap.CompressFormat.JPEG,
        100,
        stream
    ) //100 is the best quality possibe
    return stream.toByteArray()
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_yourname)
//    Home()
    CobaLagi()
}

fun cropPictureToViewport(
    bitmap : Bitmap,
//    ktpMarginHorizontalPx: Int,
//    darkSurfaceWidthPx: Int,
    onPictureCropped: (Bitmap) -> Unit,
) {
//    val bitmap = imageProxy.toBitmap().rotate(Constants.Rotation.ROTATE_90_DEGREE)
    val fileToPreviewRatio = bitmap.width.toDouble()
    Log.e("TAG", "cropPictureToViewport: $fileToPreviewRatio", )

    val ktpFileViewportWidthPx = bitmap.width - 2
    val ktpFileViewportHeightPx = ktpFileViewportWidthPx / 1f / 1f
//    val croppedBitmap = Bitmap.createBitmap(
//        bitmap,
//        fileToPreviewRatio.toInt(),
//        ((bitmap.height - ktpFileViewportHeightPx) / 2).toInt(),
//        ktpFileViewportWidthPx,
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
//    } else
//        onPictureCropped(croppedBitmap)
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

