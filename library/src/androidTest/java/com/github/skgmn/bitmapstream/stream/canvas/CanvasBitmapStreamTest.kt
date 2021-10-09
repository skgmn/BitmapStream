package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.*
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.factory.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.source.factory.FactorySourceBitmapStream
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
        val imageStream = spyk(FactorySourceBitmapStream(source))
        val canvas = CanvasBitmapStream(400, 300) {
            drawColor(Color.BLACK)
            drawStream(imageStream, 210f, 160f, null)
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
        val byFactory = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        var imageBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        imageBitmap = Bitmap.createBitmap(imageBitmap, 110, 111, 112, 113)
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 200, 250, true)
        Canvas(byFactory).run {
            drawColor(Color.BLUE)
            drawBitmap(imageBitmap, -40f, -50f, null)
        }

        val source = spyk(ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image))
        val imageStream = spyk(
            FactorySourceBitmapStream(source)
                .region(110, 111, 110 + 112, 111 + 113)
                .scaleTo(200, 250)
        )
        val canvas = CanvasBitmapStream(400, 300) {
            drawColor(Color.BLUE)
            drawStream(imageStream, -40f, -50f, null)
        }
        val byDecoder = canvas.decode()

        verify(exactly = 1) {
            imageStream.region(40, 50, 200, 250)
        }
        verify(exactly = 1) {
            source.decodeBitmapRegion(Rect(132, 134, 222, 224), any())
        }
        assertSimilar(byFactory, assertNotNull(byDecoder))
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
            FactorySourceBitmapStream(source)
                .region(110, 111, 110 + 112, 111 + 113)
                .scaleTo(200, 250)
        )
        val canvas = CanvasBitmapStream(400, 300) {
            drawColor(Color.BLUE)
            drawStream(imageStream, -40f, -50f, null)
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
            FactorySourceBitmapStream(source)
                .region(110, 111, 110 + 112, 111 + 113)
                .scaleTo(200, 250)
        )
        val canvas = CanvasBitmapStream(400, 300) {
            drawColor(Color.BLUE)
            drawStream(imageStream, -40f, -50f, null)
        }
        val byDecoder = canvas.scaleBy(0.9f, 0.8f).decode()

        assertSimilar(byFactory, assertNotNull(byDecoder))
    }
}