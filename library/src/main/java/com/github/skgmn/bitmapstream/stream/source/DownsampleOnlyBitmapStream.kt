package com.github.skgmn.bitmapstream.stream.source

internal class DownsampleOnlyBitmapStream(other: SourceBitmapStream) : DelegateBitmapStream(other) {
    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            downsampleOnly = true
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return DownsampleOnlyBitmapStream(new)
    }
}