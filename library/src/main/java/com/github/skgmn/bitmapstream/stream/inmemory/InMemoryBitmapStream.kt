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
        override val width get() = exactWidth.roundToInt()
        override val height get() = exactHeight.roundToInt()
        override val mimeType get() = "image/bmp"
    }

    private val exactWidth by lazy(LazyThreadSafetyMode.NONE) {
        (right - left) * scaleX
    }
    private val exactHeight by lazy(LazyThreadSafetyMode.NONE) {
        (bottom - top) * scaleY
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (width.toFloat() == exactWidth && height.toFloat() == exactHeight) {
            this
        } else {
            val sx = width / exactWidth
            val sy = height / exactHeight
            InMemoryBitmapStream(bitmap, left, top, right, bottom, scaleX * sx, scaleY * sy)
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (width.toFloat() == exactWidth) {
            this
        } else {
            val scale = width / exactWidth
            InMemoryBitmapStream(bitmap, left, top, right, bottom, scaleX * scale, scaleY * scale)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (height.toFloat() == exactHeight) {
            this
        } else {
            val scale = height / exactHeight
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
        return if (left == 0 && top == 0 &&
            right.toFloat() == exactWidth && bottom.toFloat() == exactHeight
        ) {
            this
        } else {
            InMemoryBitmapStream(
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InMemoryBitmapStream) return false

        if (bitmap != other.bitmap) return false
        if (left != other.left) return false
        if (top != other.top) return false
        if (right != other.right) return false
        if (bottom != other.bottom) return false
        if (scaleX != other.scaleX) return false
        if (scaleY != other.scaleY) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bitmap.hashCode()
        result = 31 * result + left
        result = 31 * result + top
        result = 31 * result + right
        result = 31 * result + bottom
        result = 31 * result + scaleX.hashCode()
        result = 31 * result + scaleY.hashCode()
        return result
    }
}