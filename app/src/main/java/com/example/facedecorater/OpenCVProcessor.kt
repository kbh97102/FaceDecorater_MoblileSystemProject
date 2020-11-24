package com.example.facedecorater

import android.util.Log
import org.opencv.android.OpenCVLoader

class OpenCVProcessor {

    init {
        if (!OpenCVLoader.initDebug())
            Log.e("OpenCv", "Unable to load OpenCV")
        else
            Log.d("OpenCv", "OpenCV loaded")
    }
}