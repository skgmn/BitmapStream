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
    private val scaledRegion by lazy(LazyThreadSafetyMode.NONE) {
        Rect(
            (regionLeft * scaleX).roundToInt(),
            (regionTop * scaleY).roundToInt(),
            (regionRight * scaleX).roundToInt(),
            (regionBottom * scaleY).roundToInt()
        )
    }
    private val tempRect by lazy(LazyThreadSafetyMode.NONE) { Rect() }
    private val tempRectF by lazy(LazyThreadSafetyMode.NONE) { RectF() }

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
        val drawableWidth = d.intrinsicWidth
        val drawableHeight = d.intrinsicHeight
        if (drawableWidth <= 0 || drawableHeight <= 0) return

        if (srcLeft == 0 &&
            srcTop == 0 &&
            srcRight == drawableWidth &&
            srcBottom == drawableHeight
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
        val right = (left + drawableWidth * scaleX).roundToInt()
        val bottom = (top + drawableHeight * scaleY).roundToInt()
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
        paint: DrawPaint?
    ) {
        if (paint?.alpha == 0) return

        tempRect.set(destLeft, destTop, destRight, destBottom)
        if (!getFinalBounds(tempRect)) return

        val finalBounds = Rect(tempRect)
        val unscaledDestBounds = unscaleBounds(Rect(finalBounds))

        val p = paint?.let { DrawPaint(it) }
        val scaleX = (destRight - destLeft).toFloat() / stream.size.width
        val scaleY = (destBottom - destTop).toFloat() / stream.size.height
        val left = ((unscaledDestBounds.left - destLeft) / scaleX).roundToInt()
        val top = ((unscaledDestBounds.top - destTop) / scaleY).roundToInt()
        val right = ((unscaledDestBounds.right - destLeft) / scaleX).roundToInt()
        val bottom = ((unscaledDestBounds.bottom - destTop) / scaleY).roundToInt()
        records += RecordEntry(
            finalBounds,
            deferred = lazy(LazyThreadSafetyMode.NONE) {
                stream
                    .region(left, top, right, bottom)
                    .scaleTo(finalBounds.width(), finalBounds.height())
                    .downsampleOnly()
                    .mutable(null)
                    .hardware(false)
                    .decode()
                    ?.also {
                        if (!it.hasAlpha() && p?.isOpaque() != false) {
                            removeRedundantRecords(finalBounds)
                        }
                    }
            },
            drawer = { canvas, bitmap ->
                canvas.drawBitmap(bitmap, null, unscaledDestBounds, p?.paint)
            }
        )
    }

    override fun draw(
        bitmap: Bitmap,
        destLeft: Int,
        destTop: Int,
        destRight: Int,
        destBottom: Int,
        paint: DrawPaint?
    ) {
        tempRect.set(destLeft, destTop, destRight, destBottom)
        if (!getFinalBounds(tempRect)) return

        val finalBounds = Rect(tempRect)
        if (!bitmap.hasAlpha()) {
            removeRedundantRecords(finalBounds)
        }
        val p = paint?.let { DrawPaint(it) }
        records += RecordEntry(finalBounds) { canvas, _ ->
            tempRect.set(destLeft, destTop, destRight, destBottom)
            canvas.drawBitmap(bitmap, null, tempRect, p?.paint)
        }
    }

    override fun draw(
        bitmap: Bitmap,
        srcLeft: Int,
        srcTop: Int,
        srcRight: Int,
        srcBottom: Int,
        destLeft: Int,
        destTop: Int,
        destRight: Int,
        destBottom: Int,
        paint: DrawPaint?
    ) {
        if (srcLeft == 0 &&
            srcTop == 0 &&
            srcRight == bitmap.width &&
            srcBottom == bitmap.height
        ) {
            draw(bitmap, destLeft, destTop, destRight, destBottom)
            return
        }

        tempRect.set(destLeft, destTop, destRight, destBottom)
        if (!getFinalBounds(tempRect)) return

        val finalBounds = Rect(tempRect)
        if (!bitmap.hasAlpha()) {
            removeRedundantRecords(finalBounds)
        }
        val p = paint?.let { DrawPaint(it) }
        records += RecordEntry(finalBounds) { canvas, _ ->
            tempRect.set(srcLeft, srcTop, srcRight, srcBottom)
            val destRect = Rect(destLeft, destTop, destRight, destBottom)
            canvas.drawBitmap(bitmap, tempRect, destRect, p?.paint)
        }
    }

    override fun drawOval(left: Int, top: Int, right: Int, bottom: Int, paint: DrawPaint) {
        tempRect.set(left, top, right, bottom)
        if (!getFinalBounds(tempRect)) return
        val finalBounds = Rect(tempRect)
        val p = DrawPaint(paint)
        records += RecordEntry(
            bounds = finalBounds,
            drawer = { canvas, _ ->
                tempRectF.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
                canvas.drawOval(tempRectF, p.paint)
            }
        )
    }

    override fun drawPath(path: Path, paint: DrawPaint) {
        path.computeBounds(tempRectF, true)
        tempRect.set(tempRectF)
        if (!getFinalBounds(tempRect)) return
        val finalBounds = Rect(tempRect)
        val path2 = Path(path)
        val p = DrawPaint(paint)
        records += RecordEntry(
            bounds = finalBounds,
            drawer = { canvas, _ ->
                canvas.drawPath(path2, p.paint)
            }
        )
    }

    override fun drawRoundRect(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        rx: Float,
        ry: Float,
        paint: DrawPaint
    ) {
        tempRect.set(left, top, right, bottom)
        if (!getFinalBounds(tempRect)) return
        val finalBounds = Rect(tempRect)
        val p = DrawPaint(paint)
        records += RecordEntry(
            bounds = finalBounds,
            drawer = { canvas, _ ->
                tempRectF.set(left, top, right, bottom)
                canvas.drawRoundRect(tempRectF, rx, ry, p.paint)
            }
        )
    }

    private fun getFinalBounds(bounds: Rect): Boolean {
        bounds.left = (bounds.left * scaleX).roundToInt()
        bounds.top = (bounds.top * scaleY).roundToInt()
        bounds.right = (bounds.right * scaleX).roundToInt()
        bounds.bottom = (bounds.bottom * scaleY).roundToInt()
        return bounds.intersect(scaledRegion)
    }

    private fun unscaleBounds(bounds: Rect): Rect {
        bounds.left = (bounds.left / scaleX).roundToInt()
        bounds.top = (bounds.top / scaleY).roundToInt()
        bounds.right = (bounds.right / scaleX).roundToInt()
        bounds.bottom = (bounds.bottom / scaleY).roundToInt()
        return bounds
    }

    private fun removeRedundantRecords(bounds: Rect) {
        records.removeAll {
            it.bounds != null && bounds.contains(it.bounds)
        }
    }

    private fun Rect.set(rectF: RectF) {
        set(
            rectF.left.roundToInt(),
            rectF.top.roundToInt(),
            rectF.right.roundToInt(),
            rectF.bottom.roundToInt()
        )
    }

    private fun RectF.set(left: Int, top: Int, right: Int, bottom: Int) {
        set(
            left.toFloat(),
            top.toFloat(),
            right.toFloat(),
            bottom.toFloat()
        )
    }

    internal fun makeBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(
            scaledRegion.width(),
            scaledRegion.height(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.translate(-regionLeft * scaleX, -regionTop * scaleY)
        canvas.scale(scaleX, scaleY)

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
}