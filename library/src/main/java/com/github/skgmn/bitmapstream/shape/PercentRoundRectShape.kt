package com.github.skgmn.bitmapstream.shape

import com.github.skgmn.bitmapstream.stream.canvas.DrawPaint
import com.github.skgmn.bitmapstream.stream.canvas.DrawScope
import kotlin.math.min

internal class PercentRoundRectShape(
    private val percent: Float
) : Shape {
    override fun DrawScope.draw(left: Int, top: Int, right: Int, bottom: Int, paint: DrawPaint) {
        val minSize = min(right - left, bottom - top)
        val radius = minSize * percent
        drawRoundRect(left, top, right, bottom, radius, radius, paint)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PercentRoundRectShape) return false

        if (percent != other.percent) return false

        return true
    }

    override fun hashCode(): Int {
        return percent.hashCode()
    }
}