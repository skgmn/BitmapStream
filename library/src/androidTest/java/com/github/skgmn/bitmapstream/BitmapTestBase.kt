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
import org.opencv.features2d.*
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

    protected fun assertSimilar(expected: Bitmap, actual: Bitmap) {
        if (expected.width != actual.width || expected.height != actual.height) {
            throw AssertionError("Bitmaps should have same size")
        }

        // https://github.com/torcellite/imageComparator/blob/master/imageComparator/src/com/torcellite/imageComparator/MainActivity.java

        val bmpimg1 = Bitmap.createScaledBitmap(expected, 100, 100, true)
        val bmpimg2 = Bitmap.createScaledBitmap(actual, 100, 100, true)
        var img1 = Mat()
        var img2 = Mat()
        Utils.bitmapToMat(bmpimg1, img1)
        Utils.bitmapToMat(bmpimg2, img2)

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
        } else if (compare < 0 || compare >= 1500) {
            throw AssertionError("Bitmaps are not same")
        }

        img1 = Mat()
        img2 = Mat()
        Utils.bitmapToMat(expected, img1)
        Utils.bitmapToMat(actual, img2)
        val mssim = getMssim(img1, img2)
        if (mssim < 0.99) {
            throw AssertionError("Bitmaps are not similar")
        }
    }

    // https://docs.opencv.org/2.4/doc/tutorials/gpu/gpu-basics-similarity/gpu-basics-similarity.html
    private fun getMssim(i1: Mat, i2: Mat): Double {
        val I1 = Mat()
        val I2 = Mat()
        i1.convertTo(I1, CvType.CV_32F)
        i2.convertTo(I2, CvType.CV_32F)

        val I2_2 = I2.mul(I2)
        val I1_2 = I1.mul(I1)
        val I1_I2 = I1.mul(I2)

        val mu1 = Mat()
        val mu2 = Mat()
        Imgproc.GaussianBlur(I1, mu1, Size(11.0, 11.0), 1.5)
        Imgproc.GaussianBlur(I2, mu2, Size(11.0, 11.0), 1.5)

        val mu1_2 = mu1.mul(mu1)
        val mu2_2 = mu2.mul(mu2)
        val mu1_mu2 = mu1.mul(mu2)

        var sigma1_2 = Mat()
        var sigma2_2 = Mat()
        var sigma12 = Mat()

        Imgproc.GaussianBlur(I1_2, sigma1_2, Size(11.0, 11.0), 1.5)
        sigma1_2 -= mu1_2

        Imgproc.GaussianBlur(I2_2, sigma2_2, Size(11.0, 11.0), 1.5)
        sigma2_2 -= mu2_2

        Imgproc.GaussianBlur(I1_I2, sigma12, Size(11.0, 11.0), 1.5)
        sigma12 -= mu1_mu2

        var t1 = 2.0 * mu1_mu2 + C1
        var t2 = 2.0 * sigma12 + C2
        val t3 = t1.mul(t2)

        t1 = mu1_2 + mu2_2 + C1
        t2 = sigma1_2 + sigma2_2 + C2
        t1 = t1.mul(t2)

        val ssimMap = Mat()
        Core.divide(t3, t1, ssimMap)

        return Core.mean(ssimMap).`val`[0]
    }

    private operator fun Mat.minus(other: Mat): Mat {
        val temp = Mat()
        Core.subtract(this, other, temp)
        return temp
    }

    private operator fun Double.times(other: Mat): Mat {
        val temp = Mat()
        Core.multiply(other, Scalar(this), temp)
        return temp
    }

    private operator fun Mat.plus(other: Double): Mat {
        val temp = Mat()
        Core.add(this, Scalar(other), temp)
        return temp
    }

    private operator fun Mat.plus(other: Mat): Mat {
        val temp = Mat()
        Core.add(this, other, temp)
        return temp
    }

    companion object {
        private const val C1 = 6.5025
        private const val C2 = 58.5225
    }
}