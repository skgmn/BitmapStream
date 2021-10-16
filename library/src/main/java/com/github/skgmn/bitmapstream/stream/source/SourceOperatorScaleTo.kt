package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class SourceOperatorScaleTo(
    other: SourceBitmapStream,
    private val targetWidth: Double,
    private val targetHeight: Double
) : SourceOperatorScaleBase(other) {
    override val scaleX by lazy(LazyThreadSafetyMode.NONE) {
        (targetWidth / other.metadata.width).toFloat()
    }
    override val scaleY by lazy(LazyThreadSafetyMode.NONE) {
        (targetHeight / other.metadata.height).toFloat()
    }
    override val exactWidth get() = targetWidth
    override val exactHeight get() = targetHeight

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
            SourceOperatorScaleTo(other, width.toDouble(), targetHeight * scale)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (targetHeight == height.toDouble()) {
            this
        } else {
            val scale = height / targetHeight
            SourceOperatorScaleTo(other, targetWidth * scale, height.toDouble())
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            SourceOperatorScaleTo(other, targetWidth * scaleWidth, targetHeight * scaleHeight)
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            SourceOperatorScaleTo(new, targetWidth, targetHeight)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SourceOperatorScaleTo) return false
        if (!super.equals(other)) return false

        if (targetWidth != other.targetWidth) return false
        if (targetHeight != other.targetHeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + targetWidth.hashCode()
        result = 31 * result + targetHeight.hashCode()
        return result
    }
}