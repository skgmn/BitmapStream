package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream

internal abstract class SourceBitmapStream : BitmapStream() {
    internal open val features = object : StreamFeatures {
        override val regional get() = false
    }

    internal open val exactWidth: Double get() = metadata.width.toDouble()
    internal open val exactHeight: Double get() = metadata.height.toDouble()

    internal open val hasMetadata get() = false

    internal open fun clearMutable(): SourceBitmapStream = this

    internal abstract fun buildInputParameters(features: StreamFeatures): InputParameters
    internal abstract fun decode(inputParameters: InputParameters): Bitmap?

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (hasMetadata &&
            width.toDouble() == exactWidth && height.toDouble() == exactHeight
        ) {
            this
        } else {
            ScaleToBitmapStream(this, width.toDouble(), height.toDouble())
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (hasMetadata && width.toDouble() == exactWidth) {
            this
        } else {
            ScaleWidthBitmapStream(this, width.toDouble(), 1f)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (hasMetadata && height.toDouble() == exactHeight) {
            this
        } else {
            ScaleHeightBitmapStream(this, height.toDouble(), 1f)
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            ScaleByBitmapStream(this, scaleWidth, scaleHeight)
        }
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return if (hasMetadata && left == 0 && top == 0 &&
            right.toDouble() == exactWidth && bottom.toDouble() == exactHeight) {
            this
        } else {
            RegionBitmapStream(this, left, top, right, bottom)
        }
    }

    override fun mutable(mutable: Boolean?): BitmapStream {
        return if (mutable == null) {
            this
        } else {
            MutableBitmapStream(this, mutable)
        }
    }

    override fun downsampleOnly(): BitmapStream {
        return DownsampleOnlyBitmapStream(this)
    }

    override fun decode(): Bitmap? {
        return decode(buildInputParameters(features))
    }
}