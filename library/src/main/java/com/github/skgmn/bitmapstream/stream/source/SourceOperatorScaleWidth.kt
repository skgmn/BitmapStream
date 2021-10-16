package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class SourceOperatorScaleWidth(
    other: SourceBitmapStream,
    private val targetWidth: Double,
    private val heightScale: Float
) : SourceOperatorScaleBase(other) {
    override val scaleX by lazy(LazyThreadSafetyMode.NONE) {
        (targetWidth / other.exactWidth).toFloat()
    }
    override val scaleY by lazy(LazyThreadSafetyMode.NONE) { scaleX * heightScale }
    override val exactWidth get() = targetWidth
    override val exactHeight by lazy(LazyThreadSafetyMode.NONE) { other.exactHeight * scaleY }

    override fun scaleWidth(width: Int): BitmapStream {
        return when {
            this.targetWidth == width.toDouble() -> this
            heightScale == 1f -> other.scaleWidth(width)
            else -> SourceOperatorScaleWidth(other, width.toDouble(), heightScale)
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
            SourceOperatorScaleWidth(other, targetWidth * scaleWidth, heightScale * scaleHeight)
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            SourceOperatorScaleWidth(new, targetWidth, heightScale)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SourceOperatorScaleWidth) return false
        if (!super.equals(other)) return false

        if (targetWidth != other.targetWidth) return false
        if (heightScale != other.heightScale) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + targetWidth.hashCode()
        result = 31 * result + heightScale.hashCode()
        return result
    }
}