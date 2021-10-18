package com.github.skgmn.bitmapstream.stream.source

@Suppress("EqualsOrHashCode")
internal class SourceOperatorDownsampleOnly(other: SourceBitmapStream) : SourceOperator(other) {
    override fun buildInputParameters(): InputParameters {
        return other.buildInputParameters().apply {
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