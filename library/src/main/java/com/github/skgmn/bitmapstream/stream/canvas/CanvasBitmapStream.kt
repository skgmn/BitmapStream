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
    private val draw: DrawScope.() -> Unit
) : BitmapStream() {
    private val regionWidth get() = regionRight - regionLeft
    private val regionHeight get() = regionBottom - regionTop

    override val metadata = object : BitmapMetadata {
        override val width by lazy(LazyThreadSafetyMode.NONE) {
            (regionWidth * scaleX).roundToInt()
        }
        override val height by lazy(LazyThreadSafetyMode.NONE) {
            (regionHeight * scaleY).roundToInt()
        }
        override val mimeType get() = "image/bmp"
        override val densityScale get() = 1f
    }

    override val features = object : StreamFeatures {
        override val regional
            get() = regionLeft != 0 || regionTop != 0 ||
                    regionRight != canvasWidth || regionBottom != canvasHeight
        override val mutable: Boolean? get() = null
        override val hardware get() = false
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (width == metadata.width && height == metadata.height) {
            this
        } else {
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                regionLeft,
                regionTop,
                regionRight,
                regionBottom,
                width / regionWidth.toFloat(),
                height / regionHeight.toFloat(),
                draw
            )
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (width == metadata.width) {
            this
        } else {
            val scale = width.toFloat() / metadata.width
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                regionLeft,
                regionTop,
                regionRight,
                regionBottom,
                scaleX * scale,
                scaleY * scale,
                draw
            )
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (height == metadata.height) {
            this
        } else {
            val scale = height.toFloat() / metadata.height
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                regionLeft,
                regionTop,
                regionRight,
                regionBottom,
                scaleX * scale,
                scaleY * scale,
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
                draw
            )
        }
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return if (left == 0 && top == 0 && right == metadata.width && bottom == metadata.height) {
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
}