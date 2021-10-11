package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

internal object DrawableUtils {
    // Because Drawable.getOpacity() has been deprecated
    fun isOpaque(d: Drawable): Boolean {
        if (d.colorFilter != null || d.alpha != 0xff) return false
        return when(d) {
            is ColorDrawable -> true
            is BitmapDrawable -> !d.bitmap.hasAlpha()
            else -> false
        }
    }
}