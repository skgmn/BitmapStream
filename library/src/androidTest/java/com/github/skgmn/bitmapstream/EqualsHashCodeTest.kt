package com.github.skgmn.bitmapstream

import com.github.skgmn.bitmapstream.test.R
import org.junit.Assert.assertEquals
import org.junit.Test

class EqualsHashCodeTest : BitmapTestBase() {
    @Test
    fun resourceScaleToRegionScaleBy() {
        val res = appContext.resources
        val a = BitmapStream.fromResource(res, R.drawable.nodpi_image)
            .scaleTo(101, 102)
            .region(1, 2, 3, 4)
            .scaleBy(1.1f, 2.2f)
        val b = BitmapStream.fromResource(res, R.drawable.nodpi_image)
            .scaleTo(101, 102)
            .region(1, 2, 3, 4)
            .scaleBy(1.1f, 2.2f)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a, b)
    }
}