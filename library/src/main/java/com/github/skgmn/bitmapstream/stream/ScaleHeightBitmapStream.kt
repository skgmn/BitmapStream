package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class ScaleHeightBitmapStream(
    other: BitmapStream,
    private val height: Int
) : DelegateBitmapStream(other) {
    override val metadata = object : BitmapMetadata {
        override val width by lazy {
            AspectRatioCalculator.getWidth(other.metadata.width, other.metadata.height, height)
        }
        override val height: Int get() = this@ScaleHeightBitmapStream.height
        override val mimeType: String? get() = other.metadata.mimeType
        override val densityScale: Float get() = other.metadata.densityScale
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
        val scale = height.toFloat() / other.metadata.height
        return other.buildInputParameters(regional).apply {
            scaleX *= scale
            scaleY *= scale
        }
    }
}