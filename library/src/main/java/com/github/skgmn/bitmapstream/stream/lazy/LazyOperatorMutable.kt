package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures

internal class LazyOperatorMutable(
    other: LazyBitmapStream,
    private val mutable: Boolean
) : LazyOperator(other) {
    override val features = object : StreamFeatures by other.features {
        override val mutable get() = this@LazyOperatorMutable.mutable
        override val hardware: Boolean
            get() = if (this@LazyOperatorMutable.mutable) {
                false
            } else {
                other.features.hardware
            }
    }

    override fun mutable(mutable: Boolean?): LazyBitmapStream {
        return when (mutable) {
            null -> other
            this.mutable -> this
            else -> LazyOperatorMutable(other, mutable)
        }
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyOperatorMutable(new, mutable)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.mutable(mutable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyOperatorMutable) return false
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