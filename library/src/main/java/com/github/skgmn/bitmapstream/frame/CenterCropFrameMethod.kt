package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapSize
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator
import kotlin.math.roundToInt

internal class CenterCropFrameMethod : FrameMethod {
    override fun computeBounds(
        size: BitmapSize,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect,
        outDest: Rect
    ) {
        val width = size.width
        val height = size.height
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

        outSrc.left = (-targetLeft / ratioWidth).roundToInt()
        outSrc.top = (-targetTop / ratioHeight).roundToInt()
        outSrc.right = outSrc.left + (frameWidth / ratioWidth).roundToInt()
        outSrc.bottom = outSrc.top + (frameHeight / ratioHeight).roundToInt()

        outDest.set(0, 0, frameWidth, frameHeight)
    }
}