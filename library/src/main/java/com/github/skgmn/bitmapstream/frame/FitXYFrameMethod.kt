package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapSize

internal class FitXYFrameMethod : FrameMethod {
    override fun computeBounds(
        size: BitmapSize,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect,
        outDest: Rect
    ) {
        outSrc.set(0, 0, size.width, size.height)
        outDest.set(0, 0, frameWidth, frameHeight)
    }
}