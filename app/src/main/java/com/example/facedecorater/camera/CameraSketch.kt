package com.example.facedecorater.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.facedecorater.R
import com.example.facedecorater.camera.feature.CameraController
import com.example.facedecorater.gallery.SketchView
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.camera_sketch_layout.*
import kotlinx.android.synthetic.main.gallery_sketch_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CameraSketch : AppCompatActivity() {

    private lateinit var sketchView: SketchView
    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private var isFabOpen = false
    private var cameraController:CameraController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_sketch_layout)

        cameraController = CameraController(previewView = camera_sketch_previewView, lifecycleOwner = this as LifecycleOwner, context = this)

        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close)

        addSketchView()

        camera_brush_button.setOnClickListener {
            animFab()
        }
        camera_sketch_color_button.setOnClickListener {
            animFab()
            val colorPickerDialog = ColorPickerDialog.newBuilder().apply {
                setDialogType(ColorPickerDialog.TYPE_PRESETS)
            }.create()

            colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
                override fun onColorSelected(dialogId: Int, color: Int) {
                    sketchView.setBrushColor(color)
                }

                override fun onDialogDismissed(dialogId: Int) {
                }
            })
            colorPickerDialog.show(supportFragmentManager, "Color Picker")
        }
        camera_sketch_size_button.setOnClickListener {
            val alertBuilder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.size_layout, null)
            val seekBar = view.findViewById<SeekBar>(R.id.size_seekBar)
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    sketchView.setBrushSize(p0!!.progress.toFloat())
                }
            })
            alertBuilder.apply {
                setView(view)
                create()
            }.run { show() }
        }

        cameraController?.startCamera()
    }

    private fun addSketchView() {
        sketchView = SketchView(this).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.TRANSPARENT)
        }
        camera_sketch_layout.addView(sketchView)
        val constraintSet = ConstraintSet()
        constraintSet.apply {
            clone(camera_sketch_layout)
            connect(
                sketchView.id,
                ConstraintSet.START,
                camera_sketch_layout.id,
                ConstraintSet.START
            )
            connect(sketchView.id, ConstraintSet.END, camera_sketch_layout.id, ConstraintSet.END)
            connect(
                sketchView.id,
                ConstraintSet.TOP,
                camera_sketch_layout.id,
                ConstraintSet.BOTTOM
            )
            connect(
                sketchView.id,
                ConstraintSet.BOTTOM,
                camera_sketch_layout.id,
                ConstraintSet.BOTTOM
            )
        }.run { applyTo(camera_sketch_layout) }
    }

    private fun animFab() {
        if (isFabOpen) {
            camera_sketch_color_button.apply {
                startAnimation(fab_close)
                isClickable = false
            }
            camera_sketch_size_button.apply {
                startAnimation(fab_close)
                isClickable = false
            }
            isFabOpen = false
        } else {
            camera_sketch_color_button.apply {
                startAnimation(fab_open)
                isClickable = true
            }
            camera_sketch_size_button.apply {
                startAnimation(fab_open)
                isClickable = true
            }
            isFabOpen = true
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveImage() {
        var displayMetrics = DisplayMetrics()
        //api 30사용하기엔 너무 높은 느낌 30에서 deprecated 됨
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val imageBitmap = camera_sketch_previewView.bitmap

        val canvasBitmap = Bitmap.createBitmap(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            Bitmap.Config.ARGB_8888
        )

        var canvas = Canvas(canvasBitmap).apply {
            if (imageBitmap != null) {
                drawBitmap(imageBitmap, gallery_sketch_imageView.x, gallery_sketch_imageView.y, null)
            }
            drawBitmap(sketchView.getBitmap(), sketchView.x, sketchView.y, null)
        }

        val photoFile = File(getOutputDirectory(), "${System.currentTimeMillis()}.png")
        photoFile.createNewFile()
        val bos = ByteArrayOutputStream()
        canvasBitmap.compress(Bitmap.CompressFormat.PNG, 90, bos)
        val fos = FileOutputStream(photoFile)
        fos.write(bos.toByteArray())
        fos.flush()
        fos.close()
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
    }
}