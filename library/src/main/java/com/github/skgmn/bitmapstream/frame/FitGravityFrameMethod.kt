package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator

internal class FitGravityFrameMethod(
    private val gravity: Int
) : FrameMethod {
    override fun computeBounds(
        metadata: BitmapMetadata,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect?,
        outDest: Rect?
    ) {
        val width = metadata.width
        val height = metadata.height
        outSrc?.set(0, 0, width, height)
        if (outDest != null) {
            val targetWidth: Int
            var targetHeight: Int = AspectRatioCalculator.getHeight(width, height, frameWidth)
            if (targetHeight <= frameHeight) {
                targetWidth = frameWidth
            } else {
                targetWidth = AspectRatioCalculator.getWidth(width, height, frameHeight)
                targetHeight = frameHeight
            }
            when (gravity) {
                GRAVITY_START -> outDest.set(0, 0, targetWidth, targetHeight)
                GRAVITY_CENTER -> {
                    outDest.left = (frameWidth - targetWidth) / 2
                    outDest.top = (frameHeight - targetHeight) / 2
                    outDest.right = outDest.left + targetWidth
                    outDest.bottom = outDest.top + targetHeight
                }
                GRAVITY_END -> {
                    outDest.right = frameWidth
                    outDest.bottom = frameHeight
                    outDest.left = outDest.right - targetWidth
                    outDest.top = outDest.bottom - targetHeight
                }
                else -> outDest[0, 0, targetWidth] = targetHeight
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
        return gravity
    }

    companion object {
        const val GRAVITY_START = 0
        const val GRAVITY_CENTER = 1
        const val GRAVITY_END = 2
    }
}