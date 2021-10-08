package com.github.skgmn.bitmapstream.stream

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata

internal abstract class DelegateBitmapStream(
    protected val other: BitmapStream
): BitmapStream() {
    override val metadata: BitmapMetadata get() = other.metadata

    override fun buildInputParameters(regional: Boolean): InputParameters {
        return other.buildInputParameters(regional)
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        return other.decode(inputParameters)
    }
}