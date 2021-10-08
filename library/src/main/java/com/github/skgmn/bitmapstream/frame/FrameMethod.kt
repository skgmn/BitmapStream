package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata

internal interface FrameMethod {
    fun computeBounds(
        metadata: BitmapMetadata,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect?,
        outDest: Rect?
    )
}