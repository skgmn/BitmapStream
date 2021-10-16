package com.github.skgmn.bitmapstream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import java.io.File

internal class FileBitmapSource(
    private val file: File
) : BitmapSource() {
    override fun createDecodeSession() = object : DecodeSession {
        override fun decodeBitmap(options: BitmapFactory.Options): Bitmap? {
            return BitmapFactory.decodeFile(file.path, options)
        }

        override fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap? {
            val regionDecoder = BitmapRegionDecoder.newInstance(file.path, false)
            return regionDecoder.decodeRegion(region, options)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FileBitmapSource) return false

        if (file != other.file) return false

        return true
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }
}