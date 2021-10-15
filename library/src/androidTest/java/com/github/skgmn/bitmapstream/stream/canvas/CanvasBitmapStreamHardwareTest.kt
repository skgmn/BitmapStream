package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.source.BitmapFactoryBitmapStream
import com.github.skgmn.bitmapstream.test.R
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CanvasBitmapStreamHardwareTest : BitmapTestBase() {
    @Test
    fun overflowToRightBottomHardware() {
        val expected = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        val imageBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        Canvas(expected).run {
            drawColor(Color.BLACK)
            drawBitmap(imageBitmap, 210f, 160f, null)
        }

        val source = spyk(ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image))
        val imageStream = spyk(BitmapFactoryBitmapStream(source))
        val canvas = CanvasBitmapStream(400, 300) {
            draw(ColorDrawable(Color.BLACK))
            draw(imageStream, 210, 160, null)
        }.hardware(true).hardware(false).hardware(true)
        val actual = assertNotNull(canvas.decode())

        assertEquals(Bitmap.Config.HARDWARE, actual.config)
        assertSimilar(expected, actual)
    }

    @Test
    fun regionScaleMutableHardware() {
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
        }.mutable(true).hardware(true)
        val actual = assertNotNull(canvas.decode())

        assertEquals(Bitmap.Config.HARDWARE, actual.config)
        assertEquals(false, actual.isMutable)
        assertSimilar(expected, actual)
    }

    @Test
    fun regionScaleCanvasScaleToHardwareMutable() {
        var expected = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        var imageBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        imageBitmap = Bitmap.createBitmap(imageBitmap, 110, 111, 112, 113)
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 200, 250, true)
        Canvas(expected).run {
            drawColor(Color.BLUE)
            drawBitmap(imageBitmap, -40f, -50f, null)
        }
        expected = Bitmap.createScaledBitmap(expected, 123, 456, true)

        val source = spyk(ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image))
        val imageStream = spyk(
            BitmapFactoryBitmapStream(source)
                .region(110, 111, 110 + 112, 111 + 113)
                .scaleTo(200, 250)
        )
        val canvas = CanvasBitmapStream(400, 300) {
            draw(ColorDrawable(Color.BLUE))
            draw(imageStream, -40, -50, null)
        }.hardware(true).mutable(true)
        val actual = assertNotNull(canvas.scaleTo(123, 456).decode())

        assertNotEquals(Bitmap.Config.HARDWARE, actual.config)
        assertEquals(true, actual.isMutable)
        assertSimilar(expected, actual)
    }

    @Test
    fun regionScaleCanvasScaleByMutableHardwareSoftware() {
        var expected = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        var imageBitmap = BitmapFactory.decodeResource(appContext.resources, R.drawable.nodpi_image)
        imageBitmap = Bitmap.createBitmap(imageBitmap, 110, 111, 112, 113)
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 200, 250, true)
        Canvas(expected).run {
            drawColor(Color.BLUE)
            drawBitmap(imageBitmap, -40f, -50f, null)
        }
        expected = Bitmap.createScaledBitmap(expected, 360, 240, true)

        val source = spyk(ResourceBitmapSource(appContext.resources, R.drawable.nodpi_image))
        val imageStream = spyk(
            BitmapFactoryBitmapStream(source)
                .region(110, 111, 110 + 112, 111 + 113)
                .scaleTo(200, 250)
        )
        val canvas = CanvasBitmapStream(400, 300) {
            draw(ColorDrawable(Color.BLUE))
            draw(imageStream, -40, -50, null)
        }.mutable(true).hardware(true).hardware(false)
        val actual = assertNotNull(canvas.scaleBy(0.9f, 0.8f).decode())

        assertNotEquals(Bitmap.Config.HARDWARE, actual.config)
        assertEquals(true, actual.isMutable)
        assertSimilar(expected, actual)
    }
}