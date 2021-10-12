package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class ScaleToBitmapStream(
    other: SourceBitmapStream,
    private val targetWidth: Double,
    private val targetHeight: Double
) : ScaleBitmapStream(other) {
    override val scaleX by lazy(LazyThreadSafetyMode.NONE) {
        (targetWidth / other.metadata.width).toFloat()
    }
    override val scaleY by lazy(LazyThreadSafetyMode.NONE) {
        (targetHeight / other.metadata.height).toFloat()
    }
    override val exactWidth get() = targetWidth
    override val exactHeight get() = targetHeight
    override val hasMetadata get() = true

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (this.targetWidth == width.toDouble() && this.targetHeight == height.toDouble()) {
            this
        } else {
            other.scaleTo(width, height)
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (targetWidth == width.toDouble()) {
            this
        } else {
            val scale = width / targetWidth
            ScaleToBitmapStream(other, width.toDouble(), targetHeight * scale)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (targetHeight == height.toDouble()) {
            this
        } else {
            val scale = height / targetHeight
            ScaleToBitmapStream(other, targetWidth * scale, height.toDouble())
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            ScaleToBitmapStream(other, targetWidth * scaleWidth, targetHeight * scaleHeight)
        }
    }
}