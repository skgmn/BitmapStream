package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import kotlin.math.min

internal class MatrixFrameMethod : FrameMethod {
    override fun computeBounds(
        metadata: BitmapMetadata,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect?,
        outDest: Rect?
    ) {
        val width = min(metadata.width, frameWidth)
        val height = min(metadata.height, frameHeight)
        outSrc?.set(0, 0, width, height)
        outDest?.set(0, 0, width, height)
    }
}