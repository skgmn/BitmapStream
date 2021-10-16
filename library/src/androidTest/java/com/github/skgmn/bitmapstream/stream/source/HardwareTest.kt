package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.test.R
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class HardwareTest : BitmapTestBase() {
    @Test
    fun hardware() {
        val res = appContext.resources
        val expected = BitmapFactory.decodeResource(res, R.drawable.nodpi_image)

        val source = spyk(ResourceBitmapSource(res, R.drawable.nodpi_image))
        val actual = assertNotNull(
            BitmapFactoryBitmapStream(source)
                .hardware(true)
                .decode()
        )

        verify {
            source.decodeBitmap(match { it.inPreferredConfig == Bitmap.Config.HARDWARE })
        }
        assertEquals(Bitmap.Config.HARDWARE, actual.config)
        assertSimilar(expected, actual)
    }

    @Test
    fun scaleByRegionHardwareScaleTo() {
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
            .hardware(true)
            .scaleTo(140, 150)
        val actual = assertNotNull(decoder.decode())

        assertEquals(Bitmap.Config.HARDWARE, actual.config)
        assertSimilar(actual, expected)
    }

    @Test
    fun scaleByRegionMutableScaleToHardware() {
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
            .hardware(true)
            .scaleTo(140, 150)
            .hardware(false)
        val actual = assertNotNull(decoder.decode())

        verify {
            source.decodeBitmap(match { it.inPreferredConfig != Bitmap.Config.HARDWARE })
        }
        assertNotEquals(Bitmap.Config.HARDWARE, actual.config)
        assertSimilar(actual, expected)
    }
}