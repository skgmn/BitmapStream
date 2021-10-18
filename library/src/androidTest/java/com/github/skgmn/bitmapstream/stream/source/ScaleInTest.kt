package com.github.skgmn.bitmapstream.stream.source

import android.graphics.BitmapFactory
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.BitmapTestBase
import org.junit.Test

class ScaleInTest : BitmapTestBase() {
    @Test
    fun biggerThanMaxWidth() {
        val maxWidth = 400
        val res = appContext.resources
        allResources {
            val bitmap = BitmapFactory.decodeResource(res, it)
            val expected = scaleIn(bitmap, maxWidth, Int.MAX_VALUE)

            val stream = BitmapStream.fromResource(res, it)
                .scaleIn(maxWidth = maxWidth)
            val actual = assertNotNull(stream.decode())

            assertSimilar(expected, actual)
        }
    }

    @Test
    fun smallerThanMaxWidth() {
        val maxWidth = 9999999
        val res = appContext.resources
        allResources {
            val bitmap = BitmapFactory.decodeResource(res, it)
            val expected = scaleIn(bitmap, maxWidth, Int.MAX_VALUE)

            val stream = BitmapStream.fromResource(res, it)
                .scaleIn(maxWidth = maxWidth)
            val actual = assertNotNull(stream.decode())

            assertSimilar(expected, actual)
        }
    }

    @Test
    fun biggerThanMaxHeight() {
        val maxHeight = 200
        val res = appContext.resources
        allResources {
            val bitmap = BitmapFactory.decodeResource(res, it)
            val expected = scaleIn(bitmap, Int.MAX_VALUE, maxHeight)

            val stream = BitmapStream.fromResource(res, it)
                .scaleIn(maxHeight = maxHeight)
            val actual = assertNotNull(stream.decode())

            assertSimilar(expected, actual)
        }
    }

    @Test
    fun smallerThanMaxHeight() {
        val maxHeight = 9999999
        val res = appContext.resources
        allResources {
            val bitmap = BitmapFactory.decodeResource(res, it)
            val expected = scaleIn(bitmap, Int.MAX_VALUE, maxHeight)

            val stream = BitmapStream.fromResource(res, it)
                .scaleIn(maxHeight = maxHeight)
            val actual = assertNotNull(stream.decode())

            assertSimilar(expected, actual)
        }
    }
}