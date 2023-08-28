package com.azwar.editphotoprofile.cropper.draw

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendMode.Companion.SrcOut
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.azwar.editphotoprofile.cropper.model.CropOutline
import com.azwar.editphotoprofile.cropper.model.CropShape
import com.azwar.editphotoprofile.cropper.util.drawWithLayer

@Composable
internal fun DrawingOverlay(
    modifier: Modifier,
    rect: Rect,
    cropOutline: CropOutline,
) {
    val density = LocalDensity.current
    val layoutDirection: LayoutDirection = LocalLayoutDirection.current

    when (cropOutline) {
        is CropShape -> {
            val outline = remember(rect, cropOutline) {
                cropOutline.shape.createOutline(rect.size, layoutDirection, density)
            }
            DrawingOverlayImpl(
                modifier = modifier,
                rect = rect,
                outline = outline
            )
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    rect: Rect,
    outline: Outline
) {
    Canvas(modifier = modifier) {
        drawOverlay(rect) { drawCropOutline(outline = outline) }
    }
}

private fun DrawScope.drawOverlay(
    rect: Rect,
    drawBlock: DrawScope.() -> Unit
) {
    drawWithLayer {
        drawRect(Color.Transparent)
        translate(left = rect.left, top = rect.top) { drawBlock() }
    }
}

private fun DrawScope.drawCropOutline(
    outline: Outline,
    blendMode: BlendMode = SrcOut
) {
    drawOutline(
        outline = outline,
        color = Color.Transparent,
        blendMode = blendMode
    )
}
