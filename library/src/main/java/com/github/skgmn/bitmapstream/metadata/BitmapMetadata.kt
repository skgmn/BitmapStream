package com.github.skgmn.bitmapstream.metadata

interface BitmapMetadata {
    val width: Int
    val height: Int
    val mimeType: String?
    val densityScale: Float
}