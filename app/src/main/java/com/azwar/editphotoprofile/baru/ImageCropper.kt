package com.azwar.editphotoprofile.baru
//
//import androidx.compose.foundation.layout.BoxWithConstraints
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clipToBounds
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.graphics.DefaultAlpha
//import androidx.compose.ui.graphics.FilterQuality
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.layout.ContentScale
//
//@Composable
//fun ImageCropper(
//    modifier: Modifier = Modifier,
//    imageBitmap: ImageBitmap,
//    contentDescription: String?,
////    cropStyle: CropStyle = CropDefaults.style(),
////    cropProperties: CropProperties = CropDefaults.properties(),
////    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
//    crop: Boolean = false,
//    onCropStart: () -> Unit,
//    onCropSuccess: (ImageBitmap) -> Unit
//){
//
//
//    ImageWithConstraints(
//        modifier = modifier.clipToBounds(),
//        contentScale = cropProperties.contentScale,
//        contentDescription = contentDescription,
//        filterQuality = filterQuality,
//        imageBitmap = imageBitmap,
//        drawImage = false
//    )
//
//}
//
//@Composable
//internal fun ImageWithConstraints(
//    modifier: Modifier = Modifier,
//    imageBitmap: ImageBitmap,
//    alignment: Alignment = Alignment.Center,
//    contentScale: ContentScale = ContentScale.Fit,
//    contentDescription: String? = null,
//    alpha: Float = DefaultAlpha,
//    colorFilter: ColorFilter? = null,
//    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
//    drawImage: Boolean = true,
//    content: @Composable ImageScope.() -> Unit = {}
//){
//    BoxWithConstraints(
//        modifier = modifier,
////            .then(semantics),
//        contentAlignment = alignment,
//    ){
//
//        val bitmapWidth = imageBitmap.width
//        val bitmapHeight = imageBitmap.height
//
//        val (boxWidth: Int, boxHeight: Int) = getParentSize(bitmapWidth, bitmapHeight)
//        val srcSize = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
//        val dstSize = Size(boxWidth.toFloat(), boxHeight.toFloat())
//
//        val scaleFactor = contentScale.computeScaleFactor(srcSize, dstSize)
//        val imageWidth = bitmapWidth * scaleFactor.scaleX
//        val imageHeight = bitmapHeight * scaleFactor.scaleY
//
//    }
//
//}