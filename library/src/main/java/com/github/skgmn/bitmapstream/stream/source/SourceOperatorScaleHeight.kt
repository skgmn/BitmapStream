package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class SourceOperatorScaleHeight(
    other: SourceBitmapStream,
    private val targetHeight: Double,
    private val widthScale: Float
) : SourceOperatorScaleBase(other) {
    override val scaleX by lazy(LazyThreadSafetyMode.NONE) { scaleY * widthScale }
    override val scaleY by lazy(LazyThreadSafetyMode.NONE) {
        (targetHeight / other.exactHeight).toFloat()
    }
    override val exactWidth by lazy(LazyThreadSafetyMode.NONE) { other.exactWidth * scaleX }
    override val exactHeight: Double get() = targetHeight

    override fun scaleWidth(width: Int): BitmapStream {
        return if (widthScale == 1f) {
            other.scaleWidth(width)
        } else {
            super.scaleWidth(width)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return when {
            targetHeight == height.toDouble() -> this
            widthScale == 1f -> other.scaleHeight(height)
            else -> SourceOperatorScaleHeight(other, height.toDouble(), widthScale)
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            SourceOperatorScaleHeight(other, targetHeight * scaleHeight, widthScale * scaleWidth)
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            SourceOperatorScaleHeight(new, targetHeight, widthScale)
        }
    }
}