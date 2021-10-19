package com.github.skgmn.bitmapstream.stream

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.metadata.BitmapSize

internal class NullBitmapStream : BitmapStream() {
    override val size = object : BitmapSize {
        override val width get() = 0
        override val height get() = 0
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return this
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return this
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return this
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return this
    }

    override fun scaleIn(maxWidth: Int, maxHeight: Int): BitmapStream {
        return this
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return this
    }

    override fun decode(): Bitmap? {
        return null
    }
}