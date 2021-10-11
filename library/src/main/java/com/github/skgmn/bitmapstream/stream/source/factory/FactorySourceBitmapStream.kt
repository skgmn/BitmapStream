package com.github.skgmn.bitmapstream.stream.source.factory

import android.graphics.Bitmap
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.DecodingState
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.metadata.DecodedBitmapMetadata
import com.github.skgmn.bitmapstream.metadata.FactorySourceBitmapMetadata
import com.github.skgmn.bitmapstream.metadata.LazyBitmapMetadata
import com.github.skgmn.bitmapstream.source.factory.BitmapFactorySource
import com.github.skgmn.bitmapstream.stream.source.InputParameters
import com.github.skgmn.bitmapstream.stream.source.SourceBitmapStream
import com.github.skgmn.bitmapstream.stream.source.StreamFeatures
import java.util.concurrent.atomic.AtomicReference

internal class FactorySourceBitmapStream(
    private val source: BitmapFactorySource
) : SourceBitmapStream() {
    private val statefulMetadata = object : AtomicReference<BitmapMetadata>(), BitmapMetadata {
        init {
            set(LazyBitmapMetadata { lazy ->
                FactorySourceBitmapMetadata(source).also {
                    compareAndSet(lazy, it)
                }
            })
        }

        override val width: Int get() = get().width
        override val height: Int get() = get().height
        override val mimeType: String? get() = get().mimeType
        override val densityScale: Float get() = get().densityScale
    }

    override val metadata: BitmapMetadata get() = statefulMetadata

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        val metadata = statefulMetadata.get()
        if (metadata !is LazyBitmapMetadata) {
            if (width == metadata.width && height == metadata.height) {
                return this
            }
        }
        return super.scaleTo(width, height)
    }

    override fun scaleWidth(width: Int): BitmapStream {
        val metadata = statefulMetadata.get()
        if (metadata !is LazyBitmapMetadata) {
            if (width == metadata.width) {
                return this
            }
        }
        return super.scaleWidth(width)
    }

    override fun scaleHeight(height: Int): BitmapStream {
        val metadata = statefulMetadata.get()
        if (metadata !is LazyBitmapMetadata) {
            if (height == metadata.height) {
                return this
            }
        }
        return super.scaleHeight(height)
    }

    override fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream {
        val metadata = statefulMetadata.get()
        if (metadata !is LazyBitmapMetadata) {
            if (left == 0 && top == 0 && right == metadata.width && bottom == metadata.height) {
                return this
            }
        }
        return super.region(left, top, right, bottom)
    }

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