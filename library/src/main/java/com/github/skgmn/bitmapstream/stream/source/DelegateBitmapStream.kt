package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap

internal abstract class DelegateBitmapStream(
    protected val other: SourceBitmapStream
) : SourceBitmapStream() {
    override val metadata get() = other.metadata
    override val features get() = other.features
    override val hasMetadata get() = other.hasMetadata

    protected abstract fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream

    override fun mutable(mutable: Boolean?): SourceBitmapStream {
        val mutableCleared = replaceUpstream(other.mutable(null))
        return when (mutable) {
            null -> mutableCleared
            else -> MutableBitmapStream(mutableCleared, mutable)
        }
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features)
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        return other.decode(inputParameters)
    }
}