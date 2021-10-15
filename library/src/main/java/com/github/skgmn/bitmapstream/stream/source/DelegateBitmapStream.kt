package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream

internal abstract class DelegateBitmapStream(
    protected val other: SourceBitmapStream
) : SourceBitmapStream() {
    override val metadata get() = other.metadata
    override val features get() = other.features
    override val hasMetadata get() = other.hasMetadata

    protected abstract fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream

    override fun mutable(mutable: Boolean?): BitmapStream {
        val mutableCleared = clearMutable()
        return when (mutable) {
            null -> mutableCleared
            else -> MutableBitmapStream(mutableCleared, mutable)
        }
    }

    override fun clearMutable(): SourceBitmapStream {
        val mutableCleared = other.clearMutable()
        return if (other === mutableCleared) {
            this
        } else {
            replaceUpstream(mutableCleared)
        }
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features)
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        return other.decode(inputParameters)
    }
}