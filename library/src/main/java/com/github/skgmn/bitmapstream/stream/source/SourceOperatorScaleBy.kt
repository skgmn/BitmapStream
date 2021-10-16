package com.github.skgmn.bitmapstream.stream.source

import com.github.skgmn.bitmapstream.BitmapStream

internal class SourceOperatorScaleBy(
    other: SourceBitmapStream,
    override val scaleX: Float,
    override val scaleY: Float
) : SourceOperatorScaleBase(other) {
    override val exactWidth by lazy(LazyThreadSafetyMode.NONE) { other.exactWidth * scaleX }
    override val exactHeight by lazy(LazyThreadSafetyMode.NONE) { other.exactHeight * scaleY }

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

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            SourceOperatorScaleBy(new, scaleX, scaleY)
        }
    }
}