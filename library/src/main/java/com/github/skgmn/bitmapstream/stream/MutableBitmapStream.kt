package com.github.skgmn.bitmapstream.stream

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters

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

    override fun buildInputParameters(regional: Boolean): InputParameters {
        return other.buildInputParameters(regional).apply {
            mutable = this@MutableBitmapStream.mutable
        }
    }
}