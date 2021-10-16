package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.StreamFeatures

internal class DownsampleOnlyBitmapStream(other: SourceBitmapStream) : DelegateBitmapStream(other) {
    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            downsampleOnly = true
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            DownsampleOnlyBitmapStream(new)
        }
    }
}