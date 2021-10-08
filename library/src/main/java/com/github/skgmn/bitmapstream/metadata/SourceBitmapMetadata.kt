package com.github.skgmn.bitmapstream.metadata

import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.BitmapSource

internal class SourceBitmapMetadata(
    source: BitmapSource
) : DecodedBitmapMetadata(decodeBounds(source)) {
    companion object {
        private fun decodeBounds(source: BitmapSource): BitmapFactory.Options {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            source.decodeBitmap(options)
            return options
        }
    }
}