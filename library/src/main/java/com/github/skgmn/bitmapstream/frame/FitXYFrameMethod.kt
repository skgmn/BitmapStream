package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata

internal class FitXYFrameMethod : FrameMethod {
    override fun computeBounds(
        metadata: BitmapMetadata,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect?,
        outDest: Rect?
    ) {
        outSrc?.set(0, 0, metadata.width, metadata.height)
        outDest?.set(0, 0, frameWidth, frameHeight)
    }
}