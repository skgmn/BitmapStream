package com.github.skgmn.bitmapstream

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.github.skgmn.bitmapstream.frame.*
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.source.factory.*
import com.github.skgmn.bitmapstream.stream.canvas.CanvasBitmapStream
import com.github.skgmn.bitmapstream.stream.canvas.drawStream
import com.github.skgmn.bitmapstream.stream.source.DecodingParameters
import com.github.skgmn.bitmapstream.stream.source.factory.FactorySourceBitmapStream
import java.io.File
import java.io.InputStream
import kotlin.math.roundToInt

abstract class BitmapStream {
    abstract val metadata: BitmapMetadata

    abstract fun scaleTo(width: Int, height: Int): BitmapStream
    abstract fun scaleWidth(width: Int): BitmapStream
    abstract fun scaleHeight(height: Int): BitmapStream
    abstract fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream
    abstract fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream
    abstract fun mutable(mutable: Boolean?): BitmapStream

    abstract fun decode(): Bitmap?

    fun region(bounds: Rect): BitmapStream {
        return region(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    fun frame(
        frameWidth: Int,
        frameHeight: Int,
        scaleType: ImageView.ScaleType,
        background: Drawable?
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
            ImageView.ScaleType.CENTER_CROP -> CenterCropFrameMethod()
            else -> throw IllegalArgumentException()
        }
        return CanvasBitmapStream(frameWidth, frameHeight) { canvas ->
            background?.let {
                it.setBounds(0, 0, frameWidth, frameHeight)
                it.draw(canvas)
            }
            val srcRect = Rect()
            val destRect = Rect()
            frameMethod.computeBounds(metadata, frameWidth, frameHeight, srcRect, destRect)
            canvas.drawStream(region(srcRect), destRect, Paint(Paint.FILTER_BITMAP_FLAG))
        }
    }

    internal open fun downsampleOnly(): BitmapStream {
        return this
    }

    internal fun postProcess(bitmap: Bitmap?, params: DecodingParameters): Bitmap? {
        return if (bitmap == null || params.postScaleX == 1f && params.postScaleY == 1f) {
            bitmap
        } else {
            val newWidth = (bitmap.width * params.postScaleX).roundToInt()
            val newHeight = (bitmap.height * params.postScaleY).roundToInt()
            Bitmap.createBitmap(newWidth, newHeight, bitmap.config).also {
                val c = Canvas(it)
                c.drawBitmap(
                    bitmap,
                    null,
                    Rect(0, 0, newWidth, newHeight),
                    Paint(Paint.FILTER_BITMAP_FLAG)
                )
                it.density = bitmap.density
                it.setHasAlpha(bitmap.hasAlpha())
                it.isPremultiplied = bitmap.isPremultiplied
                bitmap.recycle()
            }
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
        fun create(assetManager: AssetManager, path: String): BitmapStream {
            return FactorySourceBitmapStream(AssetBitmapSource(assetManager, path))
        }

        @JvmStatic
        fun create(array: ByteArray): BitmapStream {
            return create(array, 0, array.size)
        }

        @JvmStatic
        fun create(array: ByteArray, offset: Int, length: Int): BitmapStream {
            return FactorySourceBitmapStream(ByteArrayBitmapSource(array, offset, length))
        }

        @JvmStatic
        fun create(file: File): BitmapStream {
            return FactorySourceBitmapStream(FileBitmapSource(file))
        }

        @JvmStatic
        fun create(res: Resources, @DrawableRes id: Int): BitmapStream {
            return FactorySourceBitmapStream(ResourceBitmapSource(res, id))
        }

        @JvmStatic
        fun create(inputStream: InputStream): BitmapStream {
            return FactorySourceBitmapStream(InputStreamBitmapSource(inputStream))
        }

        @JvmStatic
        fun create(inputStreamFactory: InputStreamFactory): BitmapStream {
            return FactorySourceBitmapStream(InputStreamFactoryBitmapSource(inputStreamFactory))
        }

        @JvmStatic
        fun create(d: Drawable): BitmapStream {
            return CanvasBitmapStream(d.intrinsicWidth, d.intrinsicHeight) {
                d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
                d.draw(it)
            }
        }
    }
}