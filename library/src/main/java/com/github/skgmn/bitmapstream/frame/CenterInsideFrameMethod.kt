package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class CenterInsideFrameMethod : FrameMethod {
    override fun computeBounds(
        bitmapStream: BitmapStream,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect?,
        outDest: Rect?
    ) {
        val width = bitmapStream.width
        val height = bitmapStream.height
        outSrc?.set(0, 0, width, height)
        if (outDest != null) {
            if (width <= frameWidth && height <= frameHeight) {
                outDest.left = (frameWidth - width) / 2
                outDest.top = (frameHeight - height) / 2
                outDest.right = outDest.left + width
                outDest.bottom = outDest.top + height
            } else {
                val targetWidth: Int
                var targetHeight: Int = AspectRatioCalculator.getHeight(width, height, frameWidth)
                if (targetHeight <= frameHeight) {
                    targetWidth = frameWidth
                } else {
                    targetWidth = AspectRatioCalculator.getWidth(width, height, frameHeight)
                    targetHeight = frameHeight
                }
                outDest.left = (frameWidth - targetWidth) / 2
                outDest.top = (frameHeight - targetHeight) / 2
                outDest.right = outDest.left + targetWidth
                outDest.bottom = outDest.top + targetHeight
            }
        }
    }
}