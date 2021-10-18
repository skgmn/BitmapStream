package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.StreamFeatures

internal class SourceOperatorMutable(
    other: SourceBitmapStream,
    private val mutable: Boolean
) : SourceOperator(other) {
    override val features = object : StreamFeatures by other.features {
        override val hardware: Boolean
            get() = if (this@SourceOperatorMutable.mutable) {
                false
            } else {
                other.features.hardware
            }
        override val mutable get() = this@SourceOperatorMutable.mutable
    }

    override fun mutable(mutable: Boolean?): SourceBitmapStream {
        return when (mutable) {
            null -> other
            this.mutable -> this
            else -> SourceOperatorMutable(other, mutable)
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            SourceOperatorMutable(new, mutable)
        }
    }

    override fun buildInputParameters(): InputParameters {
        return other.buildInputParameters().apply {
            mutable = this@SourceOperatorMutable.mutable
            if (this@SourceOperatorMutable.mutable) {
                hardware = false
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SourceOperatorMutable) return false
        if (!super.equals(other)) return false

        if (mutable != other.mutable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + mutable.hashCode()
        return result
    }
}