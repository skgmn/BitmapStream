package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream

internal class FitXYFrameMethod : FrameMethod {
    override fun computeBounds(
        bitmapStream: BitmapStream,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect?,
        outDest: Rect?
    ) {
        outSrc?.set(0, 0, bitmapStream.width, bitmapStream.height)
        outDest?.set(0, 0, frameWidth, frameHeight)
    }
}