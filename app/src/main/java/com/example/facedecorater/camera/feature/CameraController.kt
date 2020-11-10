package com.example.facedecorater.camera.feature

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CameraController(
    private val previewView: PreviewView,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context
) {

    private var imageCapture: ImageCapture? = null

    private fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = Executors.newSingleThreadExecutor()
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("Camera Error", "Use case binding failed")
            }
        }, executor)
    }

    private fun takePicture(outputDirectory: File) {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss-SSS",
                Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        val executor = Executors.newSingleThreadExecutor()
        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("ImageSaved", "Success")
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("ImageSaved", "Error")
                }
            })
    }

}