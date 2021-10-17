package com.github.skgmn.bitmapstream.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

internal class RoundRectShape(
    private val radius: Float
) : Shape {
    override fun draw(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int, paint: Paint) {
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
        if (other !is RoundRectShape) return false

        if (radius != other.radius) return false

        return true
    }

    override fun hashCode(): Int {
        return radius.hashCode()
    }
}