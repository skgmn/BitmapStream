package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream

internal class LazyScaleHeight(
    other: LazyBitmapStream,
    private val height: Int
) : LazyOperator(other) {
    override val simulatedWidth by lazy(LazyThreadSafetyMode.NONE) {
        height / other.simulatedHeight * other.simulatedWidth
    }
    override val simulatedHeight get() = height.toDouble()

    override fun scaleHeight(height: Int): BitmapStream {
        return if (this.height == height) {
            this
        } else {
            other.scaleHeight(height)
        }
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyScaleHeight(new, height)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.scaleHeight(height)
    }
}