package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.Canvas
import android.graphics.RectF

private val unitLazy = lazy(LazyThreadSafetyMode.NONE) { }

internal class RecordEntry<T : Any>(
    val bounds: RectF?,
    val deferred: Lazy<T?>,
    val drawer: Canvas.(T) -> Unit
)

internal fun RecordEntry(drawer: Canvas.(Unit) -> Unit) = RecordEntry(
    null, unitLazy, drawer
)

internal fun RecordEntry(bounds: RectF, drawer: Canvas.(Unit) -> Unit) = RecordEntry(
    bounds, unitLazy, drawer
)