package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream

internal class ScaleWidthBitmapStream(
    other: BitmapStream,
    private val targetWidth: Double,
    private val heightScale: Float
) : ScaleBitmapStream(other) {
    override val scaleX get() = (targetWidth / other.exactWidth).toFloat()
    override val scaleY get() = scaleX * heightScale
    override val exactWidth get() = targetWidth
    override val exactHeight by lazy { other.exactHeight * scaleY }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (this.targetWidth == width.toDouble()) {
            this
        } else if (heightScale == 1f) {
            other.scaleWidth(width)
        } else {
            ScaleWidthBitmapStream(other, width.toDouble(), heightScale)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return other.scaleHeight(height)
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            ScaleWidthBitmapStream(other, targetWidth * scaleWidth, heightScale * scaleHeight)
        }
    }
}