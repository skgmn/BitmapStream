package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.test.R
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class MutableTest : BitmapTestBase() {
    @Test
    fun mutable() {
        val res = appContext.resources
        val expected = BitmapFactory.decodeResource(res, R.drawable.nodpi_image)

        val sourceSpy = BitmapSourceSpy(ResourceBitmapSource(res, R.drawable.nodpi_image))
        val actual = assertNotNull(
            BitmapFactoryBitmapStream(sourceSpy.source)
                .mutable(true)
                .decode()
        )

        verify {
            sourceSpy.sessions.last()
                .decodeBitmap(match { it.inMutable })
        }
        assertEquals(true, actual.isMutable)
        assertSimilar(expected, actual)
    }

    @Test
    fun scaleByRegionMutableScaleTo() {
        val res = appContext.resources
        val scaledBy = decodeBitmapScaleBy(0.9f, 0.8f) {
            BitmapFactory.decodeResource(res, R.drawable.nodpi_image, it)
        }
        val regioned = Bitmap.createBitmap(scaledBy, 100, 110, 120, 130)
        val expected = Bitmap.createScaledBitmap(regioned, 140, 150, true)

        val source = spyk(ResourceBitmapSource(res, R.drawable.nodpi_image))
        val decoder = BitmapFactoryBitmapStream(source)
            .scaleBy(0.9f, 0.8f)
            .region(100, 110, 100 + 120, 110 + 130)
            .mutable(true)
            .scaleTo(140, 150)
        val actual = assertNotNull(decoder.decode())

        assertEquals(true, actual.isMutable)
        assertSimilar(actual, expected)
    }

    @Test
    fun scaleByRegionMutableScaleToMutable() {
        val res = appContext.resources
        val scaledBy = decodeBitmapScaleBy(0.9f, 0.8f) {
            BitmapFactory.decodeResource(res, R.drawable.nodpi_image, it)
        }
        val regioned = Bitmap.createBitmap(scaledBy, 100, 110, 120, 130)
        val expected = Bitmap.createScaledBitmap(regioned, 140, 150, true)

        val sourceSpy = BitmapSourceSpy(ResourceBitmapSource(res, R.drawable.nodpi_image))
        val decoder = BitmapFactoryBitmapStream(sourceSpy.source)
            .scaleBy(0.9f, 0.8f)
            .region(100, 110, 100 + 120, 110 + 130)
            .mutable(true)
            .scaleTo(140, 150)
            .mutable(false)
        val actual = assertNotNull(decoder.decode())

        verify {
            sourceSpy.sessions.last()
                .decodeBitmap(match { !it.inMutable })
        }
        assertEquals(false, actual.isMutable)
        assertSimilar(actual, expected)
    }
}