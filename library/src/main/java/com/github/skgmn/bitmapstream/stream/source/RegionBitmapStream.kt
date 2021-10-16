package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import kotlin.math.roundToInt

internal class RegionBitmapStream(
    other: SourceBitmapStream,
    private val left: Int,
    private val top: Int,
    private val right: Int,
    private val bottom: Int
) : DelegateBitmapStream(other) {
    override val metadata = object : BitmapMetadata {
        override val width: Int get() = right - left
        override val height: Int get() = bottom - top
        override val mimeType: String? get() = other.metadata.mimeType
        override val densityScale: Float get() = other.metadata.densityScale
    }
    override val hasMetadata get() = true

    override val features = object : StreamFeatures by other.features {
        override val regional get() = true
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return if (left == 0 && top == 0 && right == metadata.width && bottom == metadata.height) {
            this
        } else {
            val newLeft = this.left + left
            val newTop = this.top + top
            RegionBitmapStream(
                other,
                newLeft,
                newTop,
                newLeft + (right - left),
                newTop + (bottom - top)
            )
        }
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return other.buildInputParameters(features).apply {
            val width = metadata.width
            val height = metadata.height

            val left = (region?.left ?: 0) + (left / scaleX).roundToInt()
            val top = (region?.top ?: 0) + (top / scaleY).roundToInt()
            val right = left + (width / scaleX).roundToInt()
            val bottom = top + (height / scaleY).roundToInt()

            val scaledRegion = region ?: Rect().also { region = it }
            scaledRegion.left = left
            scaledRegion.top = top
            scaledRegion.right = right
            scaledRegion.bottom = bottom

            scaleX = width.toFloat() / scaledRegion.width()
            scaleY = height.toFloat() / scaledRegion.height()
        }
    }

    override fun replaceUpstream(new: SourceBitmapStream): SourceBitmapStream {
        return if (other === new) {
            this
        } else {
            RegionBitmapStream(new, left, top, right, bottom)
        }
    }
}