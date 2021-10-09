package com.github.skgmn.bitmapstream.stream

import android.graphics.Bitmap
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
class TwoOpsTest : BitmapTestBase() {
    @Test
    fun scaleToScaleTo() {
        val scaledTo = decodeBitmapScaleTo(100, 200) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val byFactory = scaledTo

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleTo(300, 400)
            .scaleTo(100, 200)
        assertEquals(100, decoder.metadata.width)
        assertEquals(200, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun scaleToScaleBy() {
        val scaledTo = decodeBitmapScaleTo(110, 240) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val byFactory = scaledTo

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleTo(100, 200)
            .scaleBy(1.1f, 1.2f)
        assertEquals(110, decoder.metadata.width)
        assertEquals(240, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun scaleToRegion() {
        val scaledTo = decodeBitmapScaleTo(300, 400) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val regioned = Bitmap.createBitmap(scaledTo, 100, 110, 120, 130)
        val byFactory = regioned

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleTo(300, 400)
            .region(100, 110, 100 + 120, 110 + 130)
        assertEquals(120, decoder.metadata.width)
        assertEquals(130, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun scaleWidthScaleWidth() {
        val scaledTo = decodeBitmapScaleTo(200, 120) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val byFactory = scaledTo

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleWidth(100)
            .scaleWidth(200)
        assertEquals(byFactory.width, decoder.metadata.width)
        assertEquals(byFactory.height, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun scaleWidthScaleHeight() {
        val scaledTo = decodeBitmapScaleTo(333, 200) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val byFactory = scaledTo

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleWidth(100)
            .scaleHeight(200)
        assertEquals(byFactory.width, decoder.metadata.width)
        assertEquals(byFactory.height, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun scaleHeightScaleWidth() {
        val scaledTo = decodeBitmapScaleTo(200, 120) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val byFactory = scaledTo

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleHeight(100)
            .scaleWidth(200)
        assertEquals(byFactory.width, decoder.metadata.width)
        assertEquals(byFactory.height, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun scaleHeightScaleHeight() {
        val scaledTo = decodeBitmapScaleTo(333, 200) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val byFactory = scaledTo

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleHeight(100)
            .scaleHeight(200)
        assertEquals(byFactory.width, decoder.metadata.width)
        assertEquals(byFactory.height, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun scaleByScaleTo() {
        val scaledTo = decodeBitmapScaleTo(200, 210) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val byFactory = scaledTo

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleBy(0.7f, 0.8f)
            .scaleTo(200, 210)
        assertEquals(200, decoder.metadata.width)
        assertEquals(210, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun scaleByScaleBy() {
        val scaledBy = decodeBitmapScaleBy(0.35f, 0.48f) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val byFactory = scaledBy

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleBy(0.5f, 0.6f)
            .scaleBy(0.7f, 0.8f)
        assertEquals(byFactory.width, decoder.metadata.width)
        assertEquals(byFactory.height, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun scaleByRegion() {
        val scaledBy = decodeBitmapScaleBy(0.7f, 0.8f) {
            BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, it)
        }
        val regioned = Bitmap.createBitmap(scaledBy, 100, 110, 120, 130)
        val byFactory = regioned

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .scaleBy(0.7f, 0.8f)
            .region(100, 110, 100 + 120, 110 + 130)
        assertEquals(120, decoder.metadata.width)
        assertEquals(130, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun regionScaleTo() {
        val bitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, null)
        val regioned = Bitmap.createBitmap(bitmap, 100, 110, 120, 130)
        val scaledTo = Bitmap.createScaledBitmap(regioned, 140, 150, true)
        val byFactory = scaledTo

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .region(100, 110, 100 + 120, 110 + 130)
            .scaleTo(140, 150)
        assertEquals(140, decoder.metadata.width)
        assertEquals(150, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun regionScaleBy() {
        val bitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, null)
        val regioned = Bitmap.createBitmap(bitmap, 100, 110, 120, 130)
        val scaledBy = scaleBy(regioned, 0.9f, 0.8f)
        val byFactory = scaledBy

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .region(100, 110, 100 + 120, 110 + 130)
            .scaleBy(0.9f, 0.8f)
        assertEquals(byFactory.width, decoder.metadata.width)
        assertEquals(byFactory.height, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }

    @Test
    fun regionRegion() {
        val bitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image, null)
        val regioned = Bitmap.createBitmap(bitmap, 100, 110, 120, 130)
        val regioned2 = Bitmap.createBitmap(regioned, 10, 20, 30, 40)
        val byFactory = regioned2

        val source = ResourceBitmapSource(
            appContext.resources,
            R.drawable.nodpi_image
        )
        val decoder = FactorySourceBitmapStream(source)
            .region(100, 110, 100 + 120, 110 + 130)
            .region(10, 20, 10 + 30, 20 + 40)
        assertEquals(30, decoder.metadata.width)
        assertEquals(40, decoder.metadata.height)

        val byDecoder = assertNotNull(decoder.decode())
        assertSimilar(byDecoder, byFactory)
    }
}