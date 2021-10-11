package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class MutableBitmapStream(
    other: SourceBitmapStream,
    private val mutable: Boolean?
) : DelegateBitmapStream(other) {
    override fun mutable(mutable: Boolean?): BitmapStream {
        return if (this.mutable == mutable) {
            this
        } else {
            other.mutable(mutable)
        }
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            if (this@MutableBitmapStream.mutable != null) {
                mutable = this@MutableBitmapStream.mutable
            }
        }
    }
}