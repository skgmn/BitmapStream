package com.github.skgmn.bitmapstream

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import com.github.skgmn.bitmapstream.frame.FrameMethod
import com.github.skgmn.bitmapstream.metadata.BitmapMetadata
import com.github.skgmn.bitmapstream.source.*
import com.github.skgmn.bitmapstream.stream.canvas.CanvasBitmapStream
import com.github.skgmn.bitmapstream.stream.inmemory.InMemoryBitmapStream
import com.github.skgmn.bitmapstream.stream.lazy.BufferBitmapStream
import com.github.skgmn.bitmapstream.stream.source.BitmapFactoryBitmapStream
import com.github.skgmn.bitmapstream.stream.transform.HardwareTransformBitmapStream
import com.github.skgmn.bitmapstream.stream.transform.MutableTransformBitmapStream
import okio.source
import java.io.File
import java.io.FileNotFoundException
import java.net.URL

abstract class BitmapStream {
    abstract val metadata: BitmapMetadata

    internal open val features = object : StreamFeatures {
        override val regional get() = false
        override val hardware get() = false
        override val mutable: Boolean? get() = null
    }

    abstract fun scaleTo(width: Int, height: Int): BitmapStream
    abstract fun scaleWidth(width: Int): BitmapStream
    abstract fun scaleHeight(height: Int): BitmapStream
    abstract fun scaleBy(scaleWidth: Float, scaleHeight: Float): BitmapStream
    abstract fun region(left: Int, top: Int, right: Int, bottom: Int): BitmapStream

    abstract fun decode(): Bitmap?

    fun region(bounds: Rect): BitmapStream {
        return region(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    open fun mutable(mutable: Boolean?): BitmapStream {
        return if (mutable == null) {
            this
        } else {
            MutableTransformBitmapStream(this, mutable)
        }
    }

    fun frame(
        frameWidth: Int,
        frameHeight: Int,
        frameMethod: FrameMethod,
        background: Drawable?
    ): BitmapStream {
        val features = features
        return CanvasBitmapStream(
            canvasWidth = frameWidth,
            canvasHeight = frameHeight,
            key = FrameKey(this, frameMethod)
        ) {
            background?.let {
                draw(it, 0, 0, frameWidth, frameHeight)
            }
            val srcRect = Rect()
            val destRect = Rect()
            frameMethod.computeBounds(metadata, frameWidth, frameHeight, srcRect, destRect)
            draw(region(srcRect), destRect, Paint(Paint.FILTER_BITMAP_FLAG))
        }.hardware(features.hardware).mutable(features.mutable)
    }

    open fun hardware(hardware: Boolean): BitmapStream {
        return if (hardware) HardwareTransformBitmapStream(this) else this
    }

    open fun buffer(): BitmapStream {
        return BufferBitmapStream(this)
    }

    internal open fun downsampleOnly(): BitmapStream {
        return this
    }

    private class FrameKey(
        private val stream: BitmapStream,
        private val frameMethod: FrameMethod
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is FrameKey) return false

            if (stream != other.stream) return false
            if (frameMethod != other.frameMethod) return false

            return true
        }

        override fun hashCode(): Int {
            var result = stream.hashCode()
            result = 31 * result + frameMethod.hashCode()
            return result
        }
    }

    companion object {
        @JvmStatic
        fun fromAsset(assetManager: AssetManager, path: String): BitmapStream {
            return BitmapFactoryBitmapStream(AssetBitmapSource(assetManager, path))
        }

        @JvmStatic
        fun fromByteArray(array: ByteArray, key: Any? = null): BitmapStream {
            return fromByteArray(array, 0, array.size, key)
        }

        @JvmStatic
        fun fromByteArray(
            array: ByteArray,
            offset: Int,
            length: Int,
            key: Any? = null
        ): BitmapStream {
            return BitmapFactoryBitmapStream(ByteArrayBitmapSource(array, offset, length, key))
        }

        @JvmStatic
        fun fromFile(file: File): BitmapStream {
            return BitmapFactoryBitmapStream(FileBitmapSource(file))
        }

        @JvmStatic
        fun fromResource(res: Resources, @DrawableRes id: Int): BitmapStream {
            return BitmapFactoryBitmapStream(ResourceBitmapSource(res, id))
        }

        @JvmStatic
        fun fromInputStreamFactory(key: Any? = null, factory: InputStreamFactory): BitmapStream {
            return BitmapFactoryBitmapStream(SourceFactoryBitmapSource(key) {
                factory.createInputStream().source()
            })
        }

        @JvmStatic
        fun fromSourceFactory(key: Any? = null, factory: SourceFactory): BitmapStream {
            return BitmapFactoryBitmapStream(SourceFactoryBitmapSource(key, factory))
        }

        @JvmStatic
        fun fromBitmap(bitmap: Bitmap): BitmapStream {
            return InMemoryBitmapStream(bitmap)
        }

        @JvmStatic
        fun fromDrawable(d: Drawable): BitmapStream {
            return CanvasBitmapStream(d.intrinsicWidth, d.intrinsicHeight) {
                draw(d)
            }
        }

        @JvmStatic
        fun fromUri(context: Context, uriString: String): BitmapStream {
            return fromUri(context, Uri.parse(uriString))
        }

        @JvmStatic
        fun fromUri(context: Context, uri: Uri): BitmapStream {
            return when (uri.scheme?.lowercase()) {
                "http", "https" -> fromInputStreamFactory(uri) {
                    URL(uri.toString()).openStream()
                }
                "file" -> {
                    val pathSegments = requireNotNull(uri.pathSegments) {
                        "No path: $uri"
                    }
                    require(pathSegments.size > 0) {
                        "No path: $uri"
                    }
                    val path = requireNotNull(uri.path) {
                        "No path: $uri"
                    }
                    if (pathSegments[0].equals("android_asset", true)) {
                        val assetPath = pathSegments.drop(1).joinToString("/")
                        fromAsset(context.assets, assetPath)
                    } else {
                        fromFile(File(path))
                    }
                }
                "android.resource" -> {
                    val authority = requireNotNull(uri.authority) { "No authority: $uri" }
                    val r = try {
                        context.packageManager.getResourcesForApplication(authority)
                    } catch (ex: PackageManager.NameNotFoundException) {
                        throw FileNotFoundException("No package found for authority: $uri")
                    }
                    val path = requireNotNull(uri.pathSegments) { "No path: $uri" }
                    val id = when (path.size) {
                        1 -> try {
                            path[0].toInt()
                        } catch (e: NumberFormatException) {
                            throw IllegalArgumentException("Single path segment is not a resource ID: $uri")
                        }
                        2 -> r.getIdentifier(path[1], path[0], authority)
                        else -> throw IllegalArgumentException("More than two path segments: $uri")
                    }
                    if (id == 0) {
                        throw FileNotFoundException("No resource found for: $uri")
                    }
                    fromResource(r, id)
                }
                "content" -> {
                    val contentResolver = context.contentResolver
                    fromInputStreamFactory(uri) {
                        contentResolver.openInputStream(uri)
                            ?: throw FileNotFoundException("Can't open: $uri")
                    }
                }
                else -> throw IllegalArgumentException("Unsupported uri: $uri")
            }
        }
    }
}