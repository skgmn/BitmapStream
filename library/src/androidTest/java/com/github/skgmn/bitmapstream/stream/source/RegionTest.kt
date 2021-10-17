package com.github.skgmn.bitmapstream.stream.source

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.ByteArrayBitmapSource
import com.github.skgmn.bitmapstream.source.FileBitmapSource
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.source.SourceFactoryBitmapSource
import com.github.skgmn.bitmapstream.test.R
import okio.source
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(AndroidJUnit4::class)
class RegionTest : BitmapTestBase() {
    @Test
    fun simple() {
        val bitmap =
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, null)
        val byFactory = Bitmap.createBitmap(
            bitmap, 100, 110, 120, 130
        )

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = BitmapFactoryBitmapStream(source)
            .region(100, 110, 100 + 120, 110 + 130)
        assertEquals(decoder.size.width, byFactory.width)
        assertEquals(decoder.size.height, byFactory.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun multipleDensities() {
        val ids = intArrayOf(
            R.drawable.xxxhdpi_image,
            R.drawable.xxhdpi_image,
            R.drawable.xhdpi_image,
            R.drawable.hdpi_image,
            R.drawable.mdpi_image
        )

        ids.forEach {
            val byFactory = Bitmap.createBitmap(
                BitmapFactory.decodeResource(appContext.resources, it),
                12, 34, 56, 78
            )

            val source = ResourceBitmapSource(appContext.resources, it)
            val decoder = BitmapFactoryBitmapStream(
                source
            ).region(12, 34, 12 + 56, 34 + 78)
            val byDecoder = assertNotNull(decoder.decode())

            assertSimilar(byDecoder, byFactory)
        }
    }

    @Test
    fun byteArray() {
        val byFactoryFromRes =
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        val data = ByteArrayOutputStream().use {
            byFactoryFromRes.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.toByteArray()
        }

        val byFactoryFromByteArray = Bitmap.createBitmap(
            BitmapFactory.decodeByteArray(data, 0, data.size),
            12, 34, 56, 78
        )

        val source = ByteArrayBitmapSource(
            data,
            0,
            data.size
        )
        val decoder = BitmapFactoryBitmapStream(source)
            .region(12, 34, 12 + 56, 34 + 78)
        assertEquals(decoder.size.width, byFactoryFromByteArray.width)
        assertEquals(decoder.size.height, byFactoryFromByteArray.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactoryFromByteArray)
    }

    @Test
    fun file() {
        val byFactoryFromRes =
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        appContext.openFileOutput("decodeFileTest.png", Context.MODE_PRIVATE).use {
            byFactoryFromRes.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        val file = File(appContext.filesDir, "decodeFileTest.png")
        val byFactoryFromFile = Bitmap.createBitmap(
            BitmapFactory.decodeFile(file.path),
            12, 34, 56, 78
        )

        val source = FileBitmapSource(file)
        val decoder = BitmapFactoryBitmapStream(source)
            .region(12, 34, 12 + 56, 34 + 78)
        assertEquals(decoder.size.width, byFactoryFromFile.width)
        assertEquals(decoder.size.height, byFactoryFromFile.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactoryFromFile)
    }

    @Test
    fun decodeStreamWithMetadata() {
        val byFactory = Bitmap.createBitmap(
            BitmapFactory.decodeStream(appContext.resources.openRawResource(R.drawable.nodpi_image)),
            12, 34, 56, 78
        )

        val source = SourceFactoryBitmapSource {
            appContext.resources.openRawResource(R.drawable.nodpi_image).source()
        }
        val decoder = BitmapFactoryBitmapStream(source)
            .region(12, 34, 12 + 56, 34 + 78)

        assertEquals(decoder.size.width, byFactory.width)
        assertEquals(decoder.size.height, byFactory.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun afterScaleBy() {
        val bitmap =
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, null)
        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap, bitmap.width * 2, bitmap.height * 3, true
        )
        val byFactory = Bitmap.createBitmap(
            scaledBitmap, 100, 110, 120, 130
        )

        val source = ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image)
        val decoder = BitmapFactoryBitmapStream(source)
            .scaleBy(2f, 3f)
            .region(100, 110, 100 + 120, 110 + 130)
        assertEquals(decoder.size.width, byFactory.width)
        assertEquals(decoder.size.height, byFactory.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun entireArea() {
        val expected =
            BitmapFactory.decodeResource(appContext.resources, R.drawable.mdpi_image, null)

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.mdpi_image
        )
        val decoder = BitmapFactoryBitmapStream(source)
            .region(0, 0, expected.width, expected.height)
        assertEquals(decoder.size.width, expected.width)
        assertEquals(decoder.size.height, expected.height)
        val actual = assertNotNull(decoder.decode())

        assertSimilar(actual, expected)
    }
}