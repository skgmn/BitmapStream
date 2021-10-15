package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class ScaleWidthBitmapStream(
    other: SourceBitmapStream,
    private val targetWidth: Double,
    private val heightScale: Float
) : ScaleBitmapStream(other) {
    override val scaleX by lazy(LazyThreadSafetyMode.NONE) {
        (targetWidth / other.exactWidth).toFloat()
    }
    override val scaleY by lazy(LazyThreadSafetyMode.NONE) { scaleX * heightScale }
    override val exactWidth get() = targetWidth
    override val exactHeight by lazy(LazyThreadSafetyMode.NONE) { other.exactHeight * scaleY }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return when {
            this.targetWidth == width.toDouble() -> this
            heightScale == 1f -> other.scaleWidth(width)
            else -> ScaleWidthBitmapStream(other, width.toDouble(), heightScale)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (heightScale == 1f) {
            other.scaleHeight(height)
        } else {
            super.scaleHeight(height)
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            ScaleWidthBitmapStream(other, targetWidth * scaleWidth, heightScale * scaleHeight)
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return ScaleWidthBitmapStream(new, targetWidth, heightScale)
    }
}