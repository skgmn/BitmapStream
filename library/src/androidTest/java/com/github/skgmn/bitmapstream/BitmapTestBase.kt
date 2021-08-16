package com.github.skgmn.bitmapstream

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Before
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.FastFeatureDetector
import org.opencv.imgproc.Imgproc

abstract class BitmapTestBase {
    protected val appContext: Context by lazy {
        ApplicationProvider.getApplicationContext()
    }

    @Before
    fun setUp() {
        System.loadLibrary("opencv_java4")
    }

    protected fun <T : Any> assertNotNull(o: T?): T {
        Assert.assertNotNull(o)
        return o!!
    }

    protected fun decodeBitmapScaleTo(width: Int, height: Int, decoder: (BitmapFactory.Options?) -> Bitmap?): Bitmap {
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        decoder(opts)

        return decodeBitmapScaleBy(
            width.toFloat() / opts.outWidth,
            height.toFloat() / opts.outHeight,
            decoder
        )
    }

    protected fun decodeBitmapScaleBy(scaleX: Float, scaleY: Float, decoder: (BitmapFactory.Options?) -> Bitmap?): Bitmap {
        var sx = scaleX
        var sy = scaleY
        var sampleSize = 1
        while (sx <= 0.5f && sy <= 0.5f) {
            sx *= 2f
            sy *= 2f
            sampleSize *= 2
        }

        val opts = BitmapFactory.Options()
        opts.inSampleSize = sampleSize
        val bitmap = checkNotNull(decoder(opts))

        return Matrix().let { m ->
            m.setScale(sx, sy)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
        }
    }

    protected fun scaleBy(bitmap: Bitmap, scaleX: Float, scaleY: Float): Bitmap {
        return Matrix().let { m ->
            m.setScale(scaleX, scaleY)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
        }
    }

    // Source code from https://github.com/torcellite/imageComparator/blob/master/imageComparator/src/com/torcellite/imageComparator/MainActivity.java
    protected fun assertSimilar(expected: Bitmap, actual: Bitmap) {
        if (expected.width != actual.width || expected.height != actual.height) {
            throw AssertionError("Bitmaps should have same size")
        }

        val bmpimg1 = Bitmap.createScaledBitmap(expected, 100, 100, true)
        val bmpimg2 = Bitmap.createScaledBitmap(actual, 100, 100, true)
        var img1 = Mat()
        var img2 = Mat()
        Utils.bitmapToMat(bmpimg1, img1)
        Utils.bitmapToMat(bmpimg1, img2)

        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGBA2GRAY)
        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_RGBA2GRAY)
        img1.convertTo(img1, CvType.CV_32F)
        img2.convertTo(img2, CvType.CV_32F)
        val hist1 = Mat()
        val hist2 = Mat()
        val histSize = MatOfInt(180)
        val channels = MatOfInt(0)
        val bgrPlanes1 = ArrayList<Mat>()
        val bgrPlanes2 = ArrayList<Mat>()
        Core.split(img1, bgrPlanes1)
        Core.split(img2, bgrPlanes2)
        val histRanges = MatOfFloat(0f, 180f)
        val accumulate = false
        Imgproc.calcHist(bgrPlanes1, channels, Mat(), hist1, histSize, histRanges, accumulate)
        Core.normalize(hist1, hist1, 0.0, hist1.rows().toDouble(), Core.NORM_MINMAX, -1, Mat())
        Imgproc.calcHist(bgrPlanes2, channels, Mat(), hist2, histSize, histRanges, accumulate)
        Core.normalize(hist2, hist2, 0.0, hist2.rows().toDouble(), Core.NORM_MINMAX, -1, Mat())
        img1.convertTo(img1, CvType.CV_32F)
        img2.convertTo(img2, CvType.CV_32F)
        hist1.convertTo(hist1, CvType.CV_32F)
        hist2.convertTo(hist2, CvType.CV_32F)

        val compare = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CHISQR)
        if (compare == 0.0) {
            // exact same
            return
        } else if (compare <= 0 || compare >= 1500) {
            throw AssertionError("Bitmaps are not same")
        }

        img1 = Mat()
        img2 = Mat()
        Utils.bitmapToMat(bmpimg1, img1)
        Utils.bitmapToMat(bmpimg2, img2)
        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2RGB)
        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2RGB)
        val detector = FastFeatureDetector.create()
        val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

        val keypoints = MatOfKeyPoint()
        val dupKeypoints = MatOfKeyPoint()
        val descriptors = Mat()
        val dupDescriptors = Mat()
        val matches = MatOfDMatch()

        detector.detect(img1, keypoints)
        detector.detect(img2, dupKeypoints)

        detector.compute(img1, keypoints, descriptors)
        detector.compute(img2, dupKeypoints, dupDescriptors)

        matcher.match(descriptors, dupDescriptors, matches)

        val matchesList = matches.toList()
        val matchesFinal = matchesList.filter { it.distance <= MIN_DIST }

        if (matchesFinal.size <= MIN_MATCHES) {
            throw AssertionError("Bitmaps are not similar")
        }
    }

    companion object {
        private const val MIN_DIST = 10
        private const val MIN_MATCHES = 750
    }
}