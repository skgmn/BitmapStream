package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import kotlin.math.roundToInt

internal class CanvasBitmapStream(
    private val canvasWidth: Int,
    private val canvasHeight: Int,
    private val region: Rect = Rect(0, 0, canvasWidth, canvasHeight),
    private val scaleX: Float = 1f,
    private val scaleY: Float = 1f,
    private val mutable: Boolean? = null,
    private val drawer: Canvas.() -> Unit
) : BitmapStream() {
    override val metadata = object : BitmapMetadata {
        override val width by lazy { (region.width() * scaleX).roundToInt() }
        override val height by lazy { (region.height() * scaleY).roundToInt() }
        override val mimeType get() = "image/bmp"
        override val densityScale get() = 1f
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (width == metadata.width && height == metadata.height) {
            this
        } else {
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                region,
                width / region.width().toFloat(),
                height / region.height().toFloat(),
                mutable,
                drawer
            )
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (width == metadata.width) {
            this
        } else {
            val scaleX = width / region.width().toFloat()
            CanvasBitmapStream(canvasWidth, canvasHeight, region, scaleX, scaleY, mutable, drawer)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (height == metadata.height) {
            this
        } else {
            val scaleY = height / region.height().toFloat()
            CanvasBitmapStream(canvasWidth, canvasHeight, region, scaleY, scaleY, mutable, drawer)
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                region,
                scaleX * scaleWidth,
                scaleY * scaleHeight,
                mutable,
                drawer
            )
        }
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return if (left == 0 && top == 0 && right == region.width() && bottom == region.height()) {
            this
        } else {
            CanvasBitmapStream(
                canvasWidth,
                canvasHeight,
                Rect(
                    region.left + (left / scaleX).roundToInt(),
                    region.top + (top / scaleY).roundToInt(),
                    region.left + (right / scaleX).roundToInt(),
                    region.top + (bottom / scaleY).roundToInt()
                ),
                scaleX,
                scaleY,
                mutable,
                drawer
            )
        }
    }

    override fun mutable(mutable: Boolean?): BitmapStream {
        return if (this.mutable == mutable) {
            this
        } else {
            CanvasBitmapStream(
                canvasWidth, canvasHeight, region, scaleX, scaleY, mutable, drawer
            )
        }
    }

    override fun decode(): Bitmap? {
        val canvasRecorder = CanvasRecorder(metadata.width, metadata.height)
        canvasRecorder.translate(-region.left * scaleX, -region.top * scaleY)
        canvasRecorder.scale(scaleX, scaleY)
        canvasRecorder.clipRect(region)
        with(canvasRecorder) { drawer() }
        canvasRecorder.runDeferred()

        val bitmap = Bitmap.createBitmap(metadata.width, metadata.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvasRecorder.drawTo(canvas)

        return bitmap
    }
}