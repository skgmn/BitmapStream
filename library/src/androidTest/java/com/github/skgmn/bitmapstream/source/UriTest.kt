package com.github.skgmn.bitmapstream.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.test.BuildConfig
import com.github.skgmn.bitmapstream.test.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UriTest : BitmapTestBase() {
    private lateinit var uris: Array<Pair<String, () -> Bitmap?>>

    @Before
    override fun setUp() {
        super.setUp()
        val res = appContext.resources
        uris = arrayOf(
            "android.resource://${BuildConfig.APPLICATION_ID}/${R.drawable.mdpi_image}" to
                    { BitmapFactory.decodeResource(res, R.drawable.mdpi_image) },
            "android.resource://${BuildConfig.APPLICATION_ID}/${R.drawable.hdpi_image}" to
                    { BitmapFactory.decodeResource(res, R.drawable.hdpi_image) },
            "android.resource://${BuildConfig.APPLICATION_ID}/${R.drawable.xhdpi_image}" to
                    { BitmapFactory.decodeResource(res, R.drawable.xhdpi_image) },
            "android.resource://${BuildConfig.APPLICATION_ID}/${R.drawable.xxhdpi_image}" to
                    { BitmapFactory.decodeResource(res, R.drawable.xxhdpi_image) },
            "android.resource://${BuildConfig.APPLICATION_ID}/${R.drawable.xxxhdpi_image}" to
                    { BitmapFactory.decodeResource(res, R.drawable.xxxhdpi_image) },
            "android.resource://${BuildConfig.APPLICATION_ID}/drawable/mdpi_image" to
                    { BitmapFactory.decodeResource(res, R.drawable.mdpi_image) },
            "android.resource://${BuildConfig.APPLICATION_ID}/drawable/hdpi_image" to
                    { BitmapFactory.decodeResource(res, R.drawable.hdpi_image) },
            "android.resource://${BuildConfig.APPLICATION_ID}/drawable/xhdpi_image" to
                    { BitmapFactory.decodeResource(res, R.drawable.xhdpi_image) },
            "android.resource://${BuildConfig.APPLICATION_ID}/drawable/xxhdpi_image" to
                    { BitmapFactory.decodeResource(res, R.drawable.xxhdpi_image) },
            "android.resource://${BuildConfig.APPLICATION_ID}/drawable/xxxhdpi_image" to
                    { BitmapFactory.decodeResource(res, R.drawable.xxxhdpi_image) },
        )
    }

    @Test
    fun dimensions() {
        uris.forEach { (uriString, bitmapFactory) ->
            val uri = Uri.parse(uriString)
            val expected = assertNotNull(bitmapFactory())

            val stream = BitmapStream.fromUri(appContext, uri)
            Assert.assertEquals(
                "uri=$uri, expected width=${expected.width}, metadata width=${stream.size.width}",
                expected.width,
                stream.size.width
            )
            Assert.assertEquals(
                "uri=$uri, expected height=${expected.height}, metadata height=${stream.size.height}",
                expected.height,
                stream.size.height
            )

            val actual = assertNotNull(stream.decode())
            Assert.assertEquals(
                "uri=$uri, expected density=${expected.density}, metadata density=${actual.density}",
                expected.density,
                actual.density
            )
            assertSimilar(expected, actual)
        }
    }

    @Test
    fun regionDensityScaling() {
        uris.forEach { (uriString, bitmapFactory) ->
            val uri = Uri.parse(uriString)
            val bitmap = assertNotNull(bitmapFactory())
            val left = 20
            val top = 30
            val right = (left + 240).coerceAtMost(bitmap.width)
            val bottom = (top + 250).coerceAtMost(bitmap.height)
            val expected = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)

            val stream = BitmapStream.fromUri(appContext, uri)
                .region(left, top, right, bottom)
            Assert.assertEquals(expected.width, stream.size.width)
            Assert.assertEquals(expected.height, stream.size.height)

            val actual = assertNotNull(stream.decode())
            Assert.assertEquals(expected.density, actual.density)
            assertSimilar(expected, actual)
        }
    }
}