package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.factory.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.source.factory.FactorySourceBitmapStream
import com.github.skgmn.bitmapstream.test.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScaleToTest : BitmapTestBase() {
    @Test
    fun simpleScaleTo() {
        val optsDecodeBounds = BitmapFactory.Options()
        optsDecodeBounds.inJustDecodeBounds = true
        BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, optsDecodeBounds)

        val targetWidth = optsDecodeBounds.outWidth / 3
        val targetHeight = optsDecodeBounds.outHeight / 3

        val opts = BitmapFactory.Options()
        opts.inSampleSize = 2
        val byFactory = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, opts),
            targetWidth, targetHeight, true
        )

        val source = ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image)
        val decoder = FactorySourceBitmapStream(source)
            .scaleTo(targetWidth, targetHeight)
        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }
}