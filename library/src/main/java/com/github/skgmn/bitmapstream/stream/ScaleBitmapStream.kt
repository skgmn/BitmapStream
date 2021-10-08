package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import kotlin.math.roundToInt

internal abstract class ScaleBitmapStream(
    other: BitmapStream
): DelegateBitmapStream(other) {
    abstract val scaleX: Float
    abstract val scaleY: Float

    override val metadata = object : BitmapMetadata {
        override val width get() = exactWidth.roundToInt()
        override val height get() = exactHeight.roundToInt()
        override val mimeType get() = other.metadata.mimeType
        override val densityScale get() = other.metadata.densityScale
    }

    override fun buildInputParameters(regional: Boolean): InputParameters {
        return other.buildInputParameters(regional).apply {
            scaleX *= this@ScaleBitmapStream.scaleX
            scaleY *= this@ScaleBitmapStream.scaleY
        }
    }
}