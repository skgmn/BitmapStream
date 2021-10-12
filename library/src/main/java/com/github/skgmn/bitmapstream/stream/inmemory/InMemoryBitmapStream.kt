package com.github.skgmn.bitmapstream.stream.inmemory

import android.graphics.Bitmap
import android.graphics.Matrix
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import kotlin.math.roundToInt

internal class InMemoryBitmapStream(
    private val bitmap: Bitmap,
    private val left: Int = 0,
    private val top: Int = 0,
    private val right: Int = bitmap.width,
    private val bottom: Int = bitmap.height,
    private val scaleX: Float = 1f,
    private val scaleY: Float = 1f
) : BitmapStream() {
    override val metadata = object : BitmapMetadata {
        override val width by lazy(LazyThreadSafetyMode.NONE) {
            ((right - left) * scaleX).roundToInt()
        }
        override val height by lazy(LazyThreadSafetyMode.NONE) {
            ((bottom - top) * scaleY).roundToInt()
        }
        override val mimeType get() = "image/bmp"
        override val densityScale get() = 1f
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (width == metadata.width && height == metadata.height) {
            this
        } else {
            val sx = width.toFloat() / metadata.width
            val sy = height.toFloat() / metadata.height
            InMemoryBitmapStream(bitmap, left, top, right, bottom, scaleX * sx, scaleY * sy)
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (width == metadata.width) {
            this
        } else {
            val scale = width.toFloat() / metadata.width
            InMemoryBitmapStream(bitmap, left, top, right, bottom, scaleX * scale, scaleY * scale)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (height == metadata.height) {
            this
        } else {
            val scale = height.toFloat() / metadata.height
            InMemoryBitmapStream(bitmap, left, top, right, bottom, scaleX * scale, scaleY * scale)
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            InMemoryBitmapStream(
                bitmap,
                left,
                top,
                right,
                bottom,
                scaleX * scaleWidth,
                scaleY * scaleHeight
            )
        }
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        if (left == 0 && top == 0 && right == metadata.width && bottom == metadata.height) {
            return this
        } else {
            return InMemoryBitmapStream(
                bitmap,
                (this.left + left / scaleX).roundToInt(),
                (this.top + top / scaleY).roundToInt(),
                (this.left + right / scaleX).roundToInt(),
                (this.top + bottom / scaleY).roundToInt(),
                scaleX,
                scaleY
            )
        }
    }

    override fun mutable(mutable: Boolean?): BitmapStream {
        TODO("Not yet implemented")
    }

    override fun decode(): Bitmap {
        val noScale = scaleX == 1f && scaleY == 1f
        return if (left == 0 && top == 0 &&
            right == bitmap.width && bottom == bitmap.height &&
            noScale
        ) {
            bitmap
        } else {
            val m = if (noScale) null else Matrix().apply { setScale(scaleX, scaleY) }
            val filterBitmap = !noScale
            Bitmap.createBitmap(
                bitmap,
                left,
                top,
                right - left,
                bottom - top,
                m,
                filterBitmap
            )
        }
    }
}