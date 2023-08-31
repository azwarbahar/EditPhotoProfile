package com.azwar.editphotoprofile.cropper.draw

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

@Composable
fun DrawRectangleHole() {
    Canvas(modifier = Modifier.fillMaxSize(), onDraw = {

        val darkSurfaceWidthPx = drawContext.size.width.toInt()
        val darkSurfaceHeightPx = drawContext.size.height.toInt()

        drawIntoCanvas { blackOverlayCanvas ->

            val bitmap = Bitmap.createBitmap(
                darkSurfaceWidthPx,
                darkSurfaceHeightPx,
                Bitmap.Config.ARGB_8888
            ).apply { eraseColor(Color.TRANSPARENT) }
            val canvasBitmap = Canvas(bitmap)
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

            canvasBitmap.drawColor(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f).toArgb())
            canvasBitmap.drawCircle(centerX, centerY, radius, eraser)

            blackOverlayCanvas.nativeCanvas.drawBitmap(bitmap, 0F, 0F, null)
        }
    })
}