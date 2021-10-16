package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import kotlin.math.roundToInt

internal abstract class LazyOperator(
    protected val other: LazyBitmapStream
) : LazyBitmapStream() {
    override val metadata = object : BitmapMetadata {
        override val width get() = simulatedWidth.roundToInt()
        override val height get() = simulatedHeight.roundToInt()
        override val mimeType get() = other.metadata.mimeType
    }
    override val features get() = other.features

    override val simulatedWidth get() = other.simulatedWidth
    override val simulatedHeight get() = other.simulatedHeight
    override val hasDimensions get() = other.hasDimensions

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
}