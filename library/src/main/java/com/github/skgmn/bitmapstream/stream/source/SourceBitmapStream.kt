package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import android.widget.ImageView
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.frame.*

internal abstract class SourceBitmapStream : BitmapStream() {
    internal open val features = object : StreamFeatures {
        override val regional: Boolean
            get() = false
    }

    internal open val exactWidth: Double get() = metadata.width.toDouble()
    internal open val exactHeight: Double get() = metadata.height.toDouble()

    internal abstract fun buildInputParameters(features: StreamFeatures): InputParameters
    internal abstract fun decode(inputParameters: InputParameters): Bitmap?

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return ScaleToBitmapStream(this, width.toDouble(), height.toDouble())
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return ScaleWidthBitmapStream(this, width.toDouble(), 1f)
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return ScaleHeightBitmapStream(this, height.toDouble(), 1f)
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            ScaleByBitmapStream(this, scaleWidth, scaleHeight)
        }
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return RegionBitmapStream(this, left, top, right, bottom)
    }

    override fun mutable(mutable: Boolean?): BitmapStream {
        return MutableBitmapStream(this, mutable)
    }

    override fun frame(
        frameWidth: Int,
        frameHeight: Int,
        scaleType: ImageView.ScaleType
    ): BitmapStream {
        val frameMethod = when (scaleType) {
            ImageView.ScaleType.MATRIX -> MatrixFrameMethod()
            ImageView.ScaleType.FIT_XY -> FitXYFrameMethod()
            ImageView.ScaleType.FIT_START ->
                FitGravityFrameMethod(FitGravityFrameMethod.GRAVITY_START)
            ImageView.ScaleType.FIT_CENTER ->
                FitGravityFrameMethod(FitGravityFrameMethod.GRAVITY_CENTER)
            ImageView.ScaleType.FIT_END ->
                FitGravityFrameMethod(FitGravityFrameMethod.GRAVITY_END)
            ImageView.ScaleType.CENTER -> CenterFrameMethod()
            ImageView.ScaleType.CENTER_INSIDE -> CenterInsideFrameMethod()
            else -> throw IllegalArgumentException()
        }
        return FrameBitmapStream(this, frameWidth, frameHeight, frameMethod)
    }

    override fun decode(): Bitmap? {
        return decode(buildInputParameters(features))
    }
}