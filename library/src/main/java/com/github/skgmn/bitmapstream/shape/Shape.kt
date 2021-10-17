package com.github.skgmn.bitmapstream.shape

import android.graphics.Canvas
import android.graphics.Paint

fun interface Shape {
    fun draw(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int, paint: Paint)

    companion object {
        val OVAL: Shape = OvalShape()

        fun roundRect(radius: Float): Shape = RoundRectShape(radius)
        fun roundRect(
            topLeftRadius: Float,
            topRightRadius: Float,
            bottomLeftRadius: Float,
            bottomRightRadius: Float
        ): Shape = if (topLeftRadius == topRightRadius &&
            topRightRadius == bottomLeftRadius &&
            bottomLeftRadius == bottomRightRadius
        ) {
            RoundRectShape(topLeftRadius)
        } else {
            PartialRoundRectShape(
                topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius
            )
        }

        fun percentRoundRect(percent: Float): Shape = PercentRoundRectShape(percent)
        fun percentRoundRect(
            topLeftPercent: Float,
            topRightPercent: Float,
            bottomLeftPercent: Float,
            bottomRightPercent: Float
        ): Shape = if (topLeftPercent == topRightPercent &&
            topRightPercent == bottomLeftPercent &&
            bottomLeftPercent == bottomRightPercent
        ) {
            PercentRoundRectShape(topLeftPercent)
        } else {
            PartialPercentRoundRectShape(
                topLeftPercent, topRightPercent, bottomLeftPercent, bottomRightPercent
            )
        }
    }
}