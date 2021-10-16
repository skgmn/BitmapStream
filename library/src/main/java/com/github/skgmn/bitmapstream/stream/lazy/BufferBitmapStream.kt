package com.github.skgmn.bitmapstream.stream.lazy

import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.stream.inmemory.InMemoryBitmapStream

internal class BufferBitmapStream(
    private val other: BitmapStream
) : LazyBitmapStream() {
    override val metadata get() = other.metadata
    override val simulatedWidth get() = other.metadata.width.toDouble()
    override val simulatedHeight get() = other.metadata.height.toDouble()

    override fun buffer(): BitmapStream {
        return this
    }

    override fun buildStream(): BitmapStream? {
        return other.decode()?.let { InMemoryBitmapStream(it) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BufferBitmapStream) return false

        if (this.other != other.other) return false

        return true
    }

    override fun hashCode(): Int {
        return other.hashCode()
    }
}