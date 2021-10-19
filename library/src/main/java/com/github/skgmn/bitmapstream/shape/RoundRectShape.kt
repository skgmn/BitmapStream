package com.github.skgmn.bitmapstream.shape

import com.github.skgmn.bitmapstream.stream.canvas.DrawPaint
import com.github.skgmn.bitmapstream.stream.canvas.DrawScope

internal class RoundRectShape(
    private val radius: Float
) : Shape {
    override fun DrawScope.draw(left: Int, top: Int, right: Int, bottom: Int, paint: DrawPaint) {
        drawRoundRect(left, top, right, bottom, radius, radius, paint)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RoundRectShape) return false

        if (radius != other.radius) return false

        return true
    }

    override fun hashCode(): Int {
        return radius.hashCode()
    }
}