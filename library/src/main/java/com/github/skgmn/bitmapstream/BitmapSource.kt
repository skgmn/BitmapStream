package com.github.skgmn.bitmapstream

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

internal abstract class BitmapSource {
    open val manualDensityScalingForRegional: Boolean
        get() = false

    abstract fun decodeBitmap(options: BitmapFactory.Options): Bitmap?
    abstract fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap?
    open fun createNewDecodingState(): DecodingState? = null
}