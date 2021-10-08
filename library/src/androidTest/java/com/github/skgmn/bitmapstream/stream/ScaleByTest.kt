package com.github.skgmn.bitmapstream.stream

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.test.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScaleByTest : BitmapTestBase() {
    @Test
    fun simple() {
        val opts = BitmapFactory.Options()
        opts.inSampleSize = 2
        val m = Matrix()
        m.setScale(0.6f, 0.6f)
        val bitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, opts)
        val byFactory = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, m, true
        )

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = SourceBitmapStream(source)
            .scaleBy(0.3f, 0.3f)
        assertEquals(decoder.metadata.width, byFactory.width)
        assertEquals(decoder.metadata.height, byFactory.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }
}