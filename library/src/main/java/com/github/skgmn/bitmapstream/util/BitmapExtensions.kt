package com.github.skgmn.bitmapstream.util

import android.graphics.Bitmap
import android.graphics.Matrix

internal fun Bitmap.mutable(mutable: Boolean?): Bitmap {
    return if (mutable == null || this.isMutable == mutable) {
        this
    } else {
        copy(config, mutable).also { recycle() }
    }
}

internal fun Bitmap.hardware(): Bitmap {
    return if (config == Bitmap.Config.HARDWARE) {
        this
    } else {
        copy(Bitmap.Config.HARDWARE, false).also { recycle() }
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