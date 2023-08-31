package com.azwar.editphotoprofile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azwar.editphotoprofile.cropper.ImageCropper
import com.azwar.editphotoprofile.cropper.draw.DrawRectangleHole
import com.azwar.editphotoprofile.cropper.model.CropDefaults
import com.azwar.editphotoprofile.cropper.model.CropOutlineProperty
import com.azwar.editphotoprofile.cropper.model.OutlineType
import com.azwar.editphotoprofile.cropper.model.RectCropShape
import com.azwar.editphotoprofile.ui.theme.EditPhotoProfileTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditPhotoProfileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImageCropDemoSimple()
                }
            }
        }
    }
}

@Composable
fun ImageCropDemoSimple() {
    val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }
    val cropProperties by remember {
        mutableStateOf(
            CropDefaults.properties(
                cropOutlineProperty = CropOutlineProperty(
                    OutlineType.Rect,
                    RectCropShape(0, "Rect")
                ),
                handleSize = handleSize
            )
        )
    }
    val imageBitmapLarge = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable.img_yourname
    )

    val imageBitmap by remember { mutableStateOf(imageBitmapLarge) }
    var crop by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var isCropping by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier.fillMaxSize()) {

                ImageCropper(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    imageBitmap = imageBitmap,
                    contentDescription = "Image Cropper",
                    cropProperties = cropProperties,
                    crop = crop,
                    onCropStart = {
                        isCropping = true
                    },
                    onCropSuccess = {
                        croppedImage = it
                        isCropping = false
                        crop = false
                        showDialog = true
                    },
                )
            }

            DrawRectangleHole()

            Button(onClick = { crop = true }) {
                Text(text = "Potong")
            }

            if (showDialog) {
                croppedImage?.let {
                    ShowCroppedImageDialog(imageBitmap = it) {
                        showDialog = !showDialog
                        croppedImage = null
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowCroppedImageDialog(imageBitmap: ImageBitmap, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit,
                bitmap = imageBitmap,
                contentDescription = "result"
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
