package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.StreamFeatures

internal class MutableBitmapStream(
    other: BitmapStream,
    private val mutable: Boolean
) : DelegateBitmapStream(other) {
    override fun mutable(mutable: Boolean): BitmapStream {
        return if (this.mutable == mutable) {
            this
        } else {
            other.mutable(mutable)
        }
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            mutable = this@MutableBitmapStream.mutable
        }
    }
}