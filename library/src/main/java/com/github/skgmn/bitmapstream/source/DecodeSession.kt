package com.github.skgmn.bitmapstream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

internal interface DecodeSession {
    fun decodeBitmap(options: BitmapFactory.Options): Bitmap?
    fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap?
}