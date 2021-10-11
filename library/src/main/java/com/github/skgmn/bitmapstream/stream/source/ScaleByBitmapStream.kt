package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import kotlin.math.roundToInt

internal class ScaleByBitmapStream(
    other: SourceBitmapStream,
    private val scaleX: Float,
    private val scaleY: Float
) : DelegateBitmapStream(other) {
    override val metadata = object : BitmapMetadata {
        override val width by lazy { (other.metadata.width * scaleX).roundToInt() }
        override val height by lazy { (other.metadata.height * scaleY).roundToInt() }
        override val mimeType: String? get() = other.metadata.mimeType
        override val densityScale: Float get() = other.metadata.densityScale
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            val sx = this.scaleX * scaleWidth
            val sy = this.scaleY * scaleHeight
            if (sx == 1f && sy == 1f) {
                other
            } else {
                other.scaleBy(sx, sy)
            }
        }
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            scaleX *= this@ScaleByBitmapStream.scaleX
            scaleY *= this@ScaleByBitmapStream.scaleY
        }
    }
}