package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream

internal class LazyScaleTo(
    other: LazyBitmapStream,
    private val width: Int,
    private val height: Int
) : LazyOperator(other) {
    override val simulatedWidth get() = width.toDouble()
    override val simulatedHeight get() = height.toDouble()

    override val hasDimensions get() = true

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (this.width == width && this.height == height) {
            this
        } else {
            other.scaleTo(width, height)
        }
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyScaleTo(new, width, height)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.scaleTo(width, height)
    }
}