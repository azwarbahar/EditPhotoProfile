package com.azwar.editphotoprofile.cropper

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.azwar.editphotoprofile.cropper.model.CropOutline
import com.azwar.editphotoprofile.cropper.model.CropShape

class CropAgent {
    private val imagePaint = Paint().apply {
        blendMode = BlendMode.SrcIn
    }
    private val paint = Paint()

    fun crop(
        imageBitmap: ImageBitmap,
        cropRect: Rect,
        cropOutline: CropOutline,
        layoutDirection: LayoutDirection,
        density: Density,
    ): ImageBitmap {
        val croppedBitmap: Bitmap = Bitmap.createBitmap(
            imageBitmap.asAndroidBitmap(),
            cropRect.left.toInt(),
            cropRect.top.toInt(),
            cropRect.width.toInt(),
            cropRect.height.toInt(),
        )

        val imageToCrop = croppedBitmap
            .copy(Bitmap.Config.ARGB_8888, true)!!
            .asImageBitmap()

        drawCroppedImage(cropOutline, cropRect, layoutDirection, density, imageToCrop)

        return imageToCrop
    }

    private fun drawCroppedImage(
        cropOutline: CropOutline,
        cropRect: Rect,
        layoutDirection: LayoutDirection,
        density: Density,
        imageToCrop: ImageBitmap,
    ) {

        when (cropOutline) {
            is CropShape -> {
                val path = Path().apply {
                    val outline =
                        cropOutline.shape.createOutline(cropRect.size, layoutDirection, density)
                    addOutline(outline)
                }
                Canvas(image = imageToCrop).run {
                    saveLayer(nativeCanvas.clipBounds.toComposeRect(), imagePaint)
                    drawPath(path, paint)
                    drawImage(imageToCrop, Offset.Zero, imagePaint)
                    restore()
                }
            }
        }
    }

    fun resize(
        croppedImageBitmap: ImageBitmap,
        requiredWidth: Int,
        requiredHeight: Int
    ): ImageBitmap {
        val resizedBitmap: Bitmap = Bitmap.createScaledBitmap(
            croppedImageBitmap.asAndroidBitmap(),
            requiredWidth,
            requiredHeight,
            true
        )
        return resizedBitmap.asImageBitmap()
    }
}