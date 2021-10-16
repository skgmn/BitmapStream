package com.github.skgmn.bitmapstream.metadata

import android.graphics.BitmapFactory

internal open class DecodedBitmapMetadata(
    options: BitmapFactory.Options
) : ExtendedBitmapMetadata {
    override val width: Int = options.outWidth
    override val height: Int = options.outHeight
    override val mimeType: String? = options.outMimeType
    override val densityScale: Float =
        if (options.inScaled && options.inDensity != 0 && options.inTargetDensity != 0) {
            options.inTargetDensity.toFloat() / options.inDensity
        } else {
            1f
        }
}