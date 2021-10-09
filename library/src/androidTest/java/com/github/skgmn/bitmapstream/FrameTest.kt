package com.github.skgmn.bitmapstream

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.github.skgmn.bitmapstream.source.factory.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.source.factory.FactorySourceBitmapStream
import com.github.skgmn.bitmapstream.test.R
import io.mockk.spyk
import io.mockk.verify
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
            FactorySourceBitmapStream(ResourceBitmapSource(res, R.drawable.nodpi_image))
        val frameStream =
            sourceStream.frame(200, 200, ImageView.ScaleType.FIT_CENTER, ColorDrawable(Color.RED))
        val byDecoder = assertNotNull(frameStream.decode())

        assertSimilar(byFactory, byDecoder)
    }

    @Test
    fun centerCrop() {
        val res = appContext.resources
        val source = BitmapFactory.decodeResource(res, R.drawable.nodpi_image)
        val frame = Bitmap.createBitmap(288, 288, Bitmap.Config.ARGB_8888)
        Canvas(frame).run {
            drawColor(Color.RED)
            drawBitmap(
                source,
                Rect(120, 0, 480, 360),
                Rect(0, 0, 288, 288),
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
        }
        val byFactory = frame

        val bitmapSource = spyk(ResourceBitmapSource(res, R.drawable.nodpi_image))
        val sourceStream = FactorySourceBitmapStream(bitmapSource)
        val frameStream =
            sourceStream.frame(288, 288, ImageView.ScaleType.CENTER_CROP, ColorDrawable(Color.RED))
        val byDecoder = assertNotNull(frameStream.decode())

        verify {
            bitmapSource.decodeBitmapRegion(Rect(120, 0, 480, 360), any())
        }
        assertSimilar(byFactory, byDecoder)
    }
}