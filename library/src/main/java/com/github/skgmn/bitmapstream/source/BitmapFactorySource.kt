package com.github.skgmn.bitmapstream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.github.skgmn.bitmapstream.DecodingState
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.stream.source.InputParameters

internal abstract class BitmapFactorySource {
    abstract fun decodeBitmap(options: BitmapFactory.Options): Bitmap?
    abstract fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap?

    open fun createNewDecodingState(): DecodingState? = null
    open fun generateInputParameters(
        features: StreamFeatures,
        metadata: BitmapMetadata
    ): InputParameters = InputParameters()
}