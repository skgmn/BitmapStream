package com.github.skgmn.bitmapstream.stream.lazy

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream

internal abstract class LazyBitmapStream : BitmapStream() {
    internal abstract val simulatedWidth: Double
    internal abstract val simulatedHeight: Double

    internal abstract fun buildStream(): BitmapStream?

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return LazyOperatorScaleTo(this, width, height)
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return LazyOperatorScaleWidth(this, width)
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return LazyOperatorScaleHeight(this, height)
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            LazyOperatorScaleBy(this, scaleWidth, scaleHeight)
        }
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return LazyOperatorRegion(this, left, top, right, bottom)
    }

    override fun mutable(mutable: Boolean?): LazyBitmapStream {
        return if (mutable == null) {
            this
        } else {
            LazyOperatorMutable(this, mutable)
        }
    }

    override fun hardware(hardware: Boolean): LazyBitmapStream {
        return if (hardware) {
            LazyOperatorHardware(this)
        } else {
            this
        }
    }

    override fun decode(): Bitmap? {
        return buildStream()?.decode()
    }
}