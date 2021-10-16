package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

internal class InputParameters(
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    var region: Rect? = null,
    var mutable: Boolean? = null,
    var hardware: Boolean = false,
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

        val postScaleX = if (downsampleOnly) 1f else sx
        val postScaleY = if (downsampleOnly) 1f else sy

        if (postScaleX == 1f && postScaleY == 1f) {
            mutable?.let { options.inMutable = it }
            if (hardware) {
                options.inPreferredConfig = Bitmap.Config.HARDWARE
            }
        }

        return DecodingParameters(
            options = options,
            postScaleX = postScaleX,
            postScaleY = postScaleY,
            region = region
        )
    }
}