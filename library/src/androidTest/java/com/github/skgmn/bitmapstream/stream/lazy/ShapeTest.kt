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

    @Test
    fun partialRoundRect() {
        val res = appContext.resources
        val radius = 64f
        allResources {
            val bitmap = BitmapFactory.decodeResource(res, it)
            val expected = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(expected)
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.color = Color.WHITE
            val w = bitmap.width.toFloat()
            val h = bitmap.height.toFloat()
            canvas.drawRoundRect(0f, 0f, w, h, radius, radius, p)
            canvas.drawRect(0f, h - radius, w, h, p)
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, 0f, 0f, p)

            val stream = BitmapStream.fromResource(res, it)
                .shape(Shape.roundRect(radius, radius, 0f, 0f))
            val actual = assertNotNull(stream.decode())

            assertSimilar(expected, actual)
        }
    }
}