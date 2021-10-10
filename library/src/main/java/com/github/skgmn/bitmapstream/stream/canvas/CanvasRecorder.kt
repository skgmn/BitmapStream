package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.*
import android.graphics.text.MeasuredText
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorLong
import androidx.annotation.RequiresApi
import com.github.skgmn.bitmapstream.BitmapStream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Suppress("DEPRECATION", "UNCHECKED_CAST")
internal class CanvasRecorder internal constructor(
    private val canvasWidth: Int,
    private val canvasHeight: Int
) : Canvas() {
    private val records = mutableListOf<RecordEntry<*>>()
    private val layers = mutableListOf(SimulatedLayer(0, Region(0, 0, canvasWidth, canvasHeight)))
    private var drawFilter: DrawFilter? = null
    private var density: Int = Bitmap.DENSITY_NONE
    private val tempRect by lazy(LazyThreadSafetyMode.NONE) { RectF() }
    private val tempPath by lazy(LazyThreadSafetyMode.NONE) { Path() }

    private val currentLayer: SimulatedLayer get() = layers.last()
    private val currentClipBounds get() = RectF(currentLayer.clipRegion.bounds)

    private fun newLayer(): SimulatedLayer {
        return currentLayer.run {
            copy(index = index + 1)
        }.also { layers += it }
    }

    private fun popLayersTo(index: Int): SimulatedLayer? {
        val listIndex = layers.indexOfFirst { it.index >= index }
        return if (listIndex <= 0) {
            null
        } else {
            val layer = layers[listIndex]
            layers.subList(listIndex, layers.size).clear()
            layer
        }
    }

    private fun popLayer(): SimulatedLayer? {
        return if (layers.size <= 1) {
            null
        } else {
            layers.removeLast()
        }
    }

    private fun removeRedundantRecords(bounds: RectF) {
        records.removeAll {
            it.bounds != null && bounds.contains(it.bounds)
        }
    }

    internal fun runDeferred() {
        val newRecords = mutableListOf<RecordEntry<Any>>()
        while (records.isNotEmpty()) {
            val record = records.removeLast() as RecordEntry<Any>
            if (record.deferred.value != null) {
                newRecords.add(0, record)
            }
        }
        records.addAll(newRecords)
    }

    internal fun drawTo(canvas: Canvas) {
        val saveCount = canvas.save()
        records.forEach { record ->
            record as RecordEntry<Any>
            record.deferred.value?.let {
                record.drawer(canvas, it)
            }
        }
        canvas.restoreToCount(saveCount)
    }

    internal fun drawStream(stream: BitmapStream, left: Float, top: Float, paint: Paint?) {
        if (paint?.alpha == 0) return
        val metadata = stream.metadata
        drawStream(stream, RectF(left, top, left + metadata.width, top + metadata.height), paint)
    }

    internal fun drawStream(stream: BitmapStream, dst: RectF, paint: Paint?) {
        if (paint?.alpha == 0) return
        tempRect.set(dst)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val visibleBounds = RectF(tempRect)
        val invertedBounds = if (visibleBounds == dst) {
            visibleBounds
        } else {
            RectF(visibleBounds).also { currentLayer.invert(it) }
        }
        val p = paint?.let { Paint(it) }
        val scaleX = dst.width() / stream.metadata.width
        val scaleY = dst.height() / stream.metadata.height
        records += RecordEntry(
            visibleBounds,
            deferred = lazy(LazyThreadSafetyMode.NONE) {
                stream
                    .region(
                        ((invertedBounds.left - dst.left) / scaleX).roundToInt(),
                        ((invertedBounds.top - dst.top) / scaleY).roundToInt(),
                        ((invertedBounds.right - dst.left) / scaleX).roundToInt(),
                        ((invertedBounds.bottom - dst.top) / scaleY).roundToInt()
                    )
                    .scaleBy(
                        scaleX * visibleBounds.width() / invertedBounds.width(),
                        scaleY * visibleBounds.height() / invertedBounds.height()
                    )
                    .downsampleOnly()
                    .decode()
                    ?.also {
                        if (!it.hasAlpha()) {
                            removeRedundantRecords(visibleBounds)
                        }
                    }
            },
            drawer = {
                drawBitmap(it, null, invertedBounds, p)
            }
        )
    }

    override fun drawArc(
        left: Float, top: Float, right: Float, bottom: Float, startAngle: Float,
        sweepAngle: Float, useCenter: Boolean, paint: Paint
    ) {
        if (paint.alpha == 0) return
        tempRect.set(left, top, right, bottom)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, p)
        }
    }

    override fun drawArc(
        oval: RectF, startAngle: Float, sweepAngle: Float, useCenter: Boolean,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        tempRect.set(oval)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val bounds = RectF(oval)
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawArc(bounds, startAngle, sweepAngle, useCenter, p)
        }
    }

    override fun drawARGB(a: Int, r: Int, g: Int, b: Int) {
        val bounds = currentClipBounds
        if (a == 0xff) {
            removeRedundantRecords(bounds)
        }
        records += RecordEntry(bounds) {
            drawARGB(a, r, g, b)
        }
    }

    override fun drawBitmap(bitmap: Bitmap, left: Float, top: Float, paint: Paint?) {
        if (paint?.alpha == 0) return
        tempRect.set(left, top, left + bitmap.width, top + bitmap.height)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        if (!bitmap.hasAlpha() && (paint == null || paint.alpha == 0xff)) {
            removeRedundantRecords(tempRect)
        }
        val p = paint?.let { Paint(it) }
        records += RecordEntry(RectF(tempRect)) {
            drawBitmap(bitmap, left, top, p)
        }
    }

    override fun drawBitmap(
        bitmap: Bitmap, src: Rect?, dst: RectF,
        paint: Paint?
    ) {
        if (paint?.alpha == 0) return
        tempRect.set(dst)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        if (!bitmap.hasAlpha() && (paint == null || paint.alpha == 0xff)) {
            removeRedundantRecords(tempRect)
        }
        val src2 = src?.let { Rect(it) }
        val dst2 = RectF(dst)
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawBitmap(bitmap, src2, dst2, p)
        }
    }

    override fun drawBitmap(
        bitmap: Bitmap, src: Rect?, dst: Rect,
        paint: Paint?
    ) {
        if (paint?.alpha == 0) return
        tempRect.set(dst)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        if (!bitmap.hasAlpha() && paint?.alpha ?: 0xff == 0xff) {
            removeRedundantRecords(tempRect)
        }
        val src2 = src?.let { Rect(it) }
        val dst2 = Rect(dst)
        val p = paint?.let { Paint(it) }
        records += RecordEntry(RectF(tempRect)) {
            drawBitmap(bitmap, src2, dst2, p)
        }
    }

    override fun drawBitmap(bitmap: Bitmap, matrix: Matrix, paint: Paint?) {
        if (paint?.alpha == 0) return
        tempRect.set(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val staysRect = matrix.mapRect(tempRect)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        if (staysRect && !bitmap.hasAlpha() && paint?.alpha ?: 0xff == 0xff) {
            removeRedundantRecords(tempRect)
        }
        val m = Matrix(matrix)
        val p = paint?.let { Paint(it) }
        records += RecordEntry(RectF(tempRect)) {
            drawBitmap(bitmap, m, p)
        }
    }

    override fun drawBitmapMesh(
        bitmap: Bitmap, meshWidth: Int, meshHeight: Int,
        verts: FloatArray, vertOffset: Int, colors: IntArray?, colorOffset: Int,
        paint: Paint?
    ) {
        if (paint?.alpha == 0) return
        val verts2 = verts.clone()
        val colors2 = colors?.clone()
        val p = paint?.let { Paint(it) }
        records += RecordEntry {
            drawBitmapMesh(
                bitmap,
                meshWidth,
                meshHeight,
                verts2,
                vertOffset,
                colors2,
                colorOffset,
                p
            )
        }
    }

    override fun drawCircle(cx: Float, cy: Float, radius: Float, paint: Paint) {
        if (paint.alpha == 0) return
        tempRect.set(cx - radius, cy - radius, cx + radius, cy + radius)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawCircle(cx, cy, radius, p)
        }
    }

    override fun drawColor(@ColorInt color: Int) {
        val bounds = currentClipBounds
        if (Color.alpha(color) == 0xff) {
            removeRedundantRecords(bounds)
        }
        records += RecordEntry(bounds) {
            drawColor(color)
        }
    }

    override fun drawColor(@ColorInt color: Int, mode: PorterDuff.Mode) {
        val bounds = currentClipBounds
        if ((mode == PorterDuff.Mode.SRC_OVER || mode == PorterDuff.Mode.SRC) &&
            Color.alpha(color) == 0xff
        ) {
            removeRedundantRecords(bounds)
        }
        records += RecordEntry(bounds) {
            drawColor(color, mode)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun drawColor(@ColorInt color: Int, mode: BlendMode) {
        val bounds = currentClipBounds
        if ((mode == BlendMode.SRC_OVER || mode == BlendMode.SRC) &&
            Color.alpha(color) == 0xff
        ) {
            removeRedundantRecords(bounds)
        }
        records += RecordEntry(bounds) {
            drawColor(color, mode)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun drawColor(@ColorLong color: Long, mode: BlendMode) {
        val bounds = currentClipBounds
        if ((mode == BlendMode.SRC_OVER || mode == BlendMode.SRC) &&
            Color.alpha(color) == 1f
        ) {
            removeRedundantRecords(bounds)
        }
        records += RecordEntry(bounds) {
            drawColor(color, mode)
        }
    }

    override fun drawDoubleRoundRect(
        outer: RectF,
        outerRadii: FloatArray,
        inner: RectF,
        innerRadii: FloatArray,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        tempRect.set(outer)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val outer2 = RectF(outer)
        val outerRadii2 = outerRadii.clone()
        val inner2 = RectF(inner)
        val innerRadii2 = innerRadii.clone()
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawDoubleRoundRect(outer2, outerRadii2, inner2, innerRadii2, p)
        }
    }

    override fun drawDoubleRoundRect(
        outer: RectF,
        outerRx: Float,
        outerRy: Float,
        inner: RectF,
        innerRx: Float,
        innerRy: Float,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        tempRect.set(outer)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val outer2 = RectF(outer)
        val inner2 = RectF(inner)
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawDoubleRoundRect(outer2, outerRx, outerRy, inner2, innerRx, innerRy, p)
        }
    }

    override fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float, paint: Paint) {
        if (paint.alpha == 0) return
        tempRect.set(
            min(startX, stopX),
            min(startY, stopY),
            max(startX, stopX),
            max(startY, stopY)
        )
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawLine(startX, startY, stopX, stopY, p)
        }
    }

    override fun drawLines(pts: FloatArray, paint: Paint) {
        if (paint.alpha == 0) return
        val seq = pts.asSequence()
        val x = seq.filterIndexed { index, _ -> index % 2 == 0 }
        val y = seq.filterIndexed { index, _ -> index % 2 != 0 }
        tempRect.set(
            requireNotNull(x.minOrNull()),
            requireNotNull(y.minOrNull()),
            requireNotNull(x.maxOrNull()),
            requireNotNull(y.maxOrNull())
        )
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val pts2 = pts.clone()
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawLines(pts2, p)
        }
    }

    override fun drawLines(pts: FloatArray, offset: Int, count: Int, paint: Paint) {
        if (paint.alpha == 0) return
        val seq = pts.asSequence().drop(offset).take(count)
        val x = seq.filterIndexed { index, _ -> index % 2 == 0 }
        val y = seq.filterIndexed { index, _ -> index % 2 != 0 }
        tempRect.set(
            requireNotNull(x.minOrNull()),
            requireNotNull(y.minOrNull()),
            requireNotNull(x.maxOrNull()),
            requireNotNull(y.maxOrNull())
        )
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val pts2 = pts.clone()
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawLines(pts2, offset, count, p)
        }
    }

    override fun drawOval(oval: RectF, paint: Paint) {
        if (paint.alpha == 0) return
        tempRect.set(oval)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val oval2 = RectF(oval)
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawOval(oval2, p)
        }
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        if (paint.alpha == 0) return
        tempRect.set(left, top, right, bottom)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawOval(left, top, right, bottom, p)
        }
    }

    override fun drawPaint(paint: Paint) {
        val alpha = paint.alpha
        if (alpha == 0) return
        val bounds = currentClipBounds
        if (!currentLayer.getVisibleBounds(bounds)) return
        if (alpha == 0xff) {
            removeRedundantRecords(bounds)
        }
        val p = Paint(paint)
        records += RecordEntry(bounds) {
            drawPaint(p)
        }
    }

    override fun drawPath(path: Path, paint: Paint) {
        val alpha = paint.alpha
        if (alpha == 0) return
        path.computeBounds(tempRect, true)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        if (alpha == 0xff && path.isRect(null)) {
            removeRedundantRecords(tempRect)
        }
        val path2 = Path(path)
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawPath(path2, p)
        }
    }

    override fun drawPicture(picture: Picture) {
        tempRect.set(0f, 0f, picture.width.toFloat(), picture.height.toFloat())
        if (!currentLayer.getVisibleBounds(tempRect)) return
        records += RecordEntry(RectF(tempRect)) {
            drawPicture(picture)
        }
    }

    override fun drawPicture(picture: Picture, dst: Rect) {
        tempRect.set(dst)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val dst2 = Rect(dst)
        records += RecordEntry(RectF(tempRect)) {
            drawPicture(picture, dst2)
        }
    }

    override fun drawPicture(picture: Picture, dst: RectF) {
        tempRect.set(dst)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val dst2 = RectF(dst)
        records += RecordEntry(RectF(tempRect)) {
            drawPicture(picture, dst2)
        }
    }

    override fun drawColor(color: Long) {
        val bounds = currentClipBounds
        if (!currentLayer.getVisibleBounds(bounds)) return
        if (Color.alpha(color) == 1f) {
            removeRedundantRecords(bounds)
        }
        records += RecordEntry(bounds) {
            drawColor(color)
        }
    }

    override fun isHardwareAccelerated(): Boolean {
        return false
    }

    override fun setBitmap(bitmap: Bitmap?) {
        throw UnsupportedOperationException()
    }

    override fun enableZ() {
    }

    override fun disableZ() {
    }

    override fun isOpaque(): Boolean {
        return false
    }

    override fun getWidth(): Int {
        return canvasWidth
    }

    override fun getHeight(): Int {
        return canvasHeight
    }

    override fun getDensity(): Int {
        return density
    }

    override fun setDensity(density: Int) {
        this.density = density
    }

    override fun save(): Int {
        val layer = newLayer()
        records += RecordEntry {
            layer.actualLayerIndex = save()
        }
        return layer.index
    }

    override fun saveLayer(bounds: RectF?, paint: Paint?): Int {
        val layer = newLayer()
        val bounds2 = bounds?.let { RectF(it) }
        bounds2?.let { layer.clip(it, Region.Op.INTERSECT) }
        val p = paint?.let { Paint(it) }
        records += RecordEntry {
            layer.actualLayerIndex = saveLayer(bounds2, p)
        }
        return layer.index
    }

    override fun saveLayer(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        paint: Paint?
    ): Int {
        val layer = newLayer()
        tempRect.set(left, top, right, bottom)
        layer.clip(tempRect, Region.Op.INTERSECT)
        val p = paint?.let { Paint(it) }
        records += RecordEntry {
            layer.actualLayerIndex = saveLayer(left, top, right, bottom, p)
        }
        return layer.index
    }

    override fun saveLayerAlpha(bounds: RectF?, alpha: Int): Int {
        val layer = newLayer()
        val bounds2 = bounds?.let { RectF(it) }
        bounds2?.let { layer.clip(it, Region.Op.INTERSECT) }
        records += RecordEntry {
            layer.actualLayerIndex = saveLayerAlpha(bounds2, alpha)
        }
        return layer.index
    }

    override fun saveLayerAlpha(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        alpha: Int
    ): Int {
        val layer = newLayer()
        tempRect.set(left, top, right, bottom)
        layer.clip(tempRect, Region.Op.INTERSECT)
        records += RecordEntry {
            layer.actualLayerIndex = saveLayerAlpha(left, top, right, bottom, alpha)
        }
        return layer.index
    }

    override fun restore() {
        records += RecordEntry {
            popLayer()?.actualLayerIndex?.let {
                restoreToCount(it)
            }
        }
    }

    override fun getSaveCount(): Int {
        return layers.size - 1
    }

    override fun restoreToCount(saveCount: Int) {
        records += RecordEntry {
            popLayersTo(saveCount)?.actualLayerIndex?.let { restoreToCount(it) }
        }
    }

    override fun translate(dx: Float, dy: Float) {
        if (dx == 0f && dy == 0f) return
        currentLayer.updateMatrix { postTranslate(dx, dy) }
        records += RecordEntry {
            translate(dx, dy)
        }
    }

    override fun scale(sx: Float, sy: Float) {
        if (sx == 1f && sy == 1f) return
        currentLayer.updateMatrix { postScale(sx, sy) }
        records += RecordEntry {
            scale(sx, sy)
        }
    }

    override fun rotate(degrees: Float) {
        if (degrees % 360f == 0f) return
        currentLayer.updateMatrix { postRotate(degrees) }
        records += RecordEntry {
            rotate(degrees)
        }
    }

    override fun skew(sx: Float, sy: Float) {
        currentLayer.updateMatrix { postSkew(sx, sy) }
        records += RecordEntry {
            skew(sx, sy)
        }
    }

    override fun concat(matrix: Matrix?) {
        if (matrix?.isIdentity != false) return
        val m = Matrix(matrix)
        currentLayer.updateMatrix { postConcat(m) }
        records += RecordEntry {
            concat(m)
        }
    }

    override fun setMatrix(matrix: Matrix?) {
        val m = matrix?.let { Matrix(it) }
        currentLayer.updateMatrix { set(m) }
        records += RecordEntry {
            setMatrix(m)
        }
    }

    override fun clipRect(rect: RectF): Boolean {
        val rect2 = RectF(rect)
        val result = currentLayer.clip(rect2, Region.Op.INTERSECT)
        records += RecordEntry {
            clipRect(rect2)
        }
        return result
    }

    override fun clipRect(rect: Rect): Boolean {
        val rect2 = RectF(rect)
        val result = currentLayer.clip(rect2, Region.Op.INTERSECT)
        records += RecordEntry {
            clipRect(rect2)
        }
        return result
    }

    override fun clipRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        op: Region.Op
    ): Boolean {
        tempRect.set(left, top, right, bottom)
        val result = currentLayer.clip(tempRect, op)
        records += RecordEntry {
            clipRect(left, top, right, bottom, op)
        }
        return result
    }

    override fun clipRect(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        tempRect.set(left, top, right, bottom)
        val result = currentLayer.clip(tempRect, Region.Op.INTERSECT)
        records += RecordEntry {
            clipRect(left, top, right, bottom)
        }
        return result
    }

    override fun clipRect(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        tempRect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val result = currentLayer.clip(tempRect, Region.Op.INTERSECT)
        records += RecordEntry {
            clipRect(left, top, right, bottom)
        }
        return result
    }

    override fun clipOutRect(rect: RectF): Boolean {
        val rect2 = RectF(rect)
        val result = currentLayer.clip(rect2, Region.Op.DIFFERENCE)
        records += RecordEntry {
            clipRect(rect2)
        }
        return result
    }

    override fun clipOutRect(rect: Rect): Boolean {
        val rect2 = RectF(rect)
        val result = currentLayer.clip(rect2, Region.Op.DIFFERENCE)
        records += RecordEntry {
            clipRect(rect2)
        }
        return result
    }

    override fun clipOutRect(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        tempRect.set(left, top, right, bottom)
        val result = currentLayer.clip(tempRect, Region.Op.DIFFERENCE)
        records += RecordEntry {
            clipRect(left, top, right, bottom)
        }
        return result
    }

    override fun clipOutRect(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        tempRect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val result = currentLayer.clip(tempRect, Region.Op.DIFFERENCE)
        records += RecordEntry {
            clipRect(left, top, right, bottom)
        }
        return result
    }

    override fun clipPath(path: Path, op: Region.Op): Boolean {
        tempPath.set(path)
        val result = currentLayer.clip(tempPath, op)
        val path2 = Path(path)
        records += RecordEntry {
            clipPath(path2, op)
        }
        return result
    }

    override fun clipPath(path: Path): Boolean {
        tempPath.set(path)
        val result = currentLayer.clip(tempPath, Region.Op.INTERSECT)
        val path2 = Path(path)
        records += RecordEntry {
            clipPath(path2)
        }
        return result
    }

    override fun clipOutPath(path: Path): Boolean {
        tempPath.set(path)
        val result = currentLayer.clip(tempPath, Region.Op.DIFFERENCE)
        val path2 = Path(path)
        records += RecordEntry {
            clipPath(path2)
        }
        return result
    }

    override fun getDrawFilter(): DrawFilter? {
        return drawFilter
    }

    override fun setDrawFilter(filter: DrawFilter?) {
        drawFilter = filter
        records += RecordEntry {
            drawFilter = filter
        }
    }

    override fun quickReject(rect: RectF, type: EdgeType): Boolean {
        return quickReject(rect)
    }

    override fun quickReject(rect: RectF): Boolean {
        tempRect.set(rect)
        return !currentLayer.getVisibleBounds(tempRect)
    }

    override fun quickReject(path: Path, type: EdgeType): Boolean {
        return quickReject(path)
    }

    override fun quickReject(path: Path): Boolean {
        tempPath.set(path)
        return currentLayer.quickReject(tempPath)
    }

    override fun quickReject(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        type: EdgeType
    ): Boolean {
        return quickReject(left, top, right, bottom)
    }

    override fun quickReject(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        tempRect.set(left, top, right, bottom)
        return !currentLayer.getVisibleBounds(tempRect)
    }

    override fun getClipBounds(bounds: Rect?): Boolean {
        val clipRegion = currentLayer.clipRegion
        return if (bounds == null) {
            !clipRegion.isEmpty
        } else {
            if (clipRegion.getBounds(bounds)) {
                tempRect.set(bounds)
                currentLayer.invert(tempRect)
                bounds.set(tempRect)
                true
            } else {
                false
            }
        }
    }

    override fun drawPoint(x: Float, y: Float, paint: Paint) {
        if (paint.alpha == 0) return
        tempRect.set(x, y, x + 1, y + 1)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawPoint(x, y, p)
        }
    }

    override fun drawPoints(pts: FloatArray, offset: Int, count: Int, paint: Paint) {
        if (paint.alpha == 0 || count == 0) return
        val pts2 = pts.sliceArray(offset until offset + count)
        if (!currentLayer.containsAny(pts2)) return
        pts.copyInto(pts2, 0, offset, offset + count)
        val seq = pts2.asSequence()
        val x = seq.filterIndexed { index, _ -> index % 2 == 0 }
        val y = seq.filterIndexed { index, _ -> index % 2 != 0 }
        val bounds = RectF(
            checkNotNull(x.minOrNull()),
            checkNotNull(y.minOrNull()),
            checkNotNull(x.maxOrNull()),
            checkNotNull(y.maxOrNull())
        )
        val p = Paint(paint)
        records += RecordEntry(bounds) {
            drawPoints(pts2, p)
        }
    }

    override fun drawPoints(pts: FloatArray, paint: Paint) {
        drawPoints(pts, 0, pts.size, paint)
    }

    override fun drawPosText(
        text: CharArray,
        index: Int,
        count: Int,
        pos: FloatArray,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        records += RecordEntry {
            drawPosText(text, index, count, pos, paint)
        }
    }

    override fun drawPosText(text: String, pos: FloatArray, paint: Paint) {
        if (paint.alpha == 0) return
        records += RecordEntry {
            drawPosText(text, pos, paint)
        }
    }

    override fun drawRect(rect: RectF, paint: Paint) {
        if (paint.alpha == 0) return
        tempRect.set(rect)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        if (paint.alpha == 0xff) {
            removeRedundantRecords(tempRect)
        }
        val rect2 = RectF(rect)
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawRect(rect2, p)
        }
    }

    override fun drawRect(r: Rect, paint: Paint) {
        if (paint.alpha == 0) return
        tempRect.set(r)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        if (paint.alpha == 0xff) {
            removeRedundantRecords(tempRect)
        }
        val rect2 = Rect(r)
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawRect(rect2, p)
        }
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        if (paint.alpha == 0) return
        tempRect.set(left, top, right, bottom)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        if (paint.alpha == 0xff) {
            removeRedundantRecords(tempRect)
        }
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawRect(left, top, right, bottom, p)
        }
    }

    override fun drawRGB(r: Int, g: Int, b: Int) {
        val bounds = currentClipBounds
        removeRedundantRecords(bounds)
        records += RecordEntry(bounds) {
            drawRGB(r, g, b)
        }
    }

    override fun drawRoundRect(rect: RectF, rx: Float, ry: Float, paint: Paint) {
        if (paint.alpha == 0) return
        tempRect.set(rect)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val rect2 = RectF(rect)
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawRoundRect(rect2, rx, ry, p)
        }
    }

    override fun drawRoundRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        rx: Float,
        ry: Float,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        tempRect.set(left, top, right, bottom)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawRoundRect(left, top, right, bottom, rx, ry, p)
        }
    }

    override fun drawText(
        text: CharArray,
        index: Int,
        count: Int,
        x: Float,
        y: Float,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        val bounds = Rect()
        paint.getTextBounds(text, index, count, bounds)
        tempRect.set(bounds)
        tempRect.offset(x, y)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawText(text, index, count, x, y, p)
        }
    }

    override fun drawText(text: String, x: Float, y: Float, paint: Paint) {
        drawText(text, 0, text.length, x, y, paint)
    }

    override fun drawText(text: String, start: Int, end: Int, x: Float, y: Float, paint: Paint) {
        if (paint.alpha == 0) return
        val bounds = Rect()
        paint.getTextBounds(text, start, end, bounds)
        tempRect.set(bounds)
        tempRect.offset(x, y)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawText(text, start, end, x, y, p)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun drawText(
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        y: Float,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        val bounds = Rect()
        paint.getTextBounds(text, start, end, bounds)
        tempRect.set(bounds)
        tempRect.offset(x, y)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        val p = Paint(paint)
        records += RecordEntry(RectF(tempRect)) {
            drawText(text, start, end, x, y, p)
        }
    }

    override fun drawTextOnPath(
        text: CharArray,
        index: Int,
        count: Int,
        path: Path,
        hOffset: Float,
        vOffset: Float,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        val path2 = Path(path)
        val p = Paint(paint)
        records += RecordEntry {
            drawTextOnPath(text, index, count, path2, hOffset, vOffset, p)
        }
    }

    override fun drawTextOnPath(
        text: String,
        path: Path,
        hOffset: Float,
        vOffset: Float,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        val path2 = Path(path)
        val p = Paint(paint)
        records += RecordEntry {
            drawTextOnPath(text, path2, hOffset, vOffset, p)
        }
    }

    override fun drawTextRun(
        text: CharArray,
        index: Int,
        count: Int,
        contextIndex: Int,
        contextCount: Int,
        x: Float,
        y: Float,
        isRtl: Boolean,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        val p = Paint(paint)
        records += RecordEntry {
            drawTextRun(text, index, count, contextIndex, contextCount, x, y, isRtl, p)
        }
    }

    override fun drawTextRun(
        text: CharSequence,
        start: Int,
        end: Int,
        contextStart: Int,
        contextEnd: Int,
        x: Float,
        y: Float,
        isRtl: Boolean,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        val p = Paint(paint)
        records += RecordEntry {
            drawTextRun(text, start, end, contextStart, contextEnd, x, y, isRtl, p)
        }
    }

    override fun drawTextRun(
        text: MeasuredText,
        start: Int,
        end: Int,
        contextStart: Int,
        contextEnd: Int,
        x: Float,
        y: Float,
        isRtl: Boolean,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        val p = Paint(paint)
        records += RecordEntry {
            drawTextRun(text, start, end, contextStart, contextEnd, x, y, isRtl, p)
        }
    }

    override fun drawVertices(
        mode: VertexMode,
        vertexCount: Int,
        verts: FloatArray,
        vertOffset: Int,
        texs: FloatArray?,
        texOffset: Int,
        colors: IntArray?,
        colorOffset: Int,
        indices: ShortArray?,
        indexOffset: Int,
        indexCount: Int,
        paint: Paint
    ) {
        if (paint.alpha == 0) return
        val verts2 = verts.clone()
        val texs2 = texs?.clone()
        val colors2 = colors?.clone()
        val indices2 = indices?.clone()
        val p = Paint(paint)
        records += RecordEntry {
            drawVertices(
                mode,
                vertexCount,
                verts2,
                vertOffset,
                texs2,
                texOffset,
                colors2,
                colorOffset,
                indices2,
                indexOffset,
                indexCount,
                p
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun drawRenderNode(renderNode: RenderNode) {
        if (renderNode.alpha == 0f) return
        tempRect.set(0f, 0f, renderNode.width.toFloat(), renderNode.height.toFloat())
        if (!currentLayer.getVisibleBounds(tempRect)) return
        records += RecordEntry(RectF(tempRect)) {
            drawRenderNode(renderNode)
        }
    }

    override fun drawBitmap(
        colors: IntArray,
        offset: Int,
        stride: Int,
        x: Float,
        y: Float,
        width: Int,
        height: Int,
        hasAlpha: Boolean,
        paint: Paint?
    ) {
        if (paint?.alpha == 0) return
        tempRect.set(x, y, x + width, y + height)
        if (!currentLayer.getVisibleBounds(tempRect)) return
        if (!hasAlpha && paint?.alpha ?: 0xff == 0xff) {
            removeRedundantRecords(tempRect)
        }
        val p = paint?.let { Paint(it) }
        records += RecordEntry(RectF(tempRect)) {
            drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, p)
        }
    }

    override fun drawBitmap(
        colors: IntArray,
        offset: Int,
        stride: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        hasAlpha: Boolean,
        paint: Paint?
    ) {
        drawBitmap(colors, offset, stride, x.toFloat(), y.toFloat(), width, height, hasAlpha, paint)
    }

    override fun saveLayer(bounds: RectF?, paint: Paint?, saveFlags: Int): Int {
        return saveLayer(bounds, paint)
    }

    override fun saveLayer(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        paint: Paint?,
        saveFlags: Int
    ): Int {
        return saveLayer(left, top, right, bottom, paint)
    }

    override fun saveLayerAlpha(bounds: RectF?, alpha: Int, saveFlags: Int): Int {
        return saveLayerAlpha(bounds, alpha)
    }

    override fun saveLayerAlpha(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        alpha: Int,
        saveFlags: Int
    ): Int {
        return saveLayerAlpha(left, top, right, bottom, alpha)
    }

    override fun clipRect(rect: RectF, op: Region.Op): Boolean {
        val rect2 = RectF(rect)
        val result = currentLayer.clip(rect2, op)
        records += RecordEntry {
            clipRect(rect2, op)
        }
        return result
    }

    override fun clipRect(rect: Rect, op: Region.Op): Boolean {
        tempRect.set(rect)
        val result = currentLayer.clip(tempRect, op)
        val rect2 = Rect(rect)
        records += RecordEntry {
            clipRect(rect2, op)
        }
        return result
    }
}

private fun Rect.set(rect: RectF) {
    set(
        rect.left.roundToInt(),
        rect.top.roundToInt(),
        rect.right.roundToInt(),
        rect.bottom.roundToInt()
    )
}