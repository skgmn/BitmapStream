package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class ScaleWidthBitmapStream(
    other: BitmapStream,
    private val width: Int
) : DelegateBitmapStream(other) {
    override val metadata = object : BitmapMetadata {
        override val width: Int get() = this@ScaleWidthBitmapStream.width
        override val height by lazy {
            AspectRatioCalculator.getHeight(other.metadata.width, other.metadata.height, width)
        }
        override val mimeType: String? get() = other.metadata.mimeType
        override val densityScale: Float get() = other.metadata.densityScale
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
        val scale = width.toFloat() / other.metadata.width
        return other.buildInputParameters(regional).apply {
            scaleX *= scale
            scaleY *= scale
        }
    }
}