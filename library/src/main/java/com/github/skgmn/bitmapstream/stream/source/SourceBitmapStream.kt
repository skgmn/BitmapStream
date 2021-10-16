package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.ExtendedBitmapMetadata

internal abstract class SourceBitmapStream : BitmapStream() {
    abstract override val metadata: ExtendedBitmapMetadata

    internal open val exactWidth: Double get() = metadata.width.toDouble()
    internal open val exactHeight: Double get() = metadata.height.toDouble()

    internal abstract fun buildInputParameters(features: StreamFeatures): InputParameters
    internal abstract fun decode(inputParameters: InputParameters): Bitmap?

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return SourceOperatorScaleTo(this, width.toDouble(), height.toDouble())
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return SourceOperatorScaleWidth(this, width.toDouble(), 1f)
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return SourceOperatorScaleHeight(this, height.toDouble(), 1f)
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            SourceOperatorScaleBy(this, scaleWidth, scaleHeight)
        }
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return SourceOperatorRegion(this, left, top, right, bottom)
    }

    override fun mutable(mutable: Boolean?): SourceBitmapStream {
        return if (mutable == null) {
            this
        } else {
            SourceOperatorMutable(this, mutable)
        }
    }

    override fun hardware(hardware: Boolean): SourceBitmapStream {
        return if (hardware) {
            SourceOperatorHardware(this)
        } else {
            this
        }
    }

    override fun downsampleOnly(): BitmapStream {
        return SourceOperatorDownsampleOnly(this)
    }

    override fun decode(): Bitmap? {
        return decode(buildInputParameters(features))
    }
}