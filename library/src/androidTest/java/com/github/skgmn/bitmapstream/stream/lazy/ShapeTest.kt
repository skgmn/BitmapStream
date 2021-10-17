package com.github.skgmn.bitmapstream.stream.lazy

import android.graphics.*
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.shape.Shape
import com.github.skgmn.bitmapstream.source.ResourceBitmapSource
import com.github.skgmn.bitmapstream.stream.source.BitmapFactoryBitmapStream
import org.junit.Test

class ShapeTest : BitmapTestBase() {
    @Test
    fun oval() {
        val res = appContext.resources
        allResources {
            val bitmap = BitmapFactory.decodeResource(res, it)
            val expected = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(expected)
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.color = Color.WHITE
            canvas.drawOval(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), p)
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, 0f, 0f, p)

            val stream = BitmapStream.fromResource(res, it)
                .shape(Shape.OVAL)
            val actual = assertNotNull(stream.decode())

            assertSimilar(expected, actual)
        }
    }

    @Test
    fun ovalRegionScale() {
        val res = appContext.resources
        allResources {
            val bitmap = BitmapFactory.decodeResource(res, it)
            var expected = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(expected)
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.color = Color.WHITE
            canvas.drawOval(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), p)
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, 0f, 0f, p)
            val cropWidth = bitmap.width / 2 + 50
            val cropHeight = bitmap.height / 2 + 50
            expected = Bitmap.createBitmap(expected, 0, 0, cropWidth, cropHeight)
            expected = scaleBy(expected, 0.4f, 0.5f)

            val sourceSpy = BitmapSourceSpy(ResourceBitmapSource(res, it))
            val stream = BitmapFactoryBitmapStream(sourceSpy.source)
                .shape(Shape.OVAL)
                .region(0, 0, cropWidth, cropHeight)
                .scaleBy(0.4f, 0.5f)
            val actual = assertNotNull(stream.decode())

            assertSimilar(expected, actual)
        }
    }
}