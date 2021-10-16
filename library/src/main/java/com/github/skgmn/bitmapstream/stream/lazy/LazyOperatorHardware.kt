package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures

@Suppress("EqualsOrHashCode")
internal class LazyOperatorHardware(other: LazyBitmapStream) : LazyOperator(other) {
    override val features = object : StreamFeatures by other.features {
        override val hardware get() = true
        override val mutable: Boolean? get() = null
    }

    override fun hardware(hardware: Boolean): LazyBitmapStream {
        return if (hardware) this else other
    }

    override fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream {
        return LazyOperatorHardware(new)
    }

    override fun buildStream(): BitmapStream? {
        return other.buildStream()?.hardware(true)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyOperatorHardware) return false
        return super.equals(other)
    }
}