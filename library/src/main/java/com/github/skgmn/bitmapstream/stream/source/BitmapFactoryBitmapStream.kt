package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.metadata.BitmapSize
import com.github.skgmn.bitmapstream.metadata.DecodedBitmapSize
import com.github.skgmn.bitmapstream.metadata.LazyBitmapSize
import com.github.skgmn.bitmapstream.metadata.SessionBitmapSize
import com.github.skgmn.bitmapstream.source.BitmapSource
import com.github.skgmn.bitmapstream.source.DecodeSession
import com.github.skgmn.bitmapstream.util.characteristic
import com.github.skgmn.bitmapstream.util.scaleBy
import java.util.concurrent.atomic.AtomicReference

internal class BitmapFactoryBitmapStream(
    private val source: BitmapSource
) : SourceBitmapStream() {
    private val currentSession = AtomicReference<DecodeSession?>()

    private val statefulSize =
        object : AtomicReference<BitmapSize>(), BitmapSize {
            init {
                set(LazyBitmapSize { lazy ->
                    SessionBitmapSize(attachSession()).also {
                        compareAndSet(lazy, it)
                    }
                })
            }

            override val width: Int get() = get().width
            override val height: Int get() = get().height
        }

    override val size: BitmapSize get() = statefulSize

    private fun attachSession(): DecodeSession {
        while (true) {
            val oldSession = currentSession.get()
            if (oldSession != null) return oldSession

            val newSession = source.createDecodeSession()
            if (currentSession.compareAndSet(null, newSession)) {
                return newSession
            }
        }
    }

    private fun detachSession(): DecodeSession {
        return currentSession.getAndSet(null) ?: source.createDecodeSession()
    }

    override fun buildInputParameters(): InputParameters {
        return source.generateInputParameters()
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        val params = inputParameters.buildDecodingParameters(statefulSize)
        val region = params.region

        val bitmap = if (region == null ||
            // compare dimensions first to ensure it's been decoded first on regional decoding
            region.right == size.width && region.bottom == size.height &&
            region.left == 0 && region.top == 0
        ) {
            detachSession().decodeBitmap(params.options).also {
                val newMetadata = DecodedBitmapSize(params.options)
                do {
                    val current = statefulSize.get()
                    if (current !is LazyBitmapSize) break
                } while (!statefulSize.compareAndSet(current, newMetadata))
            }
        } else {
            detachSession().decodeBitmapRegion(region, params.options)
        }
        if (params.options.inTargetDensity != 0) {
            bitmap?.density = params.options.inTargetDensity
        }
        return bitmap
            ?.scaleBy(params.postScaleX, params.postScaleY)
            ?.characteristic(inputParameters.hardware, inputParameters.mutable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BitmapFactoryBitmapStream) return false

        if (source != other.source) return false

        return true
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }
}