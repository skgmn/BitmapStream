package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class ScaleToBitmapStream(
    other: BitmapStream,
    override val width: Int,
    override val height: Int
) : DelegateBitmapStream(other) {
    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (this.width == width && this.height == height) {
            this
        } else {
            other.scaleTo(width, height)
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (this.width == width) {
            this
        } else {
            other.scaleTo(width, AspectRatioCalculator.getHeight(this.width, this.height, width))
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (this.height == height) {
            this
        } else {
            other.scaleTo(AspectRatioCalculator.getWidth(this.width, this.height, height), height)
        }
    }

    override fun buildInputParameters(regional: Boolean): InputParameters {
        return other.buildInputParameters(regional).apply {
            scaleX *= width.toFloat() / other.width
            scaleY *= height.toFloat() / other.height
        }
    }
}