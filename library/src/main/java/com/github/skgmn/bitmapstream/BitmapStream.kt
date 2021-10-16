package com.github.skgmn.bitmapstream

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.github.skgmn.bitmapstream.frame.*
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.source.*
import com.github.skgmn.bitmapstream.stream.canvas.CanvasBitmapStream
import com.github.skgmn.bitmapstream.stream.inmemory.InMemoryBitmapStream
import com.github.skgmn.bitmapstream.stream.source.BitmapFactoryBitmapStream
import com.github.skgmn.bitmapstream.stream.transform.HardwareTransformBitmapStream
import com.github.skgmn.bitmapstream.stream.transform.MutableTransformBitmapStream
import java.io.File
import java.io.InputStream

abstract class BitmapStream {
    abstract val metadata: BitmapMetadata

    internal open val features = object : StreamFeatures {
        override val regional get() = false
        override val hardware get() = false
        override val mutable get() = null
    }

    abstract fun scaleTo(width: Int, height: Int): BitmapStream
    abstract fun scaleWidth(width: Int): BitmapStream
    abstract fun scaleHeight(height: Int): BitmapStream
    abstract fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream
    abstract fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream

    abstract fun decode(): Bitmap?

    fun region(bounds: Rect): BitmapStream {
        return region(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    open fun mutable(mutable: Boolean?): BitmapStream {
        return if (mutable == null) {
            this
        } else {
            MutableTransformBitmapStream(this, mutable)
        }
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
        val features = features
        return CanvasBitmapStream(frameWidth, frameHeight) {
            background?.let {
                draw(it, 0, 0, frameWidth, frameHeight)
            }
            val srcRect = Rect()
            val destRect = Rect()
            frameMethod.computeBounds(metadata, frameWidth, frameHeight, srcRect, destRect)
            draw(region(srcRect), destRect, Paint(Paint.FILTER_BITMAP_FLAG))
        }.hardware(features.hardware).mutable(features.mutable)
    }

    open fun hardware(hardware: Boolean): BitmapStream {
        return if (hardware) HardwareTransformBitmapStream(this) else this
    }

    internal open fun downsampleOnly(): BitmapStream {
        return this
    }

    companion object {
        @JvmStatic
        fun create(assetManager: AssetManager, path: String): BitmapStream {
            return BitmapFactoryBitmapStream(AssetBitmapSource(assetManager, path))
        }

        @JvmStatic
        fun create(array: ByteArray): BitmapStream {
            return create(array, 0, array.size)
        }

        @JvmStatic
        fun create(array: ByteArray, offset: Int, length: Int): BitmapStream {
            return BitmapFactoryBitmapStream(ByteArrayBitmapSource(array, offset, length))
        }

        @JvmStatic
        fun create(file: File): BitmapStream {
            return BitmapFactoryBitmapStream(FileBitmapSource(file))
        }

        @JvmStatic
        fun create(res: Resources, @DrawableRes id: Int): BitmapStream {
            return BitmapFactoryBitmapStream(ResourceBitmapSource(res, id))
        }

        @JvmStatic
        fun create(inputStream: InputStream): BitmapStream {
            return BitmapFactoryBitmapStream(InputStreamBitmapSource(inputStream))
        }

        @JvmStatic
        fun create(inputStreamFactory: InputStreamFactory): BitmapStream {
            return BitmapFactoryBitmapStream(InputStreamFactoryBitmapSource(inputStreamFactory))
        }

        @JvmStatic
        fun create(bitmap: Bitmap): BitmapStream {
            return InMemoryBitmapStream(bitmap)
        }

        @JvmStatic
        fun create(d: Drawable): BitmapStream {
            return CanvasBitmapStream(d.intrinsicWidth, d.intrinsicHeight) {
                draw(d)
            }
        }
    }
}