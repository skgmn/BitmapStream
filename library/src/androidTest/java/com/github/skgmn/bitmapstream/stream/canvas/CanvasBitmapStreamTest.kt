package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.factory.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.source.BitmapFactoryBitmapStream
import com.github.skgmn.bitmapstream.test.R
import io.mockk.spyk
import io.mockk.verify
import org.junit.Test

class CanvasBitmapStreamTest : BitmapTestBase() {
    @Test
    fun overflowToRightBottom() {
        val byFactory = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        val imageBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        Canvas(byFactory).run {
            drawColor(Color.BLACK)
            drawBitmap(imageBitmap, 210f, 160f, null)
        }

        val source = spyk(ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image))
        val imageStream = spyk(BitmapFactoryBitmapStream(source))
        val canvas = CanvasBitmapStream(400, 300) {
            draw(ColorDrawable(Color.BLACK))
            draw(imageStream, 210, 160, null)
        }
        val byDecoder = canvas.decode()

        verify(exactly = 1) {
            imageStream.region(0, 0, 190, 140)
        }
        verify(exactly = 1) {
            source.decodeBitmapRegion(Rect(0, 0, 190, 140), any())
        }
        assertSimilar(byFactory, assertNotNull(byDecoder))
    }

    @Test
    fun regionScale() {
        val expected = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        var imageBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        imageBitmap = Bitmap.createBitmap(imageBitmap, 110, 111, 112, 113)
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 200, 250, true)
        Canvas(expected).run {
            drawColor(Color.BLUE)
            drawBitmap(imageBitmap, -40f, -50f, null)
        }

        val source = spyk(ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image))
        val imageStream = spyk(
            BitmapFactoryBitmapStream(source)
                .region(110, 111, 110 + 112, 111 + 113)
                .scaleTo(200, 250)
        )
        val canvas = CanvasBitmapStream(400, 300) {
            draw(ColorDrawable(Color.BLUE))
            draw(imageStream, -40, -50, null)
        }
        val actual = assertNotNull(canvas.decode())

        verify(exactly = 1) {
            imageStream.region(40, 50, 200, 250)
        }
        verify(exactly = 1) {
            source.decodeBitmapRegion(Rect(132, 134, 222, 224), any())
        }
        assertSimilar(expected, actual)
    }

    @Test
    fun regionScaleCanvasScaleTo() {
        var byFactory = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        var imageBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        imageBitmap = Bitmap.createBitmap(imageBitmap, 110, 111, 112, 113)
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 200, 250, true)
        Canvas(byFactory).run {
            drawColor(Color.BLUE)
            drawBitmap(imageBitmap, -40f, -50f, null)
        }
        byFactory = Bitmap.createScaledBitmap(byFactory, 123, 456, true)

        val source = spyk(ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image))
        val imageStream = spyk(
            BitmapFactoryBitmapStream(source)
                .region(110, 111, 110 + 112, 111 + 113)
                .scaleTo(200, 250)
        )
        val canvas = CanvasBitmapStream(400, 300) {
            draw(ColorDrawable(Color.BLUE))
            draw(imageStream, -40, -50, null)
        }
        val byDecoder = canvas.scaleTo(123, 456).decode()

        assertSimilar(byFactory, assertNotNull(byDecoder))
    }

    @Test
    fun regionScaleCanvasScaleBy() {
        var byFactory = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        var imageBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        imageBitmap = Bitmap.createBitmap(imageBitmap, 110, 111, 112, 113)
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 200, 250, true)
        Canvas(byFactory).run {
            drawColor(Color.BLUE)
            drawBitmap(imageBitmap, -40f, -50f, null)
        }
        byFactory = Bitmap.createScaledBitmap(byFactory, 360, 240, true)

        val source = spyk(ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image))
        val imageStream = spyk(
            BitmapFactoryBitmapStream(source)
                .region(110, 111, 110 + 112, 111 + 113)
                .scaleTo(200, 250)
        )
        val canvas = CanvasBitmapStream(400, 300) {
            draw(ColorDrawable(Color.BLUE))
            draw(imageStream, -40, -50, null)
        }
        val byDecoder = canvas.scaleBy(0.9f, 0.8f).decode()

        assertSimilar(byFactory, assertNotNull(byDecoder))
    }

    @Test
    fun regionScaleCanvasRegionScaleByRegion() {
        var expected = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        var imageBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        imageBitmap = Bitmap.createBitmap(imageBitmap, 110, 111, 112, 113)
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 200, 250, true)
        Canvas(expected).run {
            drawColor(Color.BLUE)
            drawBitmap(imageBitmap, -40f, -50f, null)
        }
        expected = Bitmap.createBitmap(expected, 100, 10, 200, 280, null, true)
        expected = Bitmap.createScaledBitmap(expected, 180, 192, true)
        expected = Bitmap.createBitmap(expected, 10, 20, 150, 150, null, true)

        val source = spyk(ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image))
        val imageStream = spyk(
            BitmapFactoryBitmapStream(source)
                .region(110, 111, 110 + 112, 111 + 113)
                .scaleTo(200, 250)
        )
        val stream = CanvasBitmapStream(400, 300) {
            draw(ColorDrawable(Color.BLUE))
            draw(imageStream, -40, -50, null)
        }
        val actual = assertNotNull(
            stream
                .region(100, 10, 300, 290)
                .scaleTo(180, 192)
                .region(10, 20, 160, 170)
                .decode()
        )

        assertSimilar(expected, actual)
    }
}