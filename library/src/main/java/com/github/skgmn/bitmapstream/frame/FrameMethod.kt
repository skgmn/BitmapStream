package com.github.skgmn.bitmapstream.frame

import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata

interface FrameMethod {
    fun computeBounds(
        metadata: BitmapMetadata,
        frameWidth: Int,
        frameHeight: Int,
        outSrc: Rect,
        outDest: Rect
    )

    companion object {
        val CENTER_CROP: FrameMethod = CenterCropFrameMethod()
        val CENTER: FrameMethod = CenterFrameMethod()
        val CENTER_INSIDE: FrameMethod = CenterInsideFrameMethod()
        val FIT_XY: FrameMethod = FitXYFrameMethod()
        val MATRIX: FrameMethod = MatrixFrameMethod()

        fun fit(gravity: FitGravity): FrameMethod {
            return FitGravityFrameMethod(gravity)
        }
    }
}