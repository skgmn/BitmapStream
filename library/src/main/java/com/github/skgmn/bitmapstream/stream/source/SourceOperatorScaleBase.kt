package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.metadata.BitmapSize
import kotlin.math.roundToInt

internal abstract class SourceOperatorScaleBase(
    other: SourceBitmapStream
): SourceOperator(other) {
    abstract val scaleX: Float
    abstract val scaleY: Float

    override val size = object : BitmapSize {
        override val width get() = exactWidth.roundToInt()
        override val height get() = exactHeight.roundToInt()
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun buildInputParameters(): InputParameters {
        return other.buildInputParameters().apply {
            scaleX *= this@SourceOperatorScaleBase.scaleX
            scaleY *= this@SourceOperatorScaleBase.scaleY
        }
    }
}