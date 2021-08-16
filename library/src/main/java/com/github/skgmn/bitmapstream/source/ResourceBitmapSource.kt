package com.github.skgmn.bitmapstream.source

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapSource

internal class ResourceBitmapSource(
    private val res: Resources,
    private val id: Int
) : BitmapSource() {
    override val manualDensityScalingForRegional: Boolean
        get() = true

    override fun decodeBitmap(options: BitmapFactory.Options): Bitmap? {
        return BitmapFactory.decodeResource(res, id, options)
    }

    override fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap? {
        res.openRawResource(id).use { inputStream ->
            val regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false)
            return regionDecoder.decodeRegion(region, options)
        }
    }
}