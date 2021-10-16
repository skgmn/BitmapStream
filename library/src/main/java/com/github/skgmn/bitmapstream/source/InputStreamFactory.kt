package com.github.skgmn.bitmapstream.source

import java.io.InputStream

fun interface InputStreamFactory {
    fun createInputStream(): InputStream
}