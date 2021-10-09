package com.github.skgmn.bitmapstream.source.factory

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import com.github.skgmn.bitmapstream.InputStreamFactory

internal class InputStreamFactoryBitmapSource(
    private val inputStreamFactory: InputStreamFactory
) : BitmapFactorySource() {
    override fun decodeBitmap(options: BitmapFactory.Options): Bitmap? {
        return BitmapFactory.decodeStream(inputStreamFactory.openInputStream(), null, options)
    }

    override fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap? {
        return BitmapRegionDecoder.newInstance(inputStreamFactory.openInputStream(), false)
            .decodeRegion(region, options)
    }
}