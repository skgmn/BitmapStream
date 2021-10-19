package com.github.skgmn.bitmapstream.stream.lazy

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.shape.Shape
import com.github.skgmn.bitmapstream.stream.canvas.CanvasBitmapStream
import com.github.skgmn.bitmapstream.stream.canvas.DrawPaint

internal class ShapeBitmapStream(
    private val other: BitmapStream,
    private val shape: Shape
) : LazyBitmapStream() {
    override val size get() = other.size
    override val simulatedWidth get() = other.size.width.toDouble()
    override val simulatedHeight get() = other.size.height.toDouble()

    override fun shape(shape: Shape): BitmapStream {
        return if (this.shape == shape) {
            this
        } else {
            super.shape(shape)
        }
    }

    override fun buildStream(): BitmapStream {
        return CanvasBitmapStream(
            canvasWidth = other.size.width,
            canvasHeight = other.size.height,
            key = CanvasKey(other, shape)
        ) {
            val p = DrawPaint(antialias = true)
            p.color = Color.WHITE
            p.style = Paint.Style.FILL
            with(shape) { draw(0, 0, width, height, p) }

            p.porterDuffMode = PorterDuff.Mode.SRC_IN
            draw(other, 0, 0, width, height, p)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShapeBitmapStream) return false

        if (other != other.other) return false
        if (shape != other.shape) return false

        return true
    }

    override fun hashCode(): Int {
        var result = other.hashCode()
        result = 31 * result + shape.hashCode()
        return result
    }

    private data class CanvasKey(
        val stream: BitmapStream,
        val shape: Shape
    )
}