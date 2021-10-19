package com.github.skgmn.bitmapstream.shape

import android.graphics.Path
import android.graphics.RectF
import com.github.skgmn.bitmapstream.stream.canvas.DrawPaint
import com.github.skgmn.bitmapstream.stream.canvas.DrawScope

internal class PartialRoundRectShape(
    private val topLeftRadius: Float,
    private val topRightRadius: Float,
    private val bottomLeftRadius: Float,
    private val bottomRightRadius: Float
) : Shape {
    override fun DrawScope.draw(left: Int, top: Int, right: Int, bottom: Int, paint: DrawPaint) {
        drawPartialRoundRect(
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
        if (other !is PartialRoundRectShape) return false

        if (topLeftRadius != other.topLeftRadius) return false
        if (topRightRadius != other.topRightRadius) return false
        if (bottomLeftRadius != other.bottomLeftRadius) return false
        if (bottomRightRadius != other.bottomRightRadius) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topLeftRadius.hashCode()
        result = 31 * result + topRightRadius.hashCode()
        result = 31 * result + bottomLeftRadius.hashCode()
        result = 31 * result + bottomRightRadius.hashCode()
        return result
    }

    companion object {
        internal fun DrawScope.drawPartialRoundRect(
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            topLeftRadius: Float,
            topRightRadius: Float,
            bottomLeftRadius: Float,
            bottomRightRadius: Float,
            paint: DrawPaint
        ) {
            val rectF = RectF()
            val l = left.toFloat()
            val t = top.toFloat()
            val r = right.toFloat()
            val b = bottom.toFloat()

            val path = Path()
            path.moveTo(l + topLeftRadius, t)
            path.lineTo(r - topRightRadius, t)

            rectF.set(r - topRightRadius * 2, t, r, t + topRightRadius * 2)
            path.arcTo(rectF, -90f, 90f)

            path.lineTo(r, b - bottomRightRadius)

            rectF.set(r - bottomRightRadius * 2, b - bottomRightRadius * 2, r, b)
            path.arcTo(rectF, 0f, 90f)

            path.lineTo(l + bottomLeftRadius, b)

            rectF.set(l, b - bottomLeftRadius * 2, l + bottomLeftRadius * 2, b)
            path.arcTo(rectF, 90f, 90f)

            path.lineTo(l, t + topLeftRadius)

            rectF.set(l, t, l + topLeftRadius * 2, t + topLeftRadius * 2)
            path.arcTo(rectF, 180f, 90f)

            path.close()

            drawPath(path, paint)
        }
    }
}