package com.github.skgmn.bitmapstream.stream

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.InputParameters

internal abstract class DelegateBitmapStream(
    protected val other: BitmapStream
): BitmapStream() {
    override val width: Int
        get() = other.width
    override val height: Int
        get() = other.height
    override val mimeType: String
        get() = other.mimeType

    override fun buildInputParameters(regional: Boolean): InputParameters {
        return other.buildInputParameters(regional)
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        return other.decode(inputParameters)
    }
}