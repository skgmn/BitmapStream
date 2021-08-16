package com.github.skgmn.bytestream.util

import java.io.IOException
import java.io.InputStream
import kotlin.math.min

internal class RewindableInputStream(private val stream: InputStream) : InputStream() {
    private var recordBuffer: ByteArray = ByteArray(INITIAL_BUFFER_CAPACITY)
    private var recordBufferSize = 0
    private var replayOffset = 0
    private var markOffset = MARK_INVALID
    private var markLimit = 0
    private var recording = true
    private var recordingByMark = false
    private val singleByteBuffer by lazy { ByteArray(1) }

    @Synchronized
    override fun mark(readlimit: Int) {
        if (recording || replayOffset < recordBufferSize) {
            markOffset = replayOffset
            markLimit = readlimit
            recordingByMark = true
        } else {
            stream.mark(readlimit)
            markOffset = MARK_SOURCE_STREAM
        }
    }

    @Synchronized
    override fun reset() {
        when (markOffset) {
            MARK_INVALID -> throw IOException()
            MARK_SOURCE_STREAM -> stream.reset()
            else -> replayOffset = markOffset
        }
    }

    private fun record(b: ByteArray, offset: Int, count: Int) {
        val requiredBufferSize = recordBufferSize + count
        if (requiredBufferSize > recordBuffer.size) {
            val newRecordBuffer = ByteArray(Integer.highestOneBit(requiredBufferSize) shl 1)
            System.arraycopy(recordBuffer, 0, newRecordBuffer, 0, recordBufferSize)
            recordBuffer = newRecordBuffer
            this.recordBuffer = newRecordBuffer
        }
        System.arraycopy(b, offset, recordBuffer, recordBufferSize, count)
        recordBufferSize += count
        replayOffset = recordBufferSize
    }

    @Synchronized
    override fun read(): Int {
        val bytesRead = read(singleByteBuffer, 0, 1)
        return if (bytesRead == 1) {
            singleByteBuffer[0].toInt()
        } else {
            -1
        }
    }

    @Synchronized
    override fun markSupported(): Boolean = stream.markSupported()

    @Synchronized
    override fun read(b: ByteArray, byteOffset: Int, byteCount: Int): Int {
        var bytesRead = 0
        var bytesToRead = byteCount
        var targetOffset = byteOffset

        val bytesCanReadFromRecord = min(recordBufferSize - replayOffset, bytesToRead)
        if (bytesCanReadFromRecord > 0) {
            if (bytesToRead == 0) {
                return 0
            }
            System.arraycopy(recordBuffer, replayOffset, b, targetOffset, bytesCanReadFromRecord)
            replayOffset += bytesCanReadFromRecord
            targetOffset += bytesCanReadFromRecord
            bytesRead += bytesCanReadFromRecord
            bytesToRead -= bytesCanReadFromRecord
        }

        if (bytesToRead > 0 && recordingByMark) {
            val bytesToMarkEnd = markOffset + markLimit - recordBufferSize
            val bytesToReadThisTime = min(bytesToMarkEnd, bytesToRead)
            if (bytesToReadThisTime > 0) {
                val n = stream.read(b, targetOffset, bytesToReadThisTime)
                if (n > 0) {
                    record(b, targetOffset, n)
                    targetOffset += n
                    bytesRead += n
                    bytesToRead -= n
                    if (recordBufferSize >= markOffset + markLimit) {
                        recordingByMark = false
                    }
                } else if (bytesRead == 0) {
                    return -1
                }
            }
        }

        if (bytesToRead > 0) {
            val n = stream.read(b, targetOffset, bytesToRead)
            if (n > 0) {
                if (recording) {
                    record(b, targetOffset, n)
                } else if (markOffset != MARK_SOURCE_STREAM) {
                    markOffset = MARK_INVALID
                }
                bytesRead += n
            } else if (bytesRead == 0) {
                return -1
            }
        }
        return bytesRead
    }

    @Synchronized
    override fun close() {
        stream.close()
    }

    @Synchronized
    fun rewind() {
        replayOffset = 0
        markOffset = MARK_INVALID
        recordingByMark = false
    }

    @Synchronized
    fun stopRecording() {
        recording = false
    }

    companion object {
        private const val INITIAL_BUFFER_CAPACITY = 1024

        private const val MARK_INVALID = -1
        private const val MARK_SOURCE_STREAM = -2
    }
}