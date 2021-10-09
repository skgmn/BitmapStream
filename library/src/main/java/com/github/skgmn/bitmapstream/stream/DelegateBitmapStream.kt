package com.github.skgmn.bitmapstream.stream

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata

internal abstract class DelegateBitmapStream(
    protected val other: BitmapStream
): BitmapStream() {
    override val metadata: BitmapMetadata get() = other.metadata

    override val features get() = other.features

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features)
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        return other.decode(inputParameters)
    }
}