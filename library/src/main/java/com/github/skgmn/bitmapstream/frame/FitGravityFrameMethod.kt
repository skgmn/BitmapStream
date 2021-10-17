package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapSize
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class FitGravityFrameMethod(
    private val gravity: FitGravity
) : FrameMethod {
    override fun computeBounds(
        size: BitmapSize,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect,
        outDest: Rect
    ) {
        val width = size.width
        val height = size.height
        outSrc.set(0, 0, width, height)
        val targetWidth: Int
        var targetHeight: Int = AspectRatioCalculator.getHeight(width, height, frameWidth)
        if (targetHeight <= frameHeight) {
            targetWidth = frameWidth
        } else {
            targetWidth = AspectRatioCalculator.getWidth(width, height, frameHeight)
            targetHeight = frameHeight
        }
        when (gravity) {
            FitGravity.START -> outDest.set(0, 0, targetWidth, targetHeight)
            FitGravity.CENTER -> {
                outDest.left = (frameWidth - targetWidth) / 2
                outDest.top = (frameHeight - targetHeight) / 2
                outDest.right = outDest.left + targetWidth
                outDest.bottom = outDest.top + targetHeight
            }
            FitGravity.END -> {
                outDest.right = frameWidth
                outDest.bottom = frameHeight
                outDest.left = outDest.right - targetWidth
                outDest.top = outDest.bottom - targetHeight
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FitGravityFrameMethod) return false

        if (gravity != other.gravity) return false

        return true
    }

    override fun hashCode(): Int {
        return gravity.hashCode()
    }
}