package com.github.skgmn.bitmapstream.stream

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.GuardedBy
import com.github.skgmn.bitmapstream.BitmapSource
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.DecodingState
import com.github.skgmn.bitmapstream.InputParameters

internal class SourceBitmapStream(
    internal val source: BitmapSource
) : BitmapStream() {
    private val boundsDecodeLock = Any()

    @GuardedBy("boundsDecodeLock")
    private var widthDecoded = -1
    @GuardedBy("boundsDecodeLock")
    private var heightDecoded = -1
    @GuardedBy("boundsDecodeLock")
    private var mimeTypeDecoded = ""
    @GuardedBy("boundsDecodeLock")
    private var densityScale = 1f

    private val boundsDecoded
        @GuardedBy("boundsDecodeLock")
        get() = widthDecoded != -1

    override val width: Int
        get() {
            synchronized(boundsDecodeLock) {
                decodeBounds()
                return widthDecoded
            }
        }
    override val height: Int
        get() {
            synchronized(boundsDecodeLock) {
                decodeBounds()
                return heightDecoded
            }
        }
    override val mimeType: String
        get() {
            synchronized(boundsDecodeLock) {
                decodeBounds()
                return mimeTypeDecoded
            }
        }

    @GuardedBy("boundsDecodeLock")
    private fun decodeBounds() {
        if (boundsDecoded) {
            return
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        source.decodeBitmap(options)
        copyMetadata(options)
    }

    @GuardedBy("boundsDecodeLock")
    private fun copyMetadata(options: BitmapFactory.Options) {
        widthDecoded = options.outWidth
        heightDecoded = options.outHeight
        mimeTypeDecoded = options.outMimeType
        if (options.inScaled && options.inDensity != 0 && options.inTargetDensity != 0) {
            densityScale = options.inTargetDensity.toFloat() / options.inDensity
        }
    }

    override fun buildInputParameters(regional: Boolean): InputParameters {
        return if (regional && source.manualDensityScalingForRegional) {
            synchronized(boundsDecodeLock) {
                decodeBounds()
                InputParameters(
                    scaleX = densityScale,
                    scaleY = densityScale
                )
            }
        } else {
            InputParameters()
        }
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        val state = source.createNewDecodingState()
        state?.setPhase(DecodingState.PHASE_METADATA)

        val params = inputParameters.buildDecodingParameters()
        if (params.region != null) {
            synchronized(boundsDecodeLock) {
                decodeBounds()
            }
        }

        state?.setPhase(DecodingState.PHASE_BITMAP)
        try {
            val bitmap = if (params.region != null) {
                source.decodeBitmapRegion(params.region, params.options)
            } else {
                source.decodeBitmap(params.options).also {
                    synchronized(boundsDecodeLock) {
                        if (!boundsDecoded) {
                            copyMetadata(params.options)
                        }
                    }
                }
            }
            return postProcess(bitmap, params)
        } finally {
            state?.setPhase(DecodingState.PHASE_COMLETE)
        }
    }
}