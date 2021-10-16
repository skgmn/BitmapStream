package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.ExtendedBitmapMetadata

internal abstract class SourceBitmapStream : BitmapStream() {
    abstract override val metadata: ExtendedBitmapMetadata

    internal open val exactWidth: Double get() = metadata.width.toDouble()
    internal open val exactHeight: Double get() = metadata.height.toDouble()

    internal open val hasMetadata get() = false

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

    override fun mutable(mutable: Boolean?): SourceBitmapStream {
        return if (mutable == null) {
            this
        } else {
            MutableBitmapStream(this, mutable)
        }
    }

    override fun hardware(hardware: Boolean): SourceBitmapStream {
        return if (hardware) {
            HardwareBitmapStream(this)
        } else {
            this
        }
    }

    override fun downsampleOnly(): BitmapStream {
        return DownsampleOnlyBitmapStream(this)
    }

    override fun decode(): Bitmap? {
        return decode(buildInputParameters(features))
    }
}