package com.github.skgmn.bitmapstream.stream

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.*
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.metadata.DecodedBitmapMetadata
import com.github.skgmn.bitmapstream.metadata.LazyBitmapMetadata
import com.github.skgmn.bitmapstream.metadata.SourceBitmapMetadata
import java.util.concurrent.atomic.AtomicReference

internal class SourceBitmapStream(
    internal val source: BitmapSource
) : BitmapStream() {
    private val statefulMetadata = object : AtomicReference<BitmapMetadata>(), BitmapMetadata {
        init {
            set(LazyBitmapMetadata { lazy ->
                SourceBitmapMetadata(source).also {
                    compareAndSet(lazy, it)
                }
            })
        }

        override val width: Int get() = get().width
        override val height: Int get() = get().height
        override val mimeType: String? get() = get().mimeType
        override val densityScale: Float get() = get().densityScale
    }

    override val features = object : StreamFeatures {
        override val regional: Boolean
            get() = false
    }

    override val metadata: BitmapMetadata = statefulMetadata

    override fun buildInputParameters(features: StreamFeatures): InputParameters {
        return source.generateInputParameters(features, metadata)
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        val state = source.createNewDecodingState()
        state?.setPhase(DecodingState.PHASE_METADATA)

        val params = inputParameters.buildDecodingParameters()
        if (params.region != null) {
            metadata.width
        }

        state?.setPhase(DecodingState.PHASE_BITMAP)
        try {
            val bitmap = if (params.region != null) {
                source.decodeBitmapRegion(params.region, params.options)
            } else {
                source.decodeBitmap(params.options).also {
                    val newMetadata = DecodedBitmapMetadata(params.options)
                    do {
                        val current = statefulMetadata.get()
                        if (current !is LazyBitmapMetadata) break
                    } while (!statefulMetadata.compareAndSet(current, newMetadata))
                }
            }
            return setMutable(postProcess(bitmap, params) ?: return null, inputParameters.mutable)
        } finally {
            state?.setPhase(DecodingState.PHASE_COMLETE)
        }
    }
}