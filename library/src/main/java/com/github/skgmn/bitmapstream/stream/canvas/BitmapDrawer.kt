package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.*
import android.graphics.drawable.Drawable
import com.github.skgmn.bitmapstream.BitmapStream
import kotlin.math.roundToInt

@Suppress("UNCHECKED_CAST")
internal class BitmapDrawer(
    override val width: Int,
    override val height: Int,
    private val regionLeft: Int,
    private val regionTop: Int,
    private val regionRight: Int,
    private val regionBottom: Int,
    private val scaleX: Float,
    private val scaleY: Float
) : DrawScope {
    private val records = mutableListOf<RecordEntry<*>>()
    private val matrix by lazy {
        Matrix().apply {
            postTranslate(-regionLeft * scaleX, -regionTop * scaleY)
            postScale(scaleX, scaleY)
        }
    }
    private val invertedMatrix by lazy {
        Matrix().also {
            matrix.invert(it)
        }
    }
    private val scaledRegion by lazy(LazyThreadSafetyMode.NONE) {
        Rect(
            (regionLeft * scaleX).roundToInt(),
            (regionTop * scaleY).roundToInt(),
            (regionRight * scaleX).roundToInt(),
            (regionBottom * scaleY).roundToInt()
        )
    }
    private val tempRect by lazy(LazyThreadSafetyMode.NONE) { Rect() }
    private val mapBuffer by lazy(LazyThreadSafetyMode.NONE) { RectF() }

    override fun draw(
        d: Drawable,
        srcLeft: Int,
        srcTop: Int,
        srcRight: Int,
        srcBottom: Int,
        destLeft: Int,
        destTop: Int,
        destRight: Int,
        destBottom: Int
    ) {
        val srcWidth = d.intrinsicWidth
        val srcHeight = d.intrinsicHeight
        if (srcWidth <= 0 || srcHeight <= 0) return

        if (srcLeft == 0 &&
            srcTop == 0 &&
            srcRight == srcWidth &&
            srcBottom == srcHeight
        ) {
            draw(d, destLeft, destTop, destRight, destBottom)
            return
        }

        tempRect.set(destLeft, destTop, destRight, destBottom)
        if (!getFinalBounds(tempRect)) return

        val visibleBounds = Rect(tempRect)
        if (DrawableUtils.isOpaque(d)) {
            removeRedundantRecords(visibleBounds)
        }
        val scaleX = (destRight - destLeft).toFloat() / (srcRight - srcLeft)
        val scaleY = (destBottom - destTop).toFloat() / (srcBottom - srcTop)
        val left = -(srcLeft * scaleX).roundToInt()
        val top = -(srcTop * scaleY).roundToInt()
        val right = (left + srcWidth * scaleX).roundToInt()
        val bottom = (top + srcHeight * scaleY).roundToInt()
        records += RecordEntry(visibleBounds) { canvas, _ ->
            d.setBounds(left, top, right, bottom)
            d.draw(canvas)
        }
    }

    override fun draw(d: Drawable, destLeft: Int, destTop: Int, destRight: Int, destBottom: Int) {
        tempRect.set(destLeft, destTop, destRight, destBottom)
        if (!getFinalBounds(tempRect)) return

        val visibleBounds = Rect(tempRect)
        if (DrawableUtils.isOpaque(d)) {
            removeRedundantRecords(visibleBounds)
        }
        records += RecordEntry(visibleBounds) { canvas, _ ->
            d.setBounds(destLeft, destTop, destRight, destBottom)
            d.draw(canvas)
        }
    }

    override fun draw(
        stream: BitmapStream,
        destLeft: Int,
        destTop: Int,
        destRight: Int,
        destBottom: Int,
        paint: Paint?
    ) {
        if (paint?.alpha == 0) return

        tempRect.set(destLeft, destTop, destRight, destBottom)
        if (!getFinalBounds(tempRect)) return

        val finalBounds = Rect(tempRect)
        val destRegionBounds = invertToSourceBounds(Rect(tempRect))

        val p = paint?.let { Paint(it) }
        val scaleX = (destRight - destLeft).toFloat() / stream.metadata.width
        val scaleY = (destBottom - destTop).toFloat() / stream.metadata.height
        val left = ((destRegionBounds.left - destLeft) / scaleX).roundToInt()
        val top = ((destRegionBounds.top - destTop) / scaleY).roundToInt()
        val right = ((destRegionBounds.right - destLeft) / scaleX).roundToInt()
        val bottom = ((destRegionBounds.bottom - destTop) / scaleY).roundToInt()
        records += RecordEntry(
            finalBounds,
            deferred = lazy(LazyThreadSafetyMode.NONE) {
                stream
                    .region(left, top, right, bottom)
                    .scaleTo(finalBounds.width(), finalBounds.height())
                    .downsampleOnly()
                    .decode()
                    ?.also {
                        if (!it.hasAlpha() && p?.alpha ?: 0xff == 0xff) {
                            removeRedundantRecords(finalBounds)
                        }
                    }
            },
            drawer = { canvas, bitmap ->
                tempRect.set(destLeft, destTop, destRight, destBottom)
                canvas.drawBitmap(bitmap, null, tempRect, p)
            }
        )
    }

    private fun getFinalBounds(bounds: Rect): Boolean {
        mapBuffer.set(bounds)
        matrix.mapRect(mapBuffer)
        bounds.set(mapBuffer)
        return bounds.intersect(scaledRegion)
    }

    private fun invertToSourceBounds(bounds: Rect): Rect {
        mapBuffer.set(bounds)
        invertedMatrix.mapRect(mapBuffer)
        bounds.set(mapBuffer)
        return bounds
    }

    private fun removeRedundantRecords(bounds: Rect) {
        records.removeAll {
            it.bounds != null && bounds.contains(it.bounds)
        }
    }

    internal fun makeBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(
            scaledRegion.width(),
            scaledRegion.height(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.setMatrix(matrix)

        val newRecords = mutableListOf<RecordEntry<Any>>()
        while (records.isNotEmpty()) {
            val record = records.removeLast() as RecordEntry<Any>
            if (record.deferred.value != null) {
                newRecords.add(0, record)
            }
        }
        records.addAll(newRecords)

        records.forEach { record ->
            record as RecordEntry<Any>
            record.deferred.value?.let {
                record.drawer(canvas, it)
            }
        }
        records.clear()

        return  bitmap
    }

    private fun Rect.set(rectF: RectF) {
        set(
            rectF.left.roundToInt(),
            rectF.top.roundToInt(),
            rectF.right.roundToInt(),
            rectF.bottom.roundToInt()
        )
    }
}