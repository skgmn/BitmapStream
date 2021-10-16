package com.github.skgmn.bitmapstream.stream.transform

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.util.characteristic

internal class MutableTransformBitmapStream(
    other: BitmapStream,
    private val mutable: Boolean
) : TransformBitmapStream(other) {
    override val features = object : StreamFeatures by other.features {
        override val hardware: Boolean
            get() = if (this@MutableTransformBitmapStream.mutable) {
                false
            } else {
                other.features.hardware
            }
        override val mutable get() = this@MutableTransformBitmapStream.mutable
    }

    override fun mutable(mutable: Boolean?): BitmapStream {
        return when (this.mutable) {
            mutable -> this
            else -> other.mutable(mutable)
        }
    }

    override fun replaceUpstream(new: BitmapStream): BitmapStream {
        return if (other === new) {
            this
        } else {
            MutableTransformBitmapStream(new, mutable)
        }
    }

    override fun decode(): Bitmap? {
        val stream = if (mutable) {
            other.hardware(false)
        } else {
            other
        }
        return stream.decode()?.characteristic(null, mutable)
    }
}