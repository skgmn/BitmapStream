package com.github.skgmn.bitmapstream.stream.lazy

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream

internal abstract class LazyBitmapStream : BitmapStream() {
    internal abstract val simulatedWidth: Double
    internal abstract val simulatedHeight: Double

    internal abstract fun buildStream(): BitmapStream?

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (hasDimensions &&
            simulatedWidth == width.toDouble() &&
            simulatedHeight == height.toDouble()
        ) {
            this
        } else {
            LazyScaleTo(this, width, height)
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (hasDimensions && simulatedWidth == width.toDouble()) {
            this
        } else {
            LazyScaleWidth(this, width)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (hasDimensions && simulatedHeight == height.toDouble()) {
            this
        } else {
            LazyScaleHeight(this, height)
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            LazyScaleBy(this, scaleWidth, scaleHeight)
        }
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return if (hasDimensions && left == 0 && top == 0 &&
            right.toDouble() == simulatedWidth && bottom.toDouble() == simulatedHeight
        ) {
            this
        } else {
            LazyRegion(this, left, top, right, bottom)
        }
    }

    override fun mutable(mutable: Boolean?): LazyBitmapStream {
        return if (mutable == null) {
            this
        } else {
            LazyMutable(this, mutable)
        }
    }

    override fun hardware(hardware: Boolean): LazyBitmapStream {
        return if (hardware) {
            LazyHardware(this)
        } else {
            this
        }
    }

    override fun decode(): Bitmap? {
        return buildStream()?.decode()
    }
}