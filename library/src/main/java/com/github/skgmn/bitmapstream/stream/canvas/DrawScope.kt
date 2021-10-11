package com.github.skgmn.bitmapstream.stream.canvas

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.github.skgmn.bitmapstream.BitmapStream

interface DrawScope {
    val width: Int
    val height: Int

    fun draw(d: Drawable) {
        draw(d, 0, 0, width, height)
    }

    fun draw(d: Drawable, srcBounds: Rect?, destBounds: Rect) {
        if (srcBounds == null) {
            draw(d, destBounds.left, destBounds.top, destBounds.right, destBounds.bottom)
        } else {
            draw(
                d,
                srcBounds.left, srcBounds.top, srcBounds.right, srcBounds.bottom,
                destBounds.left, destBounds.top, destBounds.right, destBounds.bottom
            )
        }
    }

    fun draw(
        d: Drawable,
        destLeft: Int, destTop: Int, destRight: Int, destBottom: Int
    )

    fun draw(
        d: Drawable,
        srcLeft: Int, srcTop: Int, srcRight: Int, srcBottom: Int,
        destLeft: Int, destTop: Int, destRight: Int, destBottom: Int
    )

    fun draw(stream: BitmapStream, destBounds: Rect, paint: Paint?) {
        draw(stream, destBounds.left, destBounds.top, destBounds.right, destBounds.bottom, paint)
    }

    fun draw(stream: BitmapStream, left: Int, top: Int, paint: Paint?) {
        draw(stream, left, top, left + stream.metadata.width, top + stream.metadata.height, paint)
    }

    fun draw(
        stream: BitmapStream,
        destLeft: Int, destTop: Int, destRight: Int, destBottom: Int,
        paint: Paint?
    )
}