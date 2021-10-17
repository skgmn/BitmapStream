package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import kotlin.math.roundToInt

internal class CanvasBitmapStream(
    private val canvasWidth: Int,
    private val canvasHeight: Int,
    private val regionLeft: Int = 0,
    private val regionTop: Int = 0,
    private val regionRight: Int = canvasWidth,
    private val regionBottom: Int = canvasHeight,
    private val scaleX: Float = 1f,
    private val scaleY: Float = 1f,
    private val key: Any? = null,
    private val draw: DrawScope.() -> Unit
) : BitmapStream() {
    override val metadata = object : BitmapMetadata {
        override val width get() = exactWidth.roundToInt()
        override val height get() = exactHeight.roundToInt()
        override val mimeType get() = "image/bmp"
    }

    private val exactWidth by lazy(LazyThreadSafetyMode.NONE) {
        (regionRight - regionLeft) * scaleX
    }
    private val exactHeight by lazy(LazyThreadSafetyMode.NONE) {
        (regionBottom - regionTop) * scaleY
    }

    override val features = object : StreamFeatures {
        override val regional
            get() = regionLeft != 0 || regionTop != 0 ||
                    regionRight != canvasWidth || regionBottom != canvasHeight
        override val mutable: Boolean? get() = null
        override val hardware get() = false
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (width.toFloat() == exactWidth && height.toFloat() == exactHeight) {
            this
        } else {
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                regionLeft,
                regionTop,
                regionRight,
                regionBottom,
                width / exactWidth,
                height / exactHeight,
                key,
                draw
            )
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (width.toFloat() == exactWidth) {
            this
        } else {
            val scale = width / exactWidth
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                regionLeft,
                regionTop,
                regionRight,
                regionBottom,
                scaleX * scale,
                scaleY * scale,
                null,
                draw
            )
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (height.toFloat() == exactHeight) {
            this
        } else {
            val scale = height / exactHeight
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                regionLeft,
                regionTop,
                regionRight,
                regionBottom,
                scaleX * scale,
                scaleY * scale,
                null,
                draw
            )
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                regionLeft,
                regionTop,
                regionRight,
                regionBottom,
                scaleX * scaleWidth,
                scaleY * scaleHeight,
                null,
                draw
            )
        }
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return if (left == 0 && top == 0 &&
            right.toFloat() == exactWidth && bottom.toFloat() == exactHeight
        ) {
            this
        } else {
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                (regionLeft + left / scaleX).roundToInt(),
                (regionTop + top / scaleY).roundToInt(),
                (regionLeft + right / scaleX).roundToInt(),
                (regionTop + bottom / scaleY).roundToInt(),
                scaleX,
                scaleY,
                null,
                draw
            )
        }
    }

    override fun decode(): Bitmap {
        val drawer = BitmapDrawer(
            canvasWidth,
            canvasHeight,
            regionLeft, regionTop, regionRight, regionBottom,
            scaleX, scaleY
        )
        drawer.draw()
        return drawer.makeBitmap()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CanvasBitmapStream) return false
        if (key == null) return false

        if (canvasWidth != other.canvasWidth) return false
        if (canvasHeight != other.canvasHeight) return false
        if (regionLeft != other.regionLeft) return false
        if (regionTop != other.regionTop) return false
        if (regionRight != other.regionRight) return false
        if (regionBottom != other.regionBottom) return false
        if (scaleX != other.scaleX) return false
        if (scaleY != other.scaleY) return false
        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        if (key == null) return super.hashCode()

        var result = canvasWidth
        result = 31 * result + canvasHeight
        result = 31 * result + regionLeft
        result = 31 * result + regionTop
        result = 31 * result + regionRight
        result = 31 * result + regionBottom
        result = 31 * result + scaleX.hashCode()
        result = 31 * result + scaleY.hashCode()
        result = 31 * result + key.hashCode()
        return result
    }
}