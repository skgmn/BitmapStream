package com.github.skgmn.bitmapstream.util

import com.github.skgmn.bytestream.util.RewindableInputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class RewindableInputStreamTest {
    private lateinit var bytes: ByteArray
    private lateinit var stream: RewindableInputStream

    @Before
    fun setUp()  {
        bytes = ByteArray(8192) { (it % 256).toByte() }
        stream = RewindableInputStream(ByteArrayInputStream(bytes))
    }

    @Test
    fun rewind() {
        val buffer = ByteArray(300)
        var n = stream.read(buffer, 0, buffer.size)
        assertEquals(buffer.size, n)
        assertTrue(bytes.sliceArray(buffer.indices).contentEquals(buffer))

        val moreBuffer = ByteArray(400)
        stream.rewind()
        stream.stopRecording()
        n = stream.read(moreBuffer, 0, moreBuffer.size)
        assertEquals(moreBuffer.size, n)
        assertTrue(bytes.sliceArray(moreBuffer.indices).contentEquals(moreBuffer))
    }

    @Test
    fun markReset() {
        stream.skip(100)
        stream.mark(23)
        stream.skip(99)
        stream.reset()

        val buffer = ByteArray(300)
        var n = stream.read(buffer, 0, buffer.size)
        assertEquals(buffer.size, n)
        assertTrue(bytes.sliceArray(100 until 400).contentEquals(buffer))

        stream.rewind()
        stream.stopRecording()

        stream.skip(350)
        stream.mark(100)
        stream.skip(100)
        stream.reset()

        n = stream.read(buffer, 0, buffer.size)
        assertEquals(buffer.size, n)
        assertTrue(bytes.sliceArray(350 until 350 + buffer.size).contentEquals(buffer))
    }
}