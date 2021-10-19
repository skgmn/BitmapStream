package com.github.skgmn.bitmapstream.shape

import com.github.skgmn.bitmapstream.stream.canvas.DrawPaint
import com.github.skgmn.bitmapstream.stream.canvas.DrawScope

internal class OvalShape : Shape {
    override fun DrawScope.draw(left: Int, top: Int, right: Int, bottom: Int, paint: DrawPaint) {
        drawOval(left, top, right, bottom, paint)
    }
}