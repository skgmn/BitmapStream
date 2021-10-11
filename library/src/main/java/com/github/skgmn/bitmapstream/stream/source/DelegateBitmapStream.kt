package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata

internal abstract class DelegateBitmapStream(
    protected val other: SourceBitmapStream
): SourceBitmapStream() {
    override val metadata: BitmapMetadata get() = other.metadata

    override val features get() = other.features

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features)
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        return other.decode(inputParameters)
    }
}