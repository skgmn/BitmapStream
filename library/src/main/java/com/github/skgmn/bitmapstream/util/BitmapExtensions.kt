package com.github.skgmn.bitmapstream.util

import android.graphics.Bitmap
import android.graphics.Matrix

internal fun Bitmap.characteristic(hardware: Boolean?, mutable: Boolean?): Bitmap {
    val targetConfig = when (hardware) {
        true -> Bitmap.Config.HARDWARE
        false -> if (config == Bitmap.Config.HARDWARE) {
            Bitmap.Config.ARGB_8888
        } else {
            config
        }
        else -> config
    }
    val targetMutable = mutable ?: if (hardware == true) false else isMutable
    return if (config == targetConfig && isMutable == targetMutable) {
        this
    } else {
        copy(targetConfig, targetMutable).also { recycle() }
    }
}

internal fun Bitmap.scaleBy(scaleX: Float, scaleY: Float): Bitmap {
    return if (scaleX == 1f && scaleY == 1f) {
        this
    } else {
        val m = Matrix()
        m.setScale(scaleX, scaleY)
        return Bitmap.createBitmap(this, 0, 0, width, height, m, true).also {
            if (this !== it) recycle()
        }
    }
}