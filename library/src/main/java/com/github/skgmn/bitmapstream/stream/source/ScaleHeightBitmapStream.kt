package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class ScaleHeightBitmapStream(
    other: SourceBitmapStream,
    private val targetHeight: Double,
    private val widthScale: Float
) : ScaleBitmapStream(other) {
    override val scaleX get() = scaleY * widthScale
    override val scaleY get() = (targetHeight / other.exactHeight).toFloat()
    override val exactWidth by lazy { other.exactWidth * scaleX }
    override val exactHeight: Double get() = targetHeight

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return other.scaleWidth(width)
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (targetHeight == height.toDouble()) {
            this
        } else if (widthScale == 1f) {
            other.scaleHeight(height)
        } else {
            ScaleHeightBitmapStream(other, height.toDouble(), widthScale)
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            ScaleHeightBitmapStream(other, targetHeight * scaleHeight, widthScale * scaleWidth)
        }
    }
}