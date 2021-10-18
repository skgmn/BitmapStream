package com.github.skgmn.bitmapstream.source

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.util.TypedValue
import com.github.skgmn.bitmapstream.stream.source.InputParameters

internal class ResourceBitmapSource(
    private val res: Resources,
    private val id: Int
) : BitmapSource() {
    override fun createDecodeSession() = object : DecodeSession {
        override fun decodeBitmap(options: BitmapFactory.Options): Bitmap? {
            return BitmapFactory.decodeResource(res, id, options)
        }

        override fun decodeBitmapRegion(region: Rect, options: BitmapFactory.Options): Bitmap? {
            res.openRawResource(id).use { inputStream ->
                val regionDecoder = BitmapRegionDecoder.newInstance(inputStream, false)
                return regionDecoder.decodeRegion(region, options)
            }
        }
    }

    override fun generateInputParameters(): InputParameters {
        val value = TypedValue()
        res.getValue(id, value, false)

        val displayDensity = res.displayMetrics.densityDpi
        val densityScale = if (value.density == TypedValue.DENSITY_NONE ||
            value.density == TypedValue.DENSITY_DEFAULT ||
            displayDensity == 0
        ) {
            1f
        } else {
            displayDensity.toFloat() / value.density
        }
        return InputParameters(
            scaleX = densityScale,
            scaleY = densityScale,
            targetDensity = displayDensity
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResourceBitmapSource) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}