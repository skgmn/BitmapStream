package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream
import kotlin.math.min

internal class MatrixFrameMethod : FrameMethod {
    override fun computeBounds(
        bitmapStream: BitmapStream,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect?,
        outDest: Rect?
    ) {
        val width = min(bitmapStream.width, frameWidth)
        val height = min(bitmapStream.height, frameHeight)
        outSrc?.set(0, 0, width, height)
        outDest?.set(0, 0, width, height)
    }
}