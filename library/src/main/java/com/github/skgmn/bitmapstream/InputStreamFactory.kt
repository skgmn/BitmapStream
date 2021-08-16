package com.github.skgmn.bitmapstream

import java.io.InputStream

fun interface InputStreamFactory {
    fun openInputStream(): InputStream
}