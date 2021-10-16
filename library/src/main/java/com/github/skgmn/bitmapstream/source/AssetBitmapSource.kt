package com.github.skgmn.bitmapstream.source

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect

internal class AssetBitmapSource(
    private val assetManager: AssetManager,
    private val path: String
) : BitmapSource() {
    override fun createDecodeSession() = object : DecodeSession {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AssetBitmapSource) return false

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}