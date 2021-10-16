package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream

internal class LazyOperatorScaleWidth(
    other: LazyBitmapStream,
    private val width: Int
) : LazyOperator(other) {
    override val simulatedWidth get() = width.toDouble()
    override val simulatedHeight by lazy(LazyThreadSafetyMode.NONE) {
        width / other.simulatedWidth * other.simulatedHeight
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (this.width == width) {
            this
        } else {
            other.scaleWidth(width)
        }
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyOperatorScaleWidth(new, width)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.scaleWidth(width)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyOperatorScaleWidth) return false
        if (!super.equals(other)) return false

        if (width != other.width) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + width
        return result
    }
}