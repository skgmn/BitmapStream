package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.ExtendedBitmapMetadata
import kotlin.math.roundToInt

internal abstract class ScaleBitmapStream(
    other: SourceBitmapStream
): SourceOperator(other) {
    abstract val scaleX: Float
    abstract val scaleY: Float

    override val metadata = object : ExtendedBitmapMetadata {
        override val width get() = exactWidth.roundToInt()
        override val height get() = exactHeight.roundToInt()
        override val mimeType get() = other.metadata.mimeType
        override val densityScale get() = other.metadata.densityScale
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            scaleX *= this@ScaleBitmapStream.scaleX
            scaleY *= this@ScaleBitmapStream.scaleY
        }
    }
}