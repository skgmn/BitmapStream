package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures

internal class LazyOperatorRegion(
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

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyOperatorRegion(new, left, top, right, bottom)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.region(left, top, right, bottom)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyOperatorRegion) return false
        if (!super.equals(other)) return false

        if (left != other.left) return false
        if (top != other.top) return false
        if (right != other.right) return false
        if (bottom != other.bottom) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + left
        result = 31 * result + top
        result = 31 * result + right
        result = 31 * result + bottom
        return result
    }
}