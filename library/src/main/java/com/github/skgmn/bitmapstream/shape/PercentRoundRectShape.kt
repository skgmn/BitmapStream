package com.github.skgmn.bitmapstream.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.min

internal class PercentRoundRectShape(
    private val percent: Float
) : Shape {
    override fun draw(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int, paint: Paint) {
        val minSize = min(right - left, bottom - top)
        val radius = minSize * percent
        canvas.drawRoundRect(
            RectF(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat()
            ), radius, radius, paint
        )
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