package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class ScaleToBitmapStream(
    other: BitmapStream,
    private val width: Int,
    private val height: Int
) : DelegateBitmapStream(other) {
    override val metadata = object : BitmapMetadata {
        override val width: Int get() = this@ScaleToBitmapStream.width
        override val height: Int get() = this@ScaleToBitmapStream.height
        override val mimeType: String? get() = other.metadata.mimeType
        override val densityScale: Float get() = other.metadata.densityScale
    }

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
            scaleX *= width.toFloat() / other.metadata.width
            scaleY *= height.toFloat() / other.metadata.height
        }
    }
}