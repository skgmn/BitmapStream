package com.github.skgmn.bitmapstream.stream.inmemory

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.test.R
import org.junit.Test

class InMemoryTest : BitmapTestBase() {
    @Test
    fun scaleByRegionScaleTo() {
        val bitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)

        var expected = scaleBy(bitmap, 0.9f, 0.8f)
        expected = Bitmap.createBitmap(expected, 10, 20, 450, 200)
        expected = Bitmap.createScaledBitmap(expected, 123, 456, true)

        val actual = assertNotNull(
            InMemoryBitmapStream(bitmap)
                .scaleBy(0.9f, 0.8f)
                .region(10, 20, 10 + 450, 20 + 200)
                .scaleTo(123, 456)
                .decode()
        )

        assertSimilar(expected, actual)
    }
}