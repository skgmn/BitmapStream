package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata

internal class CenterFrameMethod : FrameMethod {
    override fun computeBounds(
        metadata: BitmapMetadata,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect,
        outDest: Rect
    ) {
        val width = metadata.width
        val height = metadata.height
        if (width > frameWidth) {
            outSrc.left = (width - frameWidth) / 2
            outSrc.right = outSrc.left + frameWidth

            outDest.left = 0
            outDest.right = frameWidth
        } else {
            outSrc.left = 0
            outSrc.right = width

            outDest.left = (frameWidth - width) / 2
            outDest.right = outDest.left + width
        }
        if (height > frameHeight) {
            outSrc.top = (height - frameHeight) / 2
            outSrc.bottom = outSrc.top + frameHeight

            outDest.top = 0
            outDest.bottom = frameHeight
        } else {
            outSrc.top = 0
            outSrc.bottom = height

            outDest.top = (frameHeight - height) / 2
            outDest.bottom = outDest.top + height
        }
    }
}