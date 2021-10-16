package com.github.skgmn.bitmapstream

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.source.BitmapFactoryBitmapStream
import com.github.skgmn.bitmapstream.test.R
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class FrameTest : BitmapTestBase() {
    @Test
    fun fitCenter() {
        val res = appContext.resources
        val source = BitmapFactory.decodeResource(res, R.drawable.nodpi_image)
        val frame = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        Canvas(frame).run {
            drawColor(Color.RED)
            drawBitmap(source, null, Rect(0, 40, 200, 160), Paint(Paint.FILTER_BITMAP_FLAG))
        }
        val byFactory = frame

        val sourceStream =
            BitmapFactoryBitmapStream(ResourceBitmapSource(res, R.drawable.nodpi_image))
        val frameStream =
            sourceStream.frame(200, 200, ImageView.ScaleType.FIT_CENTER, ColorDrawable(Color.RED))
        val byDecoder = assertNotNull(frameStream.decode())

        assertSimilar(byFactory, byDecoder)
    }

    @Test
    fun fitCenterHardware() {
        val res = appContext.resources
        val source = BitmapFactory.decodeResource(res, R.drawable.nodpi_image)
        val expected = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        Canvas(expected).run {
            drawColor(Color.RED)
            drawBitmap(source, null, Rect(0, 40, 200, 160), Paint(Paint.FILTER_BITMAP_FLAG))
        }

        val sourceStream =
            BitmapFactoryBitmapStream(ResourceBitmapSource(res, R.drawable.nodpi_image))
                .hardware(true)
        val frameStream =
            sourceStream.frame(200, 200, ImageView.ScaleType.FIT_CENTER, ColorDrawable(Color.RED))
        val actual = assertNotNull(frameStream.decode())

        assertEquals(Bitmap.Config.HARDWARE, actual.config)
        assertSimilar(expected, actual)
    }

    @Test
    fun centerCrop() {
        val res = appContext.resources
        val source = BitmapFactory.decodeResource(res, R.drawable.nodpi_image)
        val expected = Bitmap.createBitmap(288, 288, Bitmap.Config.ARGB_8888)
        Canvas(expected).run {
            drawColor(Color.RED)
            drawBitmap(
                source,
                Rect(120, 0, 480, 360),
                Rect(0, 0, 288, 288),
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
        }

        val sourceSpy = BitmapSourceSpy(ResourceBitmapSource(res, R.drawable.nodpi_image))
        val sourceStream = BitmapFactoryBitmapStream(sourceSpy.source)
        val background = spyk(ColorDrawable(Color.RED))
        val frameStream = sourceStream.frame(288, 288, ImageView.ScaleType.CENTER_CROP, background)
        val actual = assertNotNull(frameStream.decode())

        verify {
            sourceSpy.sessions.last()
                .decodeBitmapRegion(Rect(120, 0, 480, 360), any())
        }
        verify(exactly = 0) {
            background.draw(any())
        }
        assertSimilar(expected, actual)
    }

    @Test
    fun downsample() {
        val res = appContext.resources
        val source = BitmapFactory.decodeResource(res, R.drawable.nodpi_image)
        val expected = Bitmap.createBitmap(144, 144, Bitmap.Config.ARGB_8888)
        Canvas(expected).run {
            drawColor(Color.RED)
            drawBitmap(
                source,
                Rect(120, 0, 480, 360),
                Rect(0, 0, 144, 144),
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
        }

        val sourceSpy = BitmapSourceSpy(ResourceBitmapSource(res, R.drawable.nodpi_image))
        val sourceStream = BitmapFactoryBitmapStream(sourceSpy.source)
        val frameStream =
            sourceStream.frame(144, 144, ImageView.ScaleType.CENTER_CROP, ColorDrawable(Color.RED))
        val actual = assertNotNull(frameStream.decode())

        verify {
            sourceSpy.sessions.last().decodeBitmapRegion(
                Rect(120, 0, 480, 360),
                match { it.inSampleSize == 2 }
            )
        }
        assertSimilar(expected, actual)
    }
}