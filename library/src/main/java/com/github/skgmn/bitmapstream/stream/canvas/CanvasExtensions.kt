package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.github.skgmn.bitmapstream.BitmapStream

fun Canvas.drawStream(stream: BitmapStream, left: Float, top: Float, paint: Paint?) {
    if (this is CanvasOptimizer) {
        drawStream(stream, left, top, paint)
    } else {
        drawBitmap(stream.decode() ?: return, left, top, paint)
    }
}

fun Canvas.drawStream(stream: BitmapStream, dst: RectF, paint: Paint?) {
    if (this is CanvasOptimizer) {
        drawStream(stream, dst, paint)
    } else {
        drawBitmap(stream.decode() ?: return, null, dst, paint)
    }
}

fun Canvas.drawStream(stream: BitmapStream, dst: Rect, paint: Paint?) {
    if (this is CanvasOptimizer) {
        drawStream(stream, RectF(dst), paint)
    } else {
        drawBitmap(stream.decode() ?: return, null, dst, paint)
    }
}