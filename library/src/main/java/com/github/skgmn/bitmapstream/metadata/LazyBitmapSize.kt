package com.github.skgmn.bitmapstream.metadata

internal class LazyBitmapSize(
    provider: (LazyBitmapSize) -> BitmapSize
) : BitmapSize {
    private val other by lazy { provider(this) }

    override val width: Int get() = other.width
    override val height: Int get() = other.height
}