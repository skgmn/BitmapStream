package com.github.skgmn.bitmapstream.source.factory

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import java.io.File

internal class FileBitmapSource(
    private val file: File
) : BitmapFactorySource() {
    override fun decodeBitmap(options: BitmapFactory.Options): Bitmap? {
        return BitmapFactory.decodeFile(file.path, options)
    }

    override fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap? {
        val regionDecoder = BitmapRegionDecoder.newInstance(file.path, false)
        return regionDecoder.decodeRegion(region, options)
    }
}