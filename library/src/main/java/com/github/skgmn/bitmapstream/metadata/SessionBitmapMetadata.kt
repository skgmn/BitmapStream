package com.github.skgmn.bitmapstream.metadata

import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.source.DecodeSession

internal class SessionBitmapMetadata(
    decodeSession: DecodeSession
) : DecodedBitmapMetadata(decodeBounds(decodeSession)) {
    companion object {
        private fun decodeBounds(decodeSession: DecodeSession): BitmapFactory.Options {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            decodeSession.decodeBitmap(options)
            return options
        }
    }
}