package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream
import kotlin.math.min

internal class SourceOperatorScaleIn(
    other: SourceBitmapStream,
    private val maxWidth: Int,
    private val maxHeight: Int
) : SourceOperatorScaleBase(other) {
    private val scaleFactor by lazy(LazyThreadSafetyMode.NONE) {
        val w = other.size.width
        val h = other.size.height
        min(
            min(w, maxWidth).toFloat() / w,
            min(h, maxHeight).toFloat() / h
        )
    }

    override val scaleX get() = scaleFactor
    override val scaleY get() = scaleFactor
    override val exactWidth by lazy(LazyThreadSafetyMode.NONE) {
        other.exactWidth * scaleFactor
    }
    override val exactHeight by lazy(LazyThreadSafetyMode.NONE) {
        other.exactHeight * scaleFactor
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

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return SourceOperatorScaleIn(new, maxWidth, maxHeight)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SourceOperatorScaleIn) return false
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