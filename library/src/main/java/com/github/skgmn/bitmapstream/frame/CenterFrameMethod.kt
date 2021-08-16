package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream

internal class CenterFrameMethod : FrameMethod {
    override fun computeBounds(
        bitmapStream: BitmapStream,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect?,
        outDest: Rect?
    ) {
        val width = bitmapStream.width
        val height = bitmapStream.height
        if (width > frameWidth) {
            if (outSrc != null) {
                outSrc.left = (width - frameWidth) / 2
                outSrc.right = outSrc.left + frameWidth
            }
            if (outDest != null) {
                outDest.left = 0
                outDest.right = frameWidth
            }
        } else {
            if (outSrc != null) {
                outSrc.left = 0
                outSrc.right = width
            }
            if (outDest != null) {
                outDest.left = (frameWidth - width) / 2
                outDest.right = outDest.left + width
            }
        }
        if (height > frameHeight) {
            if (outSrc != null) {
                outSrc.top = (height - frameHeight) / 2
                outSrc.bottom = outSrc.top + frameHeight
            }
            if (outDest != null) {
                outDest.top = 0
                outDest.bottom = frameHeight
            }
        } else {
            if (outSrc != null) {
                outSrc.top = 0
                outSrc.bottom = height
            }
            if (outDest != null) {
                outDest.top = (frameHeight - height) / 2
                outDest.bottom = outDest.top + height
            }
        }
    }
}