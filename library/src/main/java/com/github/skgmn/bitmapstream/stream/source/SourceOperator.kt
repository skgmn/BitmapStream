package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap

internal abstract class SourceOperator(
    protected val other: SourceBitmapStream
) : SourceBitmapStream() {
    override val size get() = other.size
    override val features get() = other.features

    protected abstract fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream

    override fun mutable(mutable: Boolean?): SourceBitmapStream {
        val mutableCleared = replaceUpstream(other.mutable(null))
        return when (mutable) {
            null -> mutableCleared
            else -> SourceOperatorMutable(mutableCleared, mutable)
        }
    }

    override fun hardware(hardware: Boolean): SourceBitmapStream {
        val hardwareCleared = replaceUpstream(other.hardware(false))
        return if (hardware) {
            SourceOperatorHardware(hardwareCleared)
        } else {
            hardwareCleared
        }
    }

    override fun buildInputParameters(): InputParameters {
        return other.buildInputParameters()
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        return other.decode(inputParameters)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SourceOperator) return false

        if (this.other != other.other) return false

        return true
    }

    override fun hashCode(): Int {
        return other.hashCode()
    }
}