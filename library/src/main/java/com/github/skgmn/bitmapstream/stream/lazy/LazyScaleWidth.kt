package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream

internal class LazyScaleWidth(
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
        return LazyScaleWidth(new, width)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.scaleWidth(width)
    }
}