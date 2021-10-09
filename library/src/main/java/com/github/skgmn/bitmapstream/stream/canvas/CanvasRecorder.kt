package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.*
import android.graphics.text.MeasuredText
import androidx.annotation.ColorInt
import androidx.annotation.ColorLong
import com.github.skgmn.bitmapstream.BitmapStream
import kotlin.math.*

@Suppress("DEPRECATION")
internal class CanvasRecorder internal constructor(
    private val canvasWidth: Int,
    private val canvasHeight: Int
) : Canvas() {
    private val records = mutableListOf<Canvas.(RectF) -> Unit>()
    private val layers = mutableListOf(VirtualLayer(0))
    private var drawFilter: DrawFilter? = null
    private var density: Int = Bitmap.DENSITY_NONE

    private val currentLayer: VirtualLayer get() = layers.last()

    private fun newLayer(): VirtualLayer {
        return currentLayer.run {
            copy(index = index + 1)
        }.also { layers += it }
    }

    private fun restoreLayerTo(index: Int): VirtualLayer? {
        val listIndex = layers.indexOfFirst { it.index >= index }
        return if (listIndex <= 0) {
            null
        } else {
            val layer = layers[listIndex]
            layers.subList(listIndex, layers.size).clear()
            layer
        }
    }

    private fun popLayer(): VirtualLayer? {
        return if (layers.size <= 1) {
            null
        } else {
            layers.removeLast()
        }
    }

    internal fun drawTo(canvas: Canvas, region: RectF) {
        with(canvas) {
            records.forEach { it(region) }
        }
    }

    internal fun drawStream(stream: BitmapStream, left: Float, top: Float, paint: Paint?) {
        records += drawer@{
            val bounds = RectF(it)
            val metadata = stream.metadata
            if (bounds.intersect(left, top, left + metadata.width, top + metadata.height)) {
                val bitmap = stream.region(
                    (bounds.left - left).roundToInt(),
                    (bounds.top - top).roundToInt(),
                    (bounds.right - left).roundToInt(),
                    (bounds.bottom - top).roundToInt()
                ).decode() ?: return@drawer
                drawBitmap(bitmap, null, bounds, paint)
            }
        }
    }

    internal fun drawStream(stream: BitmapStream, dst: RectF, paint: Paint?) {
        records += drawer@ {
            val bounds = RectF(it)
            if (bounds.intersect(dst)) {
                val scaleX = dst.width() / stream.metadata.width
                val scaleY = dst.height() / stream.metadata.height
                val left = (bounds.left - dst.left) / scaleX
                val top = (bounds.top - dst.top) / scaleY
                val right = left + bounds.width() / scaleX
                val bottom = top + bounds.height() / scaleY
                val bitmap = stream.region(
                    left.roundToInt(),
                    top.roundToInt(),
                    right.roundToInt(),
                    bottom.roundToInt()
                ).decode() ?: return@drawer
                drawBitmap(bitmap, null, bounds, paint)
            }
        }
    }

    override fun drawArc(
        left: Float, top: Float, right: Float, bottom: Float, startAngle: Float,
        sweepAngle: Float, useCenter: Boolean, paint: Paint
    ) {
        records += {
            if (it.intersects(left, top, right, bottom)) {
                drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint)
            }
        }
    }

    override fun drawArc(
        oval: RectF, startAngle: Float, sweepAngle: Float, useCenter: Boolean,
        paint: Paint
    ) {
        records += {
            if (it.intersects(oval)) {
                drawArc(oval, startAngle, sweepAngle, useCenter, paint)
            }
        }
    }

    override fun drawARGB(a: Int, r: Int, g: Int, b: Int) {
        records += {
            drawARGB(a, r, g, b)
        }
    }

    override fun drawBitmap(bitmap: Bitmap, left: Float, top: Float, paint: Paint?) {
        records += {
            if (it.intersects(left, top, left + bitmap.width, top + bitmap.height)) {
                drawBitmap(bitmap, left, top, paint)
            }
        }
    }

    override fun drawBitmap(
        bitmap: Bitmap, src: Rect?, dst: RectF,
        paint: Paint?
    ) {
        records += {
            if (it.intersects(dst)) {
                drawBitmap(bitmap, src, dst, paint)
            }
        }
    }

    override fun drawBitmap(
        bitmap: Bitmap, src: Rect?, dst: Rect,
        paint: Paint?
    ) {
        records += {
            if (it.intersects(dst)) {
                drawBitmap(bitmap, src, dst, paint)
            }
        }
    }

    override fun drawBitmap(bitmap: Bitmap, matrix: Matrix, paint: Paint?) {
        records += {
            val bounds = RectF()
            matrix.mapRect(bounds, bitmap.getBoundsAsFloat())
            if (it.intersect(bounds)) {
                drawBitmap(bitmap, matrix, paint)
            }
        }
    }

    override fun drawBitmapMesh(
        bitmap: Bitmap, meshWidth: Int, meshHeight: Int,
        verts: FloatArray, vertOffset: Int, colors: IntArray?, colorOffset: Int,
        paint: Paint?
    ) {
        records += {
            drawBitmapMesh(
                bitmap,
                meshWidth,
                meshHeight,
                verts,
                vertOffset,
                colors,
                colorOffset,
                paint
            )
        }
    }

    override fun drawCircle(cx: Float, cy: Float, radius: Float, paint: Paint) {
        records += {
            if (it.intersects(cx - radius, cy - radius, cx + radius, cy + radius)) {
                drawCircle(cx, cy, radius, paint)
            }
        }
    }

    override fun drawColor(@ColorInt color: Int) {
        records += {
            drawColor(color)
        }
    }

    override fun drawColor(@ColorInt color: Int, mode: PorterDuff.Mode) {
        records += {
            drawColor(color, mode)
        }
    }

    override fun drawColor(@ColorInt color: Int, mode: BlendMode) {
        records += {
            drawColor(color, mode)
        }
    }

    override fun drawColor(@ColorLong color: Long, mode: BlendMode) {
        records += {
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
        records += {
            if (it.intersects(outer)) {
                drawDoubleRoundRect(outer, outerRadii, inner, innerRadii, paint)
            }
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
        records += {
            if (it.intersects(outer)) {
                drawDoubleRoundRect(outer, outerRx, outerRy, inner, innerRx, innerRy, paint)
            }
        }
    }

    override fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float, paint: Paint) {
        records += {
            if (it.intersects(
                    min(startX, stopX),
                    min(startY, stopY),
                    max(startX, stopX),
                    max(startY, stopY)
                )
            ) {
                drawLine(startX, startY, stopX, stopY, paint)
            }
        }
    }

    override fun drawLines(pts: FloatArray, paint: Paint) {
        records += {
            val seq = pts.asSequence()
            val x = seq.filterIndexed { index, _ -> index % 2 == 0 }
            val y = seq.filterIndexed { index, _ -> index % 2 != 0 }
            if (it.intersects(
                    requireNotNull(x.minOrNull()),
                    requireNotNull(y.minOrNull()),
                    requireNotNull(x.maxOrNull()),
                    requireNotNull(y.maxOrNull()),
                )
            ) {
                drawLines(pts, paint)
            }
        }
    }

    override fun drawLines(pts: FloatArray, offset: Int, count: Int, paint: Paint) {
        records += {
            val seq = pts.asSequence().drop(offset).take(count)
            val x = seq.filterIndexed { index, _ -> index % 2 == 0 }
            val y = seq.filterIndexed { index, _ -> index % 2 != 0 }
            if (it.intersects(
                    requireNotNull(x.minOrNull()),
                    requireNotNull(y.minOrNull()),
                    requireNotNull(x.maxOrNull()),
                    requireNotNull(y.maxOrNull()),
                )
            ) {
                drawLines(pts, offset, count, paint)
            }
        }
    }

    override fun drawOval(oval: RectF, paint: Paint) {
        records += {
            if (it.intersects(oval)) {
                drawOval(oval, paint)
            }
        }
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        records += {
            if (it.intersects(left, top, right, bottom)) {
                drawOval(left, top, right, bottom, paint)
            }
        }
    }

    override fun drawPaint(paint: Paint) {
        records += {
            drawPaint(paint)
        }
    }

    override fun drawPath(path: Path, paint: Paint) {
        records += {
            drawPath(path, paint)
        }
    }

    override fun drawPicture(picture: Picture) {
        records += {
            if (it.intersects(0f, 0f, picture.width.toFloat(), picture.height.toFloat())) {
                drawPicture(picture)
            }
        }
    }

    override fun drawPicture(picture: Picture, dst: Rect) {
        records += {
            if (it.intersects(dst)) {
                drawPicture(picture, dst)
            }
        }
    }

    override fun drawPicture(picture: Picture, dst: RectF) {
        records += {
            if (it.intersects(dst)) {
                drawPicture(picture, dst)
            }
        }
    }

    override fun drawColor(color: Long) {
        records += {
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
        return true
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
        records += {
            layer.actualLayerIndex = save()
        }
        return layer.index
    }

    override fun saveLayer(bounds: RectF?, paint: Paint?): Int {
        val layer = newLayer()
        bounds?.let { layer.clip(it, Region.Op.INTERSECT) }
        records += {
            layer.actualLayerIndex = saveLayer(bounds, paint)
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
        layer.clip(left, top, right, bottom, Region.Op.INTERSECT)
        records += {
            layer.actualLayerIndex = saveLayer(left, top, right, bottom, paint)
        }
        return layer.index
    }

    override fun saveLayerAlpha(bounds: RectF?, alpha: Int): Int {
        val layer = newLayer()
        bounds?.let { layer.clip(it, Region.Op.INTERSECT) }
        records += {
            layer.actualLayerIndex = saveLayerAlpha(bounds, alpha)
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
        layer.clip(left, top, right, bottom, Region.Op.INTERSECT)
        records += {
            layer.actualLayerIndex = saveLayerAlpha(left, top, right, bottom, alpha)
        }
        return layer.index
    }

    override fun restore() {
        records += {
            popLayer()
            restore()
        }
    }

    override fun getSaveCount(): Int {
        return layers.size - 1
    }

    override fun restoreToCount(saveCount: Int) {
        records += {
            restoreLayerTo(saveCount)?.actualLayerIndex?.let { restoreToCount(it) }
        }
    }

    override fun translate(dx: Float, dy: Float) {
        records += {
            translate(dx, dy)
        }
    }

    override fun scale(sx: Float, sy: Float) {
        records += {
            scale(sx, sy)
        }
    }

    override fun rotate(degrees: Float) {
        records += {
            rotate(degrees)
        }
    }

    override fun skew(sx: Float, sy: Float) {
        records += {
            skew(sx, sy)
        }
    }

    override fun concat(matrix: Matrix?) {
        records += {
            concat(matrix)
        }
    }

    override fun setMatrix(matrix: Matrix?) {
        records += {
            setMatrix(matrix)
        }
    }

    override fun clipRect(rect: RectF): Boolean {
        val layer = currentLayer
        val result = layer.clip(rect, Region.Op.INTERSECT)
        records += {
            clipRect(rect)
        }
        return result
    }

    override fun clipRect(rect: Rect): Boolean {
        val layer = currentLayer
        val result = layer.clip(rect, Region.Op.INTERSECT)
        records += {
            clipRect(rect)
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
        val layer = currentLayer
        val result = layer.clip(left, top, right, bottom, op)
        records += {
            clipRect(left, top, right, bottom, op)
        }
        return result
    }

    override fun clipRect(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        val layer = currentLayer
        val result = layer.clip(left, top, right, bottom, Region.Op.INTERSECT)
        records += {
            clipRect(left, top, right, bottom)
        }
        return result
    }

    override fun clipRect(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        val layer = currentLayer
        val result = layer.clip(left, top, right, bottom, Region.Op.INTERSECT)
        records += {
            clipRect(left, top, right, bottom)
        }
        return result
    }

    override fun clipOutRect(rect: RectF): Boolean {
        val layer = currentLayer
        val result = layer.clip(rect, Region.Op.DIFFERENCE)
        records += {
            clipRect(rect)
        }
        return result
    }

    override fun clipOutRect(rect: Rect): Boolean {
        val layer = currentLayer
        val result = layer.clip(rect, Region.Op.DIFFERENCE)
        records += {
            clipRect(rect)
        }
        return result
    }

    override fun clipOutRect(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        val layer = currentLayer
        val result = layer.clip(left, top, right, bottom, Region.Op.DIFFERENCE)
        records += {
            clipRect(left, top, right, bottom)
        }
        return result
    }

    override fun clipOutRect(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        val layer = currentLayer
        val result = layer.clip(left, top, right, bottom, Region.Op.DIFFERENCE)
        records += {
            clipRect(left, top, right, bottom)
        }
        return result
    }

    override fun clipPath(path: Path, op: Region.Op): Boolean {
        val layer = currentLayer
        val result = layer.clip(path, Region.Op.INTERSECT)
        records += {
            clipPath(path, op)
        }
        return result
    }

    override fun clipPath(path: Path): Boolean {
        val layer = currentLayer
        val result = layer.clip(path, Region.Op.INTERSECT)
        records += {
            clipPath(path)
        }
        return result
    }

    override fun clipOutPath(path: Path): Boolean {
        val layer = currentLayer
        val result = layer.clip(path, Region.Op.DIFFERENCE)
        records += {
            clipPath(path)
        }
        return result
    }

    override fun getDrawFilter(): DrawFilter? {
        return drawFilter
    }

    override fun setDrawFilter(filter: DrawFilter?) {
        drawFilter = filter
        records += {
            drawFilter = filter
        }
    }

    override fun quickReject(rect: RectF, type: EdgeType): Boolean {
        return currentLayer.clipRegion.quickReject(rect.roundOutToRect())
    }

    override fun quickReject(rect: RectF): Boolean {
        return currentLayer.clipRegion.quickReject(rect.roundOutToRect())
    }

    override fun quickReject(path: Path, type: EdgeType): Boolean {
        val clipRegion = currentLayer.clipRegion
        return clipRegion.quickReject(path.toRegion(clipRegion))
    }

    override fun quickReject(path: Path): Boolean {
        val clipRegion = currentLayer.clipRegion
        return clipRegion.quickReject(path.toRegion(clipRegion))
    }

    override fun quickReject(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        type: EdgeType
    ): Boolean {
        val clipRegion = currentLayer.clipRegion
        return clipRegion.quickReject(
            floor(left).toInt(),
            floor(top).toInt(),
            ceil(right).toInt(),
            ceil(bottom).toInt()
        )
    }

    override fun quickReject(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        val clipRegion = currentLayer.clipRegion
        return clipRegion.quickReject(
            floor(left).toInt(),
            floor(top).toInt(),
            ceil(right).toInt(),
            ceil(bottom).toInt()
        )
    }

    override fun getClipBounds(bounds: Rect?): Boolean {
        val clipRegion = currentLayer.clipRegion
        return if (bounds == null) {
            !clipRegion.isEmpty
        } else {
            clipRegion.getBounds(bounds)
        }
    }

    override fun drawPoint(x: Float, y: Float, paint: Paint) {
        records += {
            if (it.contains(x, y)) {
                drawPoint(x, y, paint)
            }
        }
    }

    override fun drawPoints(pts: FloatArray, offset: Int, count: Int, paint: Paint) {
        records += {
            val seq = pts.asSequence().drop(offset).take(count)
            val x = seq.filterIndexed { index, _ -> index % 2 == 0 }
            val y = seq.filterIndexed { index, _ -> index % 2 != 0 }
            if (it.intersects(
                    requireNotNull(x.minOrNull()),
                    requireNotNull(y.minOrNull()),
                    requireNotNull(x.maxOrNull()),
                    requireNotNull(y.maxOrNull()),
                )
            ) {
                drawPoints(pts, offset, count, paint)
            }
        }
    }

    override fun drawPoints(pts: FloatArray, paint: Paint) {
        records += {
            val seq = pts.asSequence()
            val x = seq.filterIndexed { index, _ -> index % 2 == 0 }
            val y = seq.filterIndexed { index, _ -> index % 2 != 0 }
            if (it.intersects(
                    requireNotNull(x.minOrNull()),
                    requireNotNull(y.minOrNull()),
                    requireNotNull(x.maxOrNull()),
                    requireNotNull(y.maxOrNull()),
                )
            ) {
                drawPoints(pts, paint)
            }
        }
    }

    override fun drawPosText(
        text: CharArray,
        index: Int,
        count: Int,
        pos: FloatArray,
        paint: Paint
    ) {
        records += {
            drawPosText(text, index, count, pos, paint)
        }
    }

    override fun drawPosText(text: String, pos: FloatArray, paint: Paint) {
        records += {
            drawPosText(text, pos, paint)
        }
    }

    override fun drawRect(rect: RectF, paint: Paint) {
        records += {
            if (it.intersects(rect)) {
                drawRect(rect, paint)
            }
        }
    }

    override fun drawRect(r: Rect, paint: Paint) {
        records += {
            if (it.intersects(r)) {
                drawRect(r, paint)
            }
        }
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        records += {
            if (it.intersects(left, top, right, bottom)) {
                drawRect(left, top, right, bottom, paint)
            }
        }
    }

    override fun drawRGB(r: Int, g: Int, b: Int) {
        records += {
            drawRGB(r, g, b)
        }
    }

    override fun drawRoundRect(rect: RectF, rx: Float, ry: Float, paint: Paint) {
        records += {
            if (it.intersects(rect)) {
                drawRoundRect(rect, rx, ry, paint)
            }
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
        records += {
            if (it.intersects(left, top, right, bottom)) {
                drawRoundRect(left, top, right, bottom, rx, ry, paint)
            }
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
        records += {
            drawText(text, index, count, x, y, paint)
        }
    }

    override fun drawText(text: String, x: Float, y: Float, paint: Paint) {
        records += {
            drawText(text, x, y, paint)
        }
    }

    override fun drawText(text: String, start: Int, end: Int, x: Float, y: Float, paint: Paint) {
        records += {
            drawText(text, start, end, x, y, paint)
        }
    }

    override fun drawText(
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        y: Float,
        paint: Paint
    ) {
        records += {
            drawText(text, start, end, x, y, paint)
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
        records += {
            drawTextOnPath(text, index, count, path, hOffset, vOffset, paint)
        }
    }

    override fun drawTextOnPath(
        text: String,
        path: Path,
        hOffset: Float,
        vOffset: Float,
        paint: Paint
    ) {
        records += {
            drawTextOnPath(text, path, hOffset, vOffset, paint)
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
        records += {
            drawTextRun(text, index, count, contextIndex, contextCount, x, y, isRtl, paint)
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
        records += {
            drawTextRun(text, start, end, contextStart, contextEnd, x, y, isRtl, paint)
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
        records += {
            drawTextRun(text, start, end, contextStart, contextEnd, x, y, isRtl, paint)
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
        records += {
            drawVertices(
                mode,
                vertexCount,
                verts,
                vertOffset,
                texs,
                texOffset,
                colors,
                colorOffset,
                indices,
                indexOffset,
                indexCount,
                paint
            )
        }
    }

    override fun drawRenderNode(renderNode: RenderNode) {
        records += {
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
        records += {
            if (it.intersects(x, y, x + width, y + height)) {
                drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint)
            }
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
        records += {
            if (it.intersects(x, y, x + width, y + height)) {
                drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint)
            }
        }
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
        val layer = currentLayer
        val result = layer.clip(rect, op)
        records += {
            clipRect(rect, op)
        }
        return result
    }

    override fun clipRect(rect: Rect, op: Region.Op): Boolean {
        val layer = currentLayer
        val result = layer.clip(rect, op)
        records += {
            clipRect(rect, op)
        }
        return result
    }

    private inner class VirtualLayer(
        val index: Int,
        val clipRegion: Region = Region(0, 0, canvasWidth, canvasHeight),
        var actualLayerIndex: Int? = null
    ) {
        fun copy(
            index: Int = this.index,
            clipRegion: Region = Region(this.clipRegion),
            actualLayerIndex: Int? = this.actualLayerIndex
        ) = VirtualLayer(index, clipRegion, actualLayerIndex)

        fun clip(bounds: Rect, op: Region.Op): Boolean {
            return clipRegion.op(bounds, op)
        }

        fun clip(bounds: RectF, op: Region.Op): Boolean {
            return clipRegion.op(bounds.roundOutToRect(), op)
        }

        fun clip(left: Int, top: Int, right: Int, bottom: Int, op: Region.Op): Boolean {
            return clipRegion.op(left, top, right, bottom, op)
        }

        fun clip(left: Float, top: Float, right: Float, bottom: Float, op: Region.Op): Boolean {
            return clipRegion.op(
                left.roundToInt(),
                top.roundToInt(),
                right.roundToInt(),
                bottom.roundToInt(),
                op
            )
        }

        fun clip(path: Path, op: Region.Op): Boolean {
            return clipRegion.op(path.toRegion(clipRegion), op)
        }
    }
}

private fun RectF.roundOutToRect(): Rect {
    return Rect().also { roundOut(it) }
}

private fun Path.toRegion(clipRegion: Region): Region {
    return Region().also {
        it.setPath(this, clipRegion)
    }
}

private fun Bitmap.getBoundsAsFloat(left: Float = 0f, top: Float = 0f): RectF {
    return RectF(left, top, left + width.toFloat(), top + height.toFloat())
}

private fun RectF.intersects(rect: RectF): Boolean {
    return intersects(rect.left, rect.top, rect.right, rect.bottom)
}

private fun RectF.intersects(rect: Rect): Boolean {
    return intersects(
        rect.left.toFloat(),
        rect.top.toFloat(),
        rect.right.toFloat(),
        rect.bottom.toFloat()
    )
}

private fun RectF.intersects(left: Int, top: Int, right: Int, bottom: Int): Boolean {
    return intersects(
        left.toFloat(),
        top.toFloat(),
        right.toFloat(),
        bottom.toFloat()
    )
}