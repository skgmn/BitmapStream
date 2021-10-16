package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class CenterInsideFrameMethod : FrameMethod {
    override fun computeBounds(
        metadata: BitmapMetadata,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect,
        outDest: Rect
    ) {
        val width = metadata.width
        val height = metadata.height
        outSrc.set(0, 0, width, height)
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