package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.StreamFeatures

internal abstract class SourceOperator(
    protected val other: SourceBitmapStream
) : SourceBitmapStream() {
    override val metadata get() = other.metadata
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

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features)
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        return other.decode(inputParameters)
    }
}