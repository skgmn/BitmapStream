package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.StreamFeatures

internal class MutableBitmapStream(
    other: SourceBitmapStream,
    private val mutable: Boolean
) : DelegateBitmapStream(other) {
    override val features = object : StreamFeatures by other.features {
        override val hardware: Boolean
            get() = if (this@MutableBitmapStream.mutable) {
                false
            } else {
                other.features.hardware
            }
        override val mutable get() = this@MutableBitmapStream.mutable
    }

    override fun mutable(mutable: Boolean?): SourceBitmapStream {
        return when (mutable) {
            null -> other
            this.mutable -> this
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
            if (this@MutableBitmapStream.mutable) {
                hardware = false
            }
        }
    }
}