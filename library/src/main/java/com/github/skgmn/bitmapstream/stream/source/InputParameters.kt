package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapSize
import kotlin.math.roundToInt

internal class InputParameters(
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    var region: Rect? = null,
    var mutable: Boolean? = null,
    var hardware: Boolean = false,
    var downsampleOnly: Boolean = false,
    var targetDensity: Int = 0,
) {
    fun buildDecodingParameters(size: BitmapSize): DecodingParameters {
        val options = BitmapFactory.Options()
        options.inScaled = false
        options.inTargetDensity = targetDensity

        var sampleSize = 1
        var sx = scaleX
        var sy = scaleY
        while (sx <= 0.5f && sy <= 0.5f) {
            sampleSize *= 2
            sx *= 2f
            sy *= 2f
        }
        options.inSampleSize = sampleSize

        var postScaleX = if (downsampleOnly) 1f else sx
        var postScaleY = if (downsampleOnly) 1f else sy
        if (sampleSize != 1 && postScaleX != 1f && postScaleY != 1f) {
            val w = size.width.toFloat()
            val h = size.height.toFloat()
            val downsampledWidth = (w / sampleSize).roundToInt()
            val downsampledHeight = (h / sampleSize).roundToInt()
            postScaleX = (w * scaleX).roundToInt() / downsampledWidth.toFloat()
            postScaleY = (h * scaleY).roundToInt() / downsampledHeight.toFloat()
        }

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