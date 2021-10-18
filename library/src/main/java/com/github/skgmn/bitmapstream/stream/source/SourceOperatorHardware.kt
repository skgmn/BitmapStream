package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.StreamFeatures

@Suppress("EqualsOrHashCode")
internal class SourceOperatorHardware(
    other: SourceBitmapStream
) : SourceOperator(other) {
    override val features = object : StreamFeatures by other.features {
        override val hardware get() = true
        override val mutable: Boolean? get() = null
    }

    override fun hardware(hardware: Boolean): SourceBitmapStream {
        return if (hardware) this else other
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            SourceOperatorHardware(new)
        }
    }

    override fun buildInputParameters(): InputParameters {
        return other.buildInputParameters().apply {
            hardware = true
            mutable = null
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SourceOperatorHardware) return false
        return super.equals(other)
    }
}