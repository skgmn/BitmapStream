package com.github.skgmn.bitmapstream.util

import android.graphics.Bitmap

internal fun Bitmap.mutable(mutable: Boolean?): Bitmap {
    return if (mutable == null || this.isMutable == mutable) {
        this
    } else {
        copy(config, mutable)
    }
}