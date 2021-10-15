package com.github.skgmn.bitmapstream.stream.source

internal class MutableBitmapStream(
    other: SourceBitmapStream,
    private val mutable: Boolean
) : DelegateBitmapStream(other) {
    override fun mutable(mutable: Boolean?): SourceBitmapStream {
        return when (mutable) {
            null -> other
            this.mutable == mutable -> this
            else -> MutableBitmapStream(other, mutable)
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            MutableBitmapStream(new, mutable)
        }
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            mutable = this@MutableBitmapStream.mutable
        }
    }
}