package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures

internal class LazyRegion(
    other: LazyBitmapStream,
    private val left: Int,
    private val top: Int,
    private val right: Int,
    private val bottom: Int
) : LazyOperator(other) {
    override val features = object : StreamFeatures by other.features {
        override val regional get() = true
    }

    override val simulatedWidth get() = (right - left).toDouble()
    override val simulatedHeight get() = (bottom - top).toDouble()

    override val hasDimensions get() = true

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyRegion(new, left, top, right, bottom)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.region(left, top, right, bottom)
    }
}