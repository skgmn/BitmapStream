package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import kotlin.math.roundToInt

internal abstract class SourceOperatorScaleBase(
    other: SourceBitmapStream
): SourceOperator(other) {
    abstract val scaleX: Float
    abstract val scaleY: Float

    override val size = object : BitmapMetadata {
        override val width get() = exactWidth.roundToInt()
        override val height get() = exactHeight.roundToInt()
        override val densityScale get() = other.size.densityScale
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            scaleX *= this@SourceOperatorScaleBase.scaleX
            scaleY *= this@SourceOperatorScaleBase.scaleY
        }
    }
}