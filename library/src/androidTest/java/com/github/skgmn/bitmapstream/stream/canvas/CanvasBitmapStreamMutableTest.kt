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
import org.junit.Test

class CanvasBitmapStreamMutableTest : BitmapTestBase() {
    @Test
    fun overflowToRightBottomImmutable() {
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
        }.mutable(false).mutable(true).mutable(false)
        val actual = assertNotNull(canvas.decode())

        assertEquals(false, actual.isMutable)
        assertSimilar(expected, actual)
    }
}