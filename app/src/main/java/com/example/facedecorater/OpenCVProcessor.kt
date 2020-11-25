package com.example.facedecorater

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class OpenCVProcessor {

    fun checkOpenCV(){
        if (!OpenCVLoader.initDebug())
            Log.e("OpenCv", "Unable to load OpenCV")
        else
            Log.d("OpenCv", "OpenCV loaded")
    }

    fun gaussianFilter(imageBitmap : Bitmap):Bitmap{
        val bitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val input = Mat()
        var output = Mat()
        Utils.bitmapToMat(bitmap, input)

        Imgproc.GaussianBlur(input, output, Size(33.0,33.0), 11.0)
        Utils.matToBitmap(output, outputBitmap)
        return outputBitmap
    }

    fun threshold(imageBitmap: Bitmap):Bitmap{
        val srcBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val outputBitmap = Bitmap.createBitmap(srcBitmap.width, srcBitmap.height, Bitmap.Config.ARGB_8888)
        val input = Mat()
        val gray = Mat()
        val output = Mat()
        Utils.bitmapToMat(srcBitmap, input)
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_RGBA2GRAY)

        Imgproc.adaptiveThreshold(gray, output, 180.0, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 3, 2.0)
        Utils.matToBitmap(output, outputBitmap)
        return outputBitmap
    }

    fun gray(imageBitmap: Bitmap):Bitmap{
        val srcBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val outputBitmap = Bitmap.createBitmap(srcBitmap.width, srcBitmap.height, Bitmap.Config.ARGB_8888)
        val input = Mat()
        val output = Mat()
        Utils.bitmapToMat(srcBitmap, input)
        Imgproc.cvtColor(input, output, Imgproc.COLOR_RGBA2GRAY)

        Utils.matToBitmap(output, outputBitmap)
        return outputBitmap
    }
//
//    fun applyFilter(imageBitmap: Bitmap):Bitmap{
//        val srcBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)
//        val outputBitmap = Bitmap.createBitmap(srcBitmap.width, srcBitmap.height, Bitmap.Config.ARGB_8888)
//        val input = Mat()
//        val output = Mat()
//        val hsv = Mat()
//
//
//
//        Utils.bitmapToMat(srcBitmap, input)
//
//        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGRA2BGR)
//
//        Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV)
//
//        val hsvChannel = ArrayList<Mat>()
//
//        Core.split(hsv, hsvChannel)
//
//        hsvChannel[2].setTo(Scalar(145.0))
//
//
//        Core.merge(hsvChannel, output)
//
//        Utils.matToBitmap(output, outputBitmap)
//
//        return outputBitmap
//    }
}