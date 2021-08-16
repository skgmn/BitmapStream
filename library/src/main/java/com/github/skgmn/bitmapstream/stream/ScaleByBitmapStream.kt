package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import kotlin.math.roundToInt

internal class ScaleByBitmapStream(
    other: BitmapStream,
    private val scaleX: Float,
    private val scaleY: Float
) : DelegateBitmapStream(other) {
    override val width: Int by lazy {
        (other.width * scaleX).roundToInt()
    }
    override val height: Int by lazy {
        (other.height * scaleY).roundToInt()
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            val sx = this.scaleX * scaleWidth
            val sy = this.scaleY * scaleHeight
            if (sx == 1f && sy == 1f) {
                other
            } else {
                other.scaleBy(sx, sy)
            }
        }
    }

    override fun buildInputParameters(regional: Boolean): InputParameters {
        return other.buildInputParameters(regional).apply {
            scaleX *= this@ScaleByBitmapStream.scaleX
            scaleY *= this@ScaleByBitmapStream.scaleY
        }
    }
}