package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class ScaleHeightBitmapStream(
    other: BitmapStream,
    override val height: Int
) : DelegateBitmapStream(other) {
    override val width: Int by lazy {
        AspectRatioCalculator.getWidth(other.width, other.height, height)
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return other.scaleWidth(width)
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (this.height == height) {
            this
        } else {
            other.scaleHeight(height)
        }
    }

    override fun buildInputParameters(regional: Boolean): InputParameters {
        val scale = height.toFloat() / other.height
        return other.buildInputParameters(regional).apply {
            scaleX *= scale
            scaleY *= scale
        }
    }
}