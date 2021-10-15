package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class MutableBitmapStream(
    other: SourceBitmapStream,
    private val mutable: Boolean
) : DelegateBitmapStream(other) {
    override fun mutable(mutable: Boolean?): BitmapStream {
        return when (mutable) {
            null -> other
            this.mutable == mutable -> this
            else -> MutableBitmapStream(other, mutable)
        }
    }

    override fun clearMutable(): SourceBitmapStream {
        return other
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return MutableBitmapStream(new, mutable)
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            mutable = this@MutableBitmapStream.mutable
        }
    }
}