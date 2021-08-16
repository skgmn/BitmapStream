package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class CenterCropFrameMethod : FrameMethod {
    override fun computeBounds(
        bitmapStream: BitmapStream,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect?,
        outDest: Rect?
    ) {
        val width = bitmapStream.width
        val height = bitmapStream.height
        val targetWidth: Int
        var targetHeight: Int = AspectRatioCalculator.getHeight(width, height, frameWidth)
        if (targetHeight >= frameHeight) {
            targetWidth = frameWidth
        } else {
            targetWidth = AspectRatioCalculator.getWidth(width, height, frameHeight)
            targetHeight = frameHeight
        }
        val targetLeft = (frameWidth - targetWidth) / 2
        val targetTop = (frameHeight - targetHeight) / 2
        val ratioWidth = targetWidth.toFloat() / width
        val ratioHeight = targetHeight.toFloat() / height
        if (outSrc != null) {
            outSrc.left = Math.round(-targetLeft / ratioWidth)
            outSrc.top = Math.round(-targetTop / ratioHeight)
            outSrc.right = outSrc.left + Math.round(frameWidth / ratioWidth)
            outSrc.bottom = outSrc.top + Math.round(frameHeight / ratioHeight)
        }
        outDest?.set(0, 0, frameWidth, frameHeight)
    }
}