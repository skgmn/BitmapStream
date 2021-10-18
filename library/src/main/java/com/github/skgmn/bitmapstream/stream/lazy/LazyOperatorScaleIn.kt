package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream
import kotlin.math.min

internal class LazyOperatorScaleIn(
    other: LazyBitmapStream,
    private val maxWidth: Int,
    private val maxHeight: Int
) : LazyOperator(other) {
    private val scaleFactor by lazy(LazyThreadSafetyMode.NONE) {
        val w = other.simulatedWidth
        val h = other.simulatedHeight
        min(
            min(w, maxWidth.toDouble()) / w,
            min(h, maxHeight.toDouble()) / h
        )
    }

    override val simulatedWidth by lazy(LazyThreadSafetyMode.NONE) {
        other.simulatedWidth * scaleFactor
    }
    override val simulatedHeight by lazy(LazyThreadSafetyMode.NONE) {
        other.simulatedHeight * scaleFactor
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleIn(maxWidth: Int, maxHeight: Int): BitmapStream {
        return if (this.maxWidth == maxWidth && this.maxHeight == maxHeight ||
            maxWidth == Int.MAX_VALUE && maxHeight == Int.MAX_VALUE
        ) {
            this
        } else {
            other.scaleIn(
                min(this.maxWidth, maxWidth),
                min(this.maxHeight, maxHeight)
            )
        }
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyOperatorScaleIn(new, maxWidth, maxHeight)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.scaleIn(maxWidth, maxHeight)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyOperatorScaleIn) return false
        if (!super.equals(other)) return false

        if (maxWidth != other.maxWidth) return false
        if (maxHeight != other.maxHeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + maxWidth
        result = 31 * result + maxHeight
        return result
    }
}