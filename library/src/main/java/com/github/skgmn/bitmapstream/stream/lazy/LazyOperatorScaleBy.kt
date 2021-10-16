package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream

internal class LazyOperatorScaleBy(
    other: LazyBitmapStream,
    private val scaleX: Float,
    private val scaleY: Float
) : LazyOperator(other) {
    override val simulatedWidth by lazy(LazyThreadSafetyMode.NONE) {
        other.simulatedWidth * scaleX
    }
    override val simulatedHeight by lazy(LazyThreadSafetyMode.NONE) {
        other.simulatedHeight * scaleY
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyOperatorScaleBy(new, scaleX, scaleY)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.scaleBy(scaleX, scaleY)
    }
}