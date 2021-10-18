package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream

internal class LazyOperatorScaleHeight(
    other: LazyBitmapStream,
    private val height: Int
) : LazyOperator(other) {
    override val simulatedWidth by lazy(LazyThreadSafetyMode.NONE) {
        height / other.simulatedHeight * other.simulatedWidth
    }
    override val simulatedHeight get() = height.toDouble()

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (this.height == height) {
            this
        } else {
            other.scaleHeight(height)
        }
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyOperatorScaleHeight(new, height)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.scaleHeight(height)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyOperatorScaleHeight) return false
        if (!super.equals(other)) return false

        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + height
        return result
    }
}