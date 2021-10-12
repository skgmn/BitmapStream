package com.github.skgmn.bitmapstream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.test.R
import org.junit.Assert.assertEquals
import org.junit.Test

class ResourceTest : BitmapTestBase() {
    @Test
    fun regionDensityScaling() {
        val resIds = arrayOf(
            R.drawable.mdpi_image,
            R.drawable.hdpi_image,
            R.drawable.xhdpi_image,
            R.drawable.xxhdpi_image,
            R.drawable.xxxhdpi_image
        )
        val res = appContext.resources
        resIds.forEach { id ->
            val bitmap = BitmapFactory.decodeResource(res, id)
            val left = 20
            val top = 30
            val right = (left + 240).coerceAtMost(bitmap.width)
            val bottom = (top + 250).coerceAtMost(bitmap.height)
            val expected = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)

            val stream = BitmapStream.create(res, id)
                .region(left, top, right, bottom)
            assertEquals(expected.width, stream.metadata.width)
            assertEquals(expected.height, stream.metadata.height)

            val actual = assertNotNull(stream.decode())
            assertEquals(expected.density, actual.density)
            assertSimilar(expected, actual)
        }
    }
}