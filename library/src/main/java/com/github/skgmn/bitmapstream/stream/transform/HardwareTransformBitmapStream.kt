package com.github.skgmn.bitmapstream.stream.transform

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.util.characteristic

internal class HardwareTransformBitmapStream(
    other: BitmapStream
) : TransformBitmapStream(other) {
    override val features = object : StreamFeatures by other.features {
        override val hardware get() = true
        override val mutable: Boolean? get() = null
    }

    override fun hardware(hardware: Boolean): BitmapStream {
        return if (hardware) this else other
    }

    override fun replaceUpstream(new: BitmapStream): BitmapStream {
        return if (other === new) {
            this
        } else {
            HardwareTransformBitmapStream(new)
        }
    }

    override fun decode(): Bitmap? {
        return other.mutable(null).decode()?.characteristic(true, false)
    }
}