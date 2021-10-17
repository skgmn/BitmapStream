package com.github.skgmn.bitmapstream.stream.lazy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.source.BitmapFactoryBitmapStream
import com.github.skgmn.bitmapstream.test.R
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class BufferTest : BitmapTestBase() {
    @Test
    fun scaleByBufferRegionScaleTo() {
        val scaledBy = decodeBitmapScaleBy(0.9f, 0.8f) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val regioned = Bitmap.createBitmap(scaledBy, 100, 110, 120, 130)
        val expected = Bitmap.createScaledBitmap(regioned, 140, 150, true)

        val sourceSpy = BitmapSourceSpy(
            ResourceBitmapSource(
                appContext.resources,
                R.drawable.nodpi_image
            )
        )
        val decoder = BitmapFactoryBitmapStream(sourceSpy.source)
            .scaleBy(0.9f, 0.8f)
            .buffer()
            .region(100, 110, 100 + 120, 110 + 130)
            .scaleTo(140, 150)
        assertEquals(140, decoder.size.width)
        assertEquals(150, decoder.size.height)

        val actual = assertNotNull(decoder.decode())
        verify(exactly = 0) { sourceSpy.sessions.last().decodeBitmapRegion(any(), any()) }

        assertSimilar(actual, expected)
    }
}