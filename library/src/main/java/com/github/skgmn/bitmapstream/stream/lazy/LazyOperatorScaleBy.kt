package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream

internal class LazyOperatorScaleBy(
    other: LazyBitmapStream,
    private val scaleX: Float,
    private val scaleY: Float
) : LazyOperator(other) {
    override val simulatedWidth by lazy(LazyThreadSafetyMode.NONE) {
        other.simulatedWidth * scaleX
    }
    override val simulatedHeight by lazy(LazyThreadSafetyMode.NONE) {
        other.simulatedHeight * scaleY
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            other.scaleBy(scaleX * scaleWidth, scaleY * scaleHeight)
        }
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyOperatorScaleBy(new, scaleX, scaleY)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.scaleBy(scaleX, scaleY)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyOperatorScaleBy) return false
        if (!super.equals(other)) return false

        if (scaleX != other.scaleX) return false
        if (scaleY != other.scaleY) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + scaleX.hashCode()
        result = 31 * result + scaleY.hashCode()
        return result
    }
}