package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class ScaleWidthBitmapStream(
    other: BitmapStream,
    override val width: Int
) : DelegateBitmapStream(other) {
    override val height: Int by lazy {
        AspectRatioCalculator.getHeight(other.width, other.height, width)
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (this.width == width) {
            this
        } else {
            other.scaleWidth(width)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return other.scaleHeight(height)
    }

    override fun buildInputParameters(regional: Boolean): InputParameters {
        val scale = width.toFloat() / other.width
        return other.buildInputParameters(regional).apply {
            scaleX *= scale
            scaleY *= scale
        }
    }
}