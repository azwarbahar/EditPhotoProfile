package com.azwar.editphotoprofile.cropper

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
import com.azwar.editphotoprofile.cropper.model.CropData
import com.azwar.editphotoprofile.cropper.state.CropState
import com.azwar.editphotoprofile.cropper.state.cropData
import com.azwar.editphotoprofile.cropper.util.ZoomLevel
import com.azwar.editphotoprofile.cropper.util.getNextZoomLevel
import com.azwar.editphotoprofile.cropper.util.update
import kotlinx.coroutines.launch

fun Modifier.crop(
    vararg keys: Any?,
    cropState: CropState,
    zoomOnDoubleTap: (ZoomLevel) -> Float = cropState.DefaultOnDoubleTap,
    onDown: ((CropData) -> Unit)? = null,
    onMove: ((CropData) -> Unit)? = null,
    onUp: ((CropData) -> Unit)? = null,
    onGestureStart: ((CropData) -> Unit)? = null,
    onGesture: ((CropData) -> Unit)? = null,
    onGestureEnd: ((CropData) -> Unit)? = null
) = composed(

    factory = {

        LaunchedEffect(key1 = cropState){
            cropState.init()
        }

        val coroutineScope = rememberCoroutineScope()

        // Current Zoom level
        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        val transformModifier = Modifier.pointerInput(*keys) {
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    onGestureStart?.invoke(cropState.cropData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        cropState.onGestureEnd {
                            onGestureEnd?.invoke(cropState.cropData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    coroutineScope.launch {
                        cropState.onGesture(
                            centroid = centroid,
                            panChange = pan,
                            zoomChange = zoom,
                            rotationChange = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }
                    onGesture?.invoke(cropState.cropData)
                    mainPointer.consume()
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            detectTapGestures(
                onDoubleTap = { offset: Offset ->
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        cropState.onDoubleTap(
                            offset = offset,
                            zoom = newZoom
                        ) {
                            onGestureEnd?.invoke(cropState.cropData)
                        }
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(cropState)
        }

        this.then(
            clipToBounds()
                .then(tapModifier)
                .then(transformModifier)
                .then(graphicsModifier)
        )
    },
    inspectorInfo = debugInspectorInfo {
        name = "crop"
        // add name and value of each argument
        properties["keys"] = keys
        properties["onDown"] = onGestureStart
        properties["onMove"] = onGesture
        properties["onUp"] = onGestureEnd
    }
)

internal val CropState.DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f
            ZoomLevel.Mid -> 3f.coerceIn(zoomMin, zoomMax)
            ZoomLevel.Max -> 5f.coerceAtLeast(zoomMax)
        }
    }
