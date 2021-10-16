package com.github.skgmn.bitmapstream.source

import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.stream.source.BitmapFactoryBitmapStream
import com.github.skgmn.bitmapstream.test.R
import io.mockk.verify
import okio.source
import org.junit.Assert.assertEquals
import org.junit.Test

class InputStreamFactoryTest : BitmapTestBase() {
    @Test
    fun metadataAndBitmapShouldBeDecodedInOneSession() {
        val res = appContext.resources

        val expected = BitmapFactory.decodeResource(res, R.drawable.nodpi_image)

        val inputStream = res.openRawResource(R.drawable.nodpi_image)
        val sourceSpy = BitmapSourceSpy(SourceFactoryBitmapSource {
            // returning same instance is illegal usage but just for testing
            inputStream.source()
        })

        val stream = BitmapFactoryBitmapStream(sourceSpy.source)
        val width = stream.metadata.width
        val height = stream.metadata.height

        assertEquals(1, sourceSpy.sessions.size)
        verify(exactly = 1) {
            sourceSpy.sessions.last().decodeBitmap(match { it.inJustDecodeBounds })
        }

        val actual = assertNotNull(stream.decode())
        assertEquals(expected.width, width)
        assertEquals(expected.height, height)
        assertSimilar(expected, actual)
    }
}