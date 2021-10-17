package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapSize
import kotlin.math.min

internal class MatrixFrameMethod : FrameMethod {
    override fun computeBounds(
        size: BitmapSize,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect,
        outDest: Rect
    ) {
        val width = min(size.width, frameWidth)
        val height = min(size.height, frameHeight)
        outSrc.set(0, 0, width, height)
        outDest.set(0, 0, width, height)
    }
}