package com.github.skgmn.bitmapstream.source

import com.github.skgmn.bitmapstream.stream.source.InputParameters

internal abstract class BitmapSource {
    abstract fun createDecodeSession(): DecodeSession
    open fun generateInputParameters(): InputParameters = InputParameters()
}