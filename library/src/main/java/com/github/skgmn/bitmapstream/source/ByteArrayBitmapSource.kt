package com.github.skgmn.bitmapstream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect

internal class ByteArrayBitmapSource(
    private val data: ByteArray,
    private val offset: Int,
    private val length: Int,
    private val key: Any? = null
) : BitmapSource() {
    override fun createDecodeSession() = object : DecodeSession {
        override fun decodeBitmap(options: BitmapFactory.Options): Bitmap? {
            return BitmapFactory.decodeByteArray(data, offset, length, options)
        }

        override fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap? {
            val regionDecoder = BitmapRegionDecoder.newInstance(data, offset, length, false)
            return regionDecoder.decodeRegion(region, options)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteArrayBitmapSource) return false

        if (key != null) {
            if (key != other.key) return false
            return true
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return key?.hashCode() ?: 0
    }
}