package com.github.skgmn.bitmapstream.stream.transform

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.util.mutable

internal class MutableTransformBitmapStream(
    other: BitmapStream,
    private val mutable: Boolean
) : TransformBitmapStream(other) {
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
        return other.decode()?.mutable(mutable)
    }
}