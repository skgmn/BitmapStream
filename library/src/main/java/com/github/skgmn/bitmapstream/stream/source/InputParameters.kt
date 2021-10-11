package com.github.skgmn.bitmapstream.stream.source

import android.graphics.BitmapFactory
import android.graphics.Rect

internal class InputParameters(
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    var region: Rect? = null,
    var mutable: Boolean = false,
    var downsampleOnly: Boolean = false
) {
    fun buildDecodingParameters(): DecodingParameters {
        val options = BitmapFactory.Options()

        var sampleSize = 1
        var sx = scaleX
        var sy = scaleY
        while (sx <= 0.5f && sy <= 0.5f) {
            sampleSize *= 2
            sx *= 2f
            sy *= 2f
        }
        options.inSampleSize = sampleSize

        options.inMutable = mutable

        return DecodingParameters(
            options = options,
            postScaleX = if (downsampleOnly) 1f else sx,
            postScaleY = if (downsampleOnly) 1f else sy,
            region = region
        )
    }
}