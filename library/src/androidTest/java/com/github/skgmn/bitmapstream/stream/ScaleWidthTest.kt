package com.github.skgmn.bitmapstream.stream

import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.factory.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.source.factory.FactorySourceBitmapStream
import com.github.skgmn.bitmapstream.test.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScaleWidthTest : BitmapTestBase() {
    @Test
    fun simple() {
        val byFactory = decodeBitmapScaleTo(400, 240) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }

        val source = ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image)
        val decoder = FactorySourceBitmapStream(source)
            .scaleWidth(400)
        assertEquals(decoder.metadata.width, byFactory.width)
        assertEquals(decoder.metadata.height, byFactory.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }
}