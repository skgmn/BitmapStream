package com.github.skgmn.bitmapstream.stream

import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters
import kotlin.math.roundToInt

internal class RegionBitmapStream(
    other: BitmapStream,
    private val left: Int,
    private val top: Int,
    private val right: Int,
    private val bottom: Int
) : DelegateBitmapStream(other) {
    override val width: Int
        get() = right - left
    override val height: Int
        get() = bottom - top

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return if (left == 0 && top == 0 && right == width && bottom == height) {
            this
        } else {
            val newLeft = this.left + left
            val newTop = this.top + top
            RegionBitmapStream(
                other,
                newLeft,
                newTop,
                newLeft + (right - left),
                newTop + (bottom - top)
            )
        }
    }

    override fun buildInputParameters(regional: Boolean): InputParameters {
        return other.buildInputParameters(true).apply {
            val left = (region?.left ?: 0) + (left / scaleX).roundToInt()
            val top = (region?.top ?: 0) + (top / scaleY).roundToInt()
            val right = left + (width / scaleX).roundToInt()
            val bottom = top + (height / scaleY).roundToInt()

            val scaledRegion = region ?: Rect().also { region = it }
            scaledRegion.left = left
            scaledRegion.top = top
            scaledRegion.right = right
            scaledRegion.bottom = bottom

            scaleX = width.toFloat() / scaledRegion.width()
            scaleY = height.toFloat() / scaledRegion.height()
        }
    }
}