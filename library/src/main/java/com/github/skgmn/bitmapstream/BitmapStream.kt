package com.github.skgmn.bitmapstream

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.*
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.github.skgmn.bitmapstream.frame.*
import com.github.skgmn.bitmapstream.frame.CenterFrameMethod
import com.github.skgmn.bitmapstream.frame.CenterInsideFrameMethod
import com.github.skgmn.bitmapstream.frame.FitGravityFrameMethod
import com.github.skgmn.bitmapstream.frame.FitXYFrameMethod
import com.github.skgmn.bitmapstream.frame.MatrixFrameMethod
import com.github.skgmn.bitmapstream.source.*
import com.github.skgmn.bitmapstream.source.AssetBitmapSource
import com.github.skgmn.bitmapstream.source.ByteArrayBitmapSource
import com.github.skgmn.bitmapstream.source.FileBitmapSource
import com.github.skgmn.bitmapstream.source.InputStreamBitmapSource
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.*
import com.github.skgmn.bitmapstream.stream.ScaleByBitmapStream
import com.github.skgmn.bitmapstream.stream.ScaleHeightBitmapStream
import com.github.skgmn.bitmapstream.stream.ScaleToBitmapStream
import com.github.skgmn.bitmapstream.stream.ScaleWidthBitmapStream
import com.github.skgmn.bitmapstream.stream.SourceBitmapStream
import java.io.File
import java.io.InputStream
import java.lang.IllegalArgumentException

abstract class BitmapStream {
    abstract val width: Int
    abstract val height: Int
    abstract val mimeType: String?

    open fun scaleTo(width: Int, height: Int): BitmapStream {
        return ScaleToBitmapStream(this, width, height)
    }

    open fun scaleWidth(width: Int): BitmapStream {
        return ScaleWidthBitmapStream(this, width)
    }

    open fun scaleHeight(height: Int): BitmapStream {
        return ScaleHeightBitmapStream(this, height)
    }

    open fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            ScaleByBitmapStream(this, scaleWidth, scaleHeight)
        }
    }

    open fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return RegionBitmapStream(this, left, top, right, bottom)
    }

    fun region(bounds: Rect): BitmapStream {
        return region(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    open fun mutable(mutable: Boolean): BitmapStream {
        return MutableBitmapStream(this, mutable)
    }

    fun decode(): Bitmap? {
        return decode(buildInputParameters(false))
    }

    fun frame(frameWidth: Int, frameHeight: Int, scaleType: ImageView.ScaleType): BitmapStream {
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

    internal abstract fun decode(inputParameters: InputParameters): Bitmap?
    internal abstract fun buildInputParameters(regional: Boolean): InputParameters

    internal fun postProcess(bitmap: Bitmap?, params: DecodingParameters): Bitmap? {
        return if (bitmap == null || params.postScaleX == 1f && params.postScaleY == 1f) {
            bitmap
        } else {
            val m = Matrix()
            m.setScale(params.postScaleX, params.postScaleY)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
        }
    }

    internal fun setMutable(bitmap: Bitmap, mutable: Boolean): Bitmap {
        if (bitmap.isMutable == mutable) {
            return bitmap
        }
        if (mutable) {
            val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            val destRect = Rect(0, 0, bitmap.width, bitmap.height)
            Canvas(newBitmap).drawBitmap(bitmap, null, destRect, paint)
            return newBitmap
        } else {
            return Bitmap.createBitmap(bitmap)
        }
    }

    companion object {
        @JvmStatic
        fun fromAsset(assetManager: AssetManager, path: String): BitmapStream {
            return SourceBitmapStream(AssetBitmapSource(assetManager, path))
        }

        @JvmStatic
        fun fromByteArray(array: ByteArray): BitmapStream {
            return fromByteArray(array, 0, array.size)
        }

        @JvmStatic
        fun fromByteArray(array: ByteArray, offset: Int, length: Int): BitmapStream {
            return SourceBitmapStream(ByteArrayBitmapSource(array, offset, length))
        }

        @JvmStatic
        fun fromFile(file: File): BitmapStream {
            return SourceBitmapStream(FileBitmapSource(file))
        }

        @JvmStatic
        fun fromResource(res: Resources, @DrawableRes id: Int): BitmapStream {
            return SourceBitmapStream(ResourceBitmapSource(res, id))
        }

        @JvmStatic
        fun fromInputStream(inputStream: InputStream): BitmapStream {
            return SourceBitmapStream(InputStreamBitmapSource(inputStream))
        }

        @JvmStatic
        fun fromInputStreamFactory(inputStreamFactory: InputStreamFactory): BitmapStream {
            return SourceBitmapStream(InputStreamFactoryBitmapSource(inputStreamFactory))
        }
    }
}