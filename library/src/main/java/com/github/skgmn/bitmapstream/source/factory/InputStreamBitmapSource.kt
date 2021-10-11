package com.github.skgmn.bitmapstream.source.factory

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import com.github.skgmn.bitmapstream.DecodingState
import com.github.skgmn.bytestream.util.RewindableInputStream
import java.io.InputStream

internal class InputStreamBitmapSource(inputStream: InputStream) : BitmapFactorySource() {
    private val rewindableInputStream = RewindableInputStream(inputStream)

    override fun decodeBitmap(options: BitmapFactory.Options): Bitmap? {
        return BitmapFactory.decodeStream(rewindableInputStream, null, options)
    }

    override fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap? {
        val regionDecoder = BitmapRegionDecoder.newInstance(rewindableInputStream, false)
        return regionDecoder.decodeRegion(region, options)
    }

    override fun createNewDecodingState(): DecodingState {
        return InputStreamDecodingState(rewindableInputStream)
    }

    private class InputStreamDecodingState(
        private val rewindableInputStream: RewindableInputStream
    ) : DecodingState() {
        override fun setPhase(phase: Int) {
            when (phase) {
                PHASE_BITMAP -> rewindableInputStream.rewind()
                PHASE_COMLETE -> rewindableInputStream.close()
            }
        }
    }
}