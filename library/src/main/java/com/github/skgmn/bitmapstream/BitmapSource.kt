package com.github.skgmn.bitmapstream

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata

internal abstract class BitmapSource {
    abstract fun decodeBitmap(options: BitmapFactory.Options): Bitmap?
    abstract fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap?

    open fun createNewDecodingState(): DecodingState? = null
    open fun generateInputParameters(
        features: StreamFeatures,
        metadata: BitmapMetadata
    ): InputParameters = InputParameters()
}