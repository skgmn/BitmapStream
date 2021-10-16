package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.StreamFeatures
import com.github.skgmn.bitmapstream.metadata.DecodedBitmapMetadata
import com.github.skgmn.bitmapstream.metadata.ExtendedBitmapMetadata
import com.github.skgmn.bitmapstream.metadata.LazyBitmapMetadata
import com.github.skgmn.bitmapstream.metadata.SessionBitmapMetadata
import com.github.skgmn.bitmapstream.source.BitmapSource
import com.github.skgmn.bitmapstream.source.DecodeSession
import com.github.skgmn.bitmapstream.util.characteristic
import com.github.skgmn.bitmapstream.util.scaleBy
import java.util.concurrent.atomic.AtomicReference

internal class BitmapFactoryBitmapStream(
    private val source: BitmapSource
) : SourceBitmapStream() {
    private val currentSession = AtomicReference<DecodeSession?>()

    private val statefulMetadata =
        object : AtomicReference<ExtendedBitmapMetadata>(), ExtendedBitmapMetadata {
            init {
                set(LazyBitmapMetadata { lazy ->
                    SessionBitmapMetadata(peekSession()).also {
                        compareAndSet(lazy, it)
                    }
                })
            }

            override val width: Int get() = get().width
            override val height: Int get() = get().height
            override val mimeType: String? get() = get().mimeType
            override val densityScale: Float get() = get().densityScale
        }

    override val hasDimensions get() = statefulMetadata.get() is DecodedBitmapMetadata
    override val metadata: ExtendedBitmapMetadata get() = statefulMetadata

    private fun peekSession(): DecodeSession {
        while (true) {
            val oldSession = currentSession.get()
            if (oldSession != null) return oldSession

            val newSession = source.createDecodeSession()
            if (currentSession.compareAndSet(null, newSession)) {
                return newSession
            }
        }
    }

    private fun getSession(): DecodeSession {
        return currentSession.getAndSet(null) ?: source.createDecodeSession()
    }

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return source.generateInputParameters(features, metadata)
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        val session = getSession()

        val params = inputParameters.buildDecodingParameters()
        if (params.region != null) {
            metadata.width
        }

        val bitmap = if (params.region != null) {
            session.decodeBitmapRegion(params.region, params.options)
        } else {
            session.decodeBitmap(params.options).also {
                val newMetadata = DecodedBitmapMetadata(params.options)
                do {
                    val current = statefulMetadata.get()
                    if (current !is LazyBitmapMetadata) break
                } while (!statefulMetadata.compareAndSet(current, newMetadata))
            }
        }
        return bitmap
            ?.scaleBy(params.postScaleX, params.postScaleY)
            ?.characteristic(inputParameters.hardware, inputParameters.mutable)
    }
}