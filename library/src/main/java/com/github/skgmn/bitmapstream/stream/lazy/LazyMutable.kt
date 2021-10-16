package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures

internal class LazyMutable(
    other: LazyBitmapStream,
    private val mutable: Boolean
) : LazyOperator(other) {
    override val features = object : StreamFeatures by other.features {
        override val mutable get() = this@LazyMutable.mutable
        override val hardware: Boolean
            get() = if (this@LazyMutable.mutable) {
                false
            } else {
                other.features.hardware
            }
    }

    override fun mutable(mutable: Boolean?): LazyBitmapStream {
        return when (mutable) {
            null -> other
            this.mutable -> this
            else -> LazyMutable(other, mutable)
        }
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyMutable(new, mutable)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.mutable(mutable)
    }
}