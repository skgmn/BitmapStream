package com.github.skgmn.bitmapstream.source

import okio.Source

fun interface SourceFactory {
    fun createSource(): Source
}