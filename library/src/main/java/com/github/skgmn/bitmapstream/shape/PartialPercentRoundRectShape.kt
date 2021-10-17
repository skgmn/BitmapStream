package com.github.skgmn.bitmapstream.shape

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.min

internal class PartialPercentRoundRectShape(
    private val topLeftPercent: Float,
    private val topRightPercent: Float,
    private val bottomLeftPercent: Float,
    private val bottomRightPercent: Float
) : Shape {
    override fun draw(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int, paint: Paint) {
        val minSize = min(right - left, bottom - top)
        val topLeftRadius = minSize * topLeftPercent
        val topRightRadius = minSize * topRightPercent
        val bottomLeftRadius = minSize * bottomLeftPercent
        val bottomRightRadius = minSize * bottomRightPercent

        PartialRoundRectShape.drawPartialRoundRect(
            canvas,
            left,
            top,
            right,
            bottom,
            topLeftRadius,
            topRightRadius,
            bottomLeftRadius,
            bottomRightRadius,
            paint
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PartialPercentRoundRectShape) return false

        if (topLeftPercent != other.topLeftPercent) return false
        if (topRightPercent != other.topRightPercent) return false
        if (bottomLeftPercent != other.bottomLeftPercent) return false
        if (bottomRightPercent != other.bottomRightPercent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topLeftPercent.hashCode()
        result = 31 * result + topRightPercent.hashCode()
        result = 31 * result + bottomLeftPercent.hashCode()
        result = 31 * result + bottomRightPercent.hashCode()
        return result
    }
}