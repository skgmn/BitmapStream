package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.*
import kotlin.math.roundToInt

internal class SimulatedLayer(
    val index: Int,
    val clipRegion: Region,
    private val matrix: Matrix = Matrix(),
    var actualLayerIndex: Int? = null
) {
    var inversedMatrixValue: Matrix? = null
    private val inversedMatrix
        get() = inversedMatrixValue ?: Matrix().also {
            matrix.invert(it)
            inversedMatrixValue = it
        }

    fun copy(
        index: Int = this.index,
        clipRegion: Region = Region(this.clipRegion),
        matrix: Matrix = Matrix(this.matrix),
        actualLayerIndex: Int? = this.actualLayerIndex
    ) = SimulatedLayer(index, clipRegion, matrix, actualLayerIndex)

    fun getVisibleBounds(bounds: RectF): Boolean {
        matrix.mapRect(bounds)
        val clipBounds = clipRegion.bounds
        return bounds.intersect(
            clipBounds.left.toFloat(),
            clipBounds.top.toFloat(),
            clipBounds.right.toFloat(),
            clipBounds.bottom.toFloat()
        )
    }

    fun containsAny(pts: FloatArray): Boolean {
        matrix.mapPoints(pts)
        return pts.indices.step(2).any {
            clipRegion.contains(pts[it].roundToInt(), pts[it + 1].roundToInt())
        }
    }

    fun invert(bounds: RectF) {
        inversedMatrix.mapRect(bounds)
    }

    fun clip(bounds: RectF, op: Region.Op): Boolean {
        matrix.mapRect(bounds)
        return clipRegion.op(bounds.roundOutToRect(), op)
    }

    fun clip(path: Path, op: Region.Op): Boolean {
        path.transform(matrix)
        return clipRegion.op(path.toRegion(clipRegion), op)
    }

    fun quickReject(path: Path): Boolean {
        path.transform(matrix)
        return clipRegion.quickReject(path.toRegion(clipRegion))
    }

    inline fun updateMatrix(block: Matrix.() -> Unit) {
        matrix.block()
        inversedMatrixValue = null
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