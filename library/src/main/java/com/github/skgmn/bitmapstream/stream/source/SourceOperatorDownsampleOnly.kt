package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.StreamFeatures

@Suppress("EqualsOrHashCode")
internal class SourceOperatorDownsampleOnly(other: SourceBitmapStream) : SourceOperator(other) {
    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            downsampleOnly = true
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            SourceOperatorDownsampleOnly(new)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SourceOperatorDownsampleOnly) return false
        return super.equals(other)
    }
}