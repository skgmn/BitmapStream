package com.github.skgmn.bitmapstream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import okio.buffer

internal class SourceFactoryBitmapSource(
    private val key: Any? = null,
    private val factory: SourceFactory
) : BitmapSource() {
    override fun createDecodeSession(): DecodeSession {
        return object : DecodeSession {
            private val bufferedSource by lazy {
                factory.createSource().buffer()
            }

            override fun decodeBitmap(options: BitmapFactory.Options): Bitmap? {
                val source = if (options.inJustDecodeBounds) {
                    bufferedSource.peek()
                } else {
                    bufferedSource
                }
                return BitmapFactory.decodeStream(source.inputStream(), null, options)
            }

            override fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap? {
                return BitmapRegionDecoder
                    .newInstance(bufferedSource.inputStream(), false)
                    .decodeRegion(region, options)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SourceFactoryBitmapSource) return false

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