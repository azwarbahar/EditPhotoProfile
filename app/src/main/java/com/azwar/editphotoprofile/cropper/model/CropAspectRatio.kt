package com.azwar.editphotoprofile.cropper.model

import androidx.compose.runtime.Immutable

@Immutable
data class AspectRatio(val value: Float) {
    companion object {
        val Original = AspectRatio(-1f)
    }
}