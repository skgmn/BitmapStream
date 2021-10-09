package com.github.skgmn.bitmapstream.stream.source

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.github.skgmn.bitmapstream.BitmapStream
import com.github.skgmn.bitmapstream.frame.FrameMethod
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.util.AspectRatioCalculator
import kotlin.math.roundToInt

internal class FrameBitmapStream(
    other: SourceBitmapStream,
    private val frameWidth: Int,
    private val frameHeight: Int,
    private val frameMethod: FrameMethod
) : DelegateBitmapStream(other) {
    override val metadata = object : BitmapMetadata {
        override val width: Int get() = frameWidth
        override val height: Int get() = frameHeight
        override val mimeType: String? get() = other.metadata.mimeType
        override val densityScale: Float get() = other.metadata.densityScale
    }

    override fun scaleTo(width: Int, height: Int): BitmapStream {
        return if (width == frameWidth && height == frameHeight) {
            this
        } else {
            other.scaleTo(width, height)
        }
    }

    override fun scaleWidth(width: Int): BitmapStream {
        return if (width == frameWidth) {
            this
        } else {
            val newHeight = AspectRatioCalculator.getHeight(frameWidth, frameHeight, width)
            FrameBitmapStream(other, width, newHeight, frameMethod)
        }
    }

    override fun scaleHeight(height: Int): BitmapStream {
        return if (height == frameHeight) {
            this
        } else {
            val newWidth = AspectRatioCalculator.getWidth(frameWidth, frameHeight, height)
            FrameBitmapStream(other, newWidth, height, frameMethod)
        }
    }

    override fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream {
        return if (scaleWidth == 1f && scaleHeight == 1f) {
            this
        } else if (scaleWidth == scaleHeight) {
            FrameBitmapStream(
                other,
                (frameWidth * scaleWidth).roundToInt(),
                (frameHeight * scaleHeight).roundToInt(),
                frameMethod
            )
        } else {
            other.scaleBy(scaleWidth, scaleHeight)
        }
    }

    override fun decode(inputParameters: InputParameters): Bitmap? {
        return setMutable(decodeBitmap(inputParameters) ?: return null, inputParameters.mutable)
    }

    private fun decodeBitmap(inputParameters: InputParameters): Bitmap? {
        val srcRect = Rect()
        val destRect = Rect()
        val targetWidth = (frameWidth * inputParameters.scaleX).roundToInt()
        val targetHeight = (frameHeight * inputParameters.scaleY).roundToInt()
        frameMethod.computeBounds(other.metadata, targetWidth, targetHeight, srcRect, destRect)

        val scaleX = destRect.width().toFloat() / srcRect.width()
        val scaleY = destRect.height().toFloat() / srcRect.height()

        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)

        inputParameters.region?.let { r ->
            val destIntersection = Rect(destRect)
            if (!destIntersection.intersect(r)) {
                return bitmap
            }
            val scaledSrcLeft = ((destIntersection.left - destRect.left) / scaleX).roundToInt()
            val scaledSrcTop = ((destIntersection.top - destRect.top) / scaleY).roundToInt()
            val scaledSrcRight = (scaledSrcLeft + destIntersection.width() / scaleX).roundToInt()
            val scaledSrcBottom = (scaledSrcTop + destIntersection.height() / scaleY).roundToInt()
            val srcBitmap =
                other.region(scaledSrcLeft, scaledSrcTop, scaledSrcRight, scaledSrcBottom).decode()
                    ?: return setMutable(bitmap, inputParameters.mutable)
            val canvas = Canvas(bitmap)
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(
                srcBitmap,
                destIntersection.left.toFloat(),
                destIntersection.top.toFloat(),
                paint
            )
            srcBitmap.recycle()
            return bitmap
        }

        val srcBitmapStream =
            if (srcRect.left == 0 && srcRect.top == 0 &&
                srcRect.width() == other.metadata.width && srcRect.height() == other.metadata.height
            ) {
                other
            } else {
                other.region(srcRect)
            }
        val srcBitmap = srcBitmapStream.decode() ?: return bitmap
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(srcBitmap, null, destRect, paint)

        return bitmap
    }
}