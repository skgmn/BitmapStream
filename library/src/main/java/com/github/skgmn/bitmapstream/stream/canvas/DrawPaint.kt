package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode

class DrawPaint private constructor(
    internal val paint: Paint
) {
    constructor() : this(Paint())
    constructor(other: DrawPaint) : this(Paint(other.paint)) {
        porterDuffMode = other.porterDuffMode
    }

    constructor(
        antialias: Boolean = false,
        filterBitmap: Boolean = false
    ) : this(
        Paint(
            (if (antialias) Paint.ANTI_ALIAS_FLAG else 0) or
                    (if (filterBitmap) Paint.FILTER_BITMAP_FLAG else 0)
        )
    )

    var antialias: Boolean
        get() = paint.isAntiAlias
        set(value) {
            paint.isAntiAlias = value
        }

    var filterBitmap: Boolean
        get() = paint.isFilterBitmap
        set(value) {
            paint.isFilterBitmap = value
        }

    var alpha: Int
        get() = paint.alpha
        set(value) {
            paint.alpha = value
        }

    var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
        }

    var porterDuffMode: PorterDuff.Mode? = null
        set(value) {
            if (field != value) {
                field = value
                if (value == null) {
                    paint.xfermode = null
                } else {
                    paint.xfermode = PorterDuffXfermode(value)
                }
            }
        }

    var style: Paint.Style
        get() = paint.style
        set(value) {
            paint.style = value
        }

    internal fun isOpaque(): Boolean {
        return alpha == 0xff &&
                (porterDuffMode == null ||
                        porterDuffMode == PorterDuff.Mode.SRC ||
                        porterDuffMode == PorterDuff.Mode.DST_ATOP ||
                        porterDuffMode == PorterDuff.Mode.ADD)
    }
}