package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.metadata.BitmapSize
import kotlin.math.roundToInt

internal abstract class LazyOperator(
    protected val other: LazyBitmapStream
) : LazyBitmapStream() {
    override val size = object : BitmapSize {
        override val width get() = simulatedWidth.roundToInt()
        override val height get() = simulatedHeight.roundToInt()
    }
    override val features get() = other.features

    override val simulatedWidth get() = other.simulatedWidth
    override val simulatedHeight get() = other.simulatedHeight

    protected abstract fun replaceUpstream(new: LazyBitmapStream): LazyBitmapStream

    override fun mutable(mutable: Boolean?): LazyBitmapStream {
        val mutableCleared = replaceUpstream(other.mutable(null))
        return when (mutable) {
            null -> mutableCleared
            else -> LazyOperatorMutable(mutableCleared, mutable)
        }
    }

    override fun hardware(hardware: Boolean): LazyBitmapStream {
        val hardwareCleared = replaceUpstream(other.hardware(false))
        return if (hardware) {
            LazyOperatorHardware(hardwareCleared)
        } else {
            hardwareCleared
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyOperator) return false

        if (this.other != other.other) return false

        return true
    }

    override fun hashCode(): Int {
        return other.hashCode()
    }
}