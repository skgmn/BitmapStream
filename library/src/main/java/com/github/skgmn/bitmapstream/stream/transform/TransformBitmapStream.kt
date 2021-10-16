package com.github.skgmn.bitmapstream.stream.transform

import com.github.skgmn.bitmapstream.BitmapStream

internal abstract class TransformBitmapStream(
    protected val other: BitmapStream
) : BitmapStream() {
    override val metadata get() = other.metadata
    override val hasDimensions get() = other.hasDimensions

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return replaceUpstream(other.scaleTo(width, height))
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return replaceUpstream(other.scaleWidth(width))
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return replaceUpstream(other.scaleHeight(height))
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return replaceUpstream(other.scaleBy(scaleWidth, scaleHeight))
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        return replaceUpstream(other.region(left, top, right, bottom))
    }

    override fun mutable(mutable: Boolean?): BitmapStream {
        val mutableCleared = replaceUpstream(other.mutable(null))
        return when (mutable) {
            null -> mutableCleared
            else -> MutableTransformBitmapStream(mutableCleared, mutable)
        }
    }

    override fun hardware(hardware: Boolean): BitmapStream {
        val hardwareCleared = replaceUpstream(other.hardware(false))
        return if (hardware) {
            HardwareTransformBitmapStream(hardwareCleared)
        } else {
            hardwareCleared
        }
    }

    protected abstract fun replaceUpstream(new: BitmapStream): BitmapStream
}