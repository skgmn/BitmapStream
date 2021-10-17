package com.github.skgmn.bitmapstream.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

internal class OvalShape : Shape {
    override fun draw(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int, paint: Paint) {
        canvas.drawOval(
            RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat()),
            paint
        )
    }
}