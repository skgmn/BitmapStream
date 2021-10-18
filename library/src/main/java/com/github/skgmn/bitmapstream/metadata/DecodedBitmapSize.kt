package com.github.skgmn.bitmapstream.metadata

import android.graphics.BitmapFactory
import kotlin.math.roundToInt

internal open class DecodedBitmapSize(
    options: BitmapFactory.Options
) : BitmapSize {
    private val scaleFactor =
        if (options.inScaled && options.inDensity != 0 && options.inTargetDensity != 0) {
            options.inTargetDensity.toFloat() / options.inDensity
        } else {
            1f
        }

    override val width = (options.outWidth * scaleFactor).roundToInt()
    override val height = (options.outHeight * scaleFactor).roundToInt()
}