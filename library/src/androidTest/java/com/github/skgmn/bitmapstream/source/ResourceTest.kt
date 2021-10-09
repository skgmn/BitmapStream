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
            val region = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
            val byFactory = region

            val stream = BitmapStream.fromResource(res, id)
                .region(left, top, right, bottom)
            assertEquals(byFactory.width, stream.metadata.width)
            assertEquals(byFactory.height, stream.metadata.height)

            val byDecoder = assertNotNull(stream.decode())
            assertSimilar(byFactory, byDecoder)
        }
    }
}