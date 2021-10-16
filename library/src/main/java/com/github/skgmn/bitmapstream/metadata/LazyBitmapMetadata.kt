package com.github.skgmn.bitmapstream.metadata

internal class LazyBitmapMetadata(
    provider: (LazyBitmapMetadata) -> ExtendedBitmapMetadata
) : ExtendedBitmapMetadata {
    private val other by lazy { provider(this) }

    override val width: Int get() = other.width
    override val height: Int get() = other.height
    override val mimeType: String? get() = other.mimeType
    override val densityScale: Float get() = other.densityScale
}