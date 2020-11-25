package com.example.facedecorater.gallery

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.OpenCVProcessor
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sketch_layout.*
import kotlinx.android.synthetic.main.opencv_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class OpenCvActivity : AppCompatActivity() {

    private lateinit var processor: OpenCVProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.opencv_layout)

        processor = OpenCVProcessor()

        processor.checkOpenCV()

        setToolbar()

        val uri = intent.getParcelableExtra<Uri>("uri")
        opencv_imageView.setImageURI(uri)

        opencv_gausain.setOnClickListener {
            opencv_imageView.setImageBitmap(
                processor.gaussianFilter(
                    getBitmapFromImagView(
                        opencv_imageView
                    )
                )
            )
        }
        opencv_threshold.setOnClickListener {
            opencv_imageView.setImageBitmap(
                processor.threshold(
                    getBitmapFromImagView(
                        opencv_imageView
                    )
                )
            )
        }
        opencv_gray.setOnClickListener {
            opencv_imageView.setImageBitmap(processor.gray(getBitmapFromImagView(opencv_imageView)))
        }
        opencv_saveButton.setOnClickListener {
            saveImage()
        }
    }

    private fun setToolbar() {
        opencv_toolbar.apply {
            title = ""
            subtitle = ""
        }
        setSupportActionBar(opencv_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun saveImage() {
        val photoFile = File(getOutputDirectory(), "${System.currentTimeMillis()}.png")
        photoFile.createNewFile()
        val bos = ByteArrayOutputStream()
        val imageBitmap = getBitmapFromImagView(opencv_imageView)
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, bos)
        val fos = FileOutputStream(photoFile)
        fos.write(bos.toByteArray())
        fos.flush()
        fos.close()
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun getBitmapFromImagView(view: ImageView): Bitmap {
        val bitampDrawable = view.drawable as BitmapDrawable
        return bitampDrawable.bitmap
    }
}