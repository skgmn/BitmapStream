package com.github.skgmn.bitmapstream.stream

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.*
import com.github.skgmn.bitmapstream.test.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(AndroidJUnit4::class)
class SourceBitmapStreamTest : BitmapTestBase() {
    @Test
    fun decodeResource() {
        val byFactory = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = SourceBitmapStream(source)
        val byDecoder = assertNotNull(decoder.decode())

        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun decodeResourceWithMetadata() {
        val byFactory = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = SourceBitmapStream(source)

        assertEquals(decoder.metadata.width, byFactory.width)
        assertEquals(decoder.metadata.height, byFactory.height)
        assertEquals(decoder.metadata.mimeType, "image/jpeg")

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun decodeResourceMultipleDensities() {
        val ids = intArrayOf(
            R.drawable.xxxhdpi_image,
            R.drawable.xxhdpi_image,
            R.drawable.xhdpi_image,
            R.drawable.hdpi_image,
            R.drawable.mdpi_image
        )

        ids.forEach {
            val byFactory = BitmapFactory.decodeResource(appContext.resources, it)

            val source = ResourceBitmapSource(
                appContext.resources,
                it
            )
            val decoder =
                SourceBitmapStream(source)
            val byDecoder = assertNotNull(decoder.decode())

            assertSimilar(byDecoder, byFactory)
        }
    }

    @Test
    fun decodeByteArray() {
        val byFactoryFromRes = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        val data = ByteArrayOutputStream().use {
            byFactoryFromRes.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.toByteArray()
        }

        val byFactoryFromByteArray = BitmapFactory.decodeByteArray(data, 0, data.size)

        val source = ByteArrayBitmapSource(
            data,
            0,
            data.size
        )
        val decoder = SourceBitmapStream(source)
        val byDecoder = assertNotNull(decoder.decode())

        assertSimilar(byDecoder, byFactoryFromByteArray)
    }

    @Test
    fun decodeByteArrayWithMetadata() {
        val byFactoryFromRes = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        val data = ByteArrayOutputStream().use {
            byFactoryFromRes.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.toByteArray()
        }

        val byFactoryFromByteArray = BitmapFactory.decodeByteArray(data, 0, data.size)

        val source = ByteArrayBitmapSource(
            data,
            0,
            data.size
        )
        val decoder = SourceBitmapStream(source)
        assertEquals(decoder.metadata.width, byFactoryFromByteArray.width)
        assertEquals(decoder.metadata.height, byFactoryFromByteArray.height)
        assertEquals(decoder.metadata.mimeType, "image/png")

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactoryFromByteArray)
    }

    @Test
    fun decodeFile() {
        val byFactoryFromRes = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        appContext.openFileOutput("decodeFileTest.png", Context.MODE_PRIVATE).use {
            byFactoryFromRes.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        val file = File(appContext.filesDir, "decodeFileTest.png")
        val byFactoryFromFile = BitmapFactory.decodeFile(file.path)

        val source = FileBitmapSource(file)
        val decoder = SourceBitmapStream(source)
        val byDecoder = assertNotNull(decoder.decode())

        assertSimilar(byDecoder, byFactoryFromFile)
    }

    @Test
    fun decodeFileWithMetadata() {
        val byFactoryFromRes = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        appContext.openFileOutput("decodeFileTest.png", Context.MODE_PRIVATE).use {
            byFactoryFromRes.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        val file = File(appContext.filesDir, "decodeFileTest.png")
        val byFactoryFromFile = BitmapFactory.decodeFile(file.path)

        val source = FileBitmapSource(file)
        val decoder = SourceBitmapStream(source)
        assertEquals(decoder.metadata.width, byFactoryFromFile.width)
        assertEquals(decoder.metadata.height, byFactoryFromFile.height)
        assertEquals(decoder.metadata.mimeType, "image/png")

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactoryFromFile)
    }

    @Test
    fun decodeStream() {
        val byFactory = BitmapFactory.decodeStream(appContext.resources.openRawResource(R.drawable.nodpi_image))

        val source = InputStreamBitmapSource(
            appContext.resources.openRawResource(R.drawable.nodpi_image)
        )
        val decoder = SourceBitmapStream(source)
        val byDecoder = assertNotNull(decoder.decode())

        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun decodeStreamWithMetadata() {
        val byFactory = BitmapFactory.decodeStream(appContext.resources.openRawResource(R.drawable.nodpi_image))

        val source = InputStreamBitmapSource(
            appContext.resources.openRawResource(R.drawable.nodpi_image)
        )
        val decoder = SourceBitmapStream(source)

        assertEquals(decoder.metadata.width, byFactory.width)
        assertEquals(decoder.metadata.height, byFactory.height)
        assertEquals(decoder.metadata.mimeType, "image/jpeg")

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun decodeAssetWithMetadata() {
        val stream = appContext.assets.open("nodpi_image.jpg")
        val byFactory = BitmapFactory.decodeStream(stream)

        val source = AssetBitmapSource(
            appContext.assets,
            "nodpi_image.jpg"
        )
        val decoder = SourceBitmapStream(source)

        assertEquals(decoder.metadata.width, byFactory.width)
        assertEquals(decoder.metadata.height, byFactory.height)
        assertEquals(decoder.metadata.mimeType, "image/jpeg")

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }
}