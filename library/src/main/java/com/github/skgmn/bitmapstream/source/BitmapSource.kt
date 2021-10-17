package com.github.skgmn.bitmapstream.source

import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.stream.source.InputParameters

internal abstract class BitmapSource {
    abstract fun createDecodeSession(): DecodeSession
    open fun generateInputParameters(
        features: StreamFeatures,
        metadata: BitmapMetadata
    ): InputParameters = InputParameters()
}