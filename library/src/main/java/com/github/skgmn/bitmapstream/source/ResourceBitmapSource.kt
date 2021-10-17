package com.github.skgmn.bitmapstream.source

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
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

    override fun generateInputParameters(
        features: StreamFeatures,
        metadata: BitmapMetadata
    ): InputParameters {
        return if (features.regional) {
            val densityScale = metadata.densityScale
            InputParameters(
                scaleX = densityScale,
                scaleY = densityScale
            )
        } else {
            super.generateInputParameters(features, metadata)
        }
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