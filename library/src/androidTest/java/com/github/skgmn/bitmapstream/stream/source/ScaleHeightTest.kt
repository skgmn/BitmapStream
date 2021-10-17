package com.github.skgmn.bitmapstream.stream.source

import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.test.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScaleHeightTest : BitmapTestBase() {
    @Test
    fun simple() {
        val byFactory = decodeBitmapScaleTo(400, 240) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }

        val source = ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image)
        val decoder = BitmapFactoryBitmapStream(source)
            .scaleHeight(240)
        assertEquals(decoder.size.width, byFactory.width)
        assertEquals(decoder.size.height, byFactory.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }
}