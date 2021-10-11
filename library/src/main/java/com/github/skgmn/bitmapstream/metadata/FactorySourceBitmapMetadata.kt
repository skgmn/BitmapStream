package com.github.skgmn.bitmapstream.metadata

import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.source.factory.BitmapFactorySource

internal class FactorySourceBitmapMetadata(
    source: BitmapFactorySource
) : DecodedBitmapMetadata(decodeBounds(source)) {
    companion object {
        private fun decodeBounds(source: BitmapFactorySource): BitmapFactory.Options {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            source.decodeBitmap(options)
            return options
        }
    }
}