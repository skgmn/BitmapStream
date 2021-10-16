package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream

internal class LazyOperatorScaleTo(
    other: LazyBitmapStream,
    private val width: Int,
    private val height: Int
) : LazyOperator(other) {
    override val simulatedWidth get() = width.toDouble()
    override val simulatedHeight get() = height.toDouble()

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (this.width == width && this.height == height) {
            this
        } else {
            other.scaleTo(width, height)
        }
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyOperatorScaleTo(new, width, height)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.scaleTo(width, height)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyOperatorScaleTo) return false
        if (!super.equals(other)) return false

        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}