package com.github.skgmn.bitmapstream.stream.source

internal class HardwareBitmapStream(
    other: SourceBitmapStream
) : DelegateBitmapStream(other) {
    override fun hardware(hardware: Boolean): SourceBitmapStream {
        return if (hardware) this else other
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            HardwareBitmapStream(new)
        }
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            hardware = true
            mutable = null
        }
    }
}