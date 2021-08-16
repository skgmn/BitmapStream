package com.github.skgmn.bitmapstream.source

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapSource
import com.github.skgmn.bitmapstream.DecodingState

internal class AssetBitmapSource(
    private val assetManager: AssetManager,
    private val path: String
) : BitmapSource() {
    override fun decodeBitmap(options: BitmapFactory.Options): Bitmap? {
        return assetManager.open(path).use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
    }

    override fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap? {
        return assetManager.open(path).use { stream ->
            val regionDecoder = BitmapRegionDecoder.newInstance(stream, false)
            regionDecoder.decodeRegion(region, options)
        }
    }
}