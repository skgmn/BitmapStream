package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.core.graphics.drawable.toBitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.BitmapTestBase
import com.github.skgmn.bitmapstream.test.R
import org.junit.Test

class VectorTest : BitmapTestBase() {
    @Test
    fun vectorRegionScale() {
        val vector = assertNotNull(appContext.getDrawable(R.drawable.vector_sample))
        var expected = vector.toBitmap()
        val m = Matrix()
        m.postScale(0.7f, 0.8f)
        expected = Bitmap.createBitmap(expected, 100, 150, 200, 300, m, true)

        val stream = BitmapStream.fromDrawable(vector)
            .region(100, 150, 100 + 200, 150 + 300)
            .scaleBy(0.7f, 0.8f)
        val actual = assertNotNull(stream.decode())

        assertSimilar(expected, actual)
    }
}