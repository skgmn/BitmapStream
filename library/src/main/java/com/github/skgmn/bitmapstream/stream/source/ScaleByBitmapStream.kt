package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class ScaleByBitmapStream(
    other: SourceBitmapStream,
    override val scaleX: Float,
    override val scaleY: Float
) : ScaleBitmapStream(other) {
    override val exactWidth by lazy(LazyThreadSafetyMode.NONE) { other.exactWidth * scaleX }
    override val exactHeight by lazy(LazyThreadSafetyMode.NONE) { other.exactHeight * scaleY }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return other.scaleTo(width, height)
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else {
            val sx = scaleX * scaleWidth
            val sy = scaleY * scaleHeight
            if (sx == 1f && sy == 1f) {
                other
            } else {
                other.scaleBy(sx, sy)
            }
        }
    }
}