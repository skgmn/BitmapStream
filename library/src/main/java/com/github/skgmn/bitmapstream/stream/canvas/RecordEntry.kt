package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.RectF

private val unitLazy = lazy(LazyThreadSafetyMode.NONE) { }

internal class RecordEntry<T : Any>(
    val bounds: RectF?,
    val deferred: Lazy<T?>,
    val drawer: (T) -> Unit
)

internal fun RecordEntry(drawer: (Unit) -> Unit) = RecordEntry(
    null, unitLazy, drawer
)

internal fun RecordEntry(bounds: RectF, drawer: (Unit) -> Unit) = RecordEntry(
    bounds, unitLazy, drawer
)