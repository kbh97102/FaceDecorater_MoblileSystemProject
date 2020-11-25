package com.example.facedecorater.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import com.example.facedecorater.R
import com.example.facedecorater.camera.feature.CameraController
import com.example.facedecorater.gallery.SketchView
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.camera_sketch_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@RequiresApi(Build.VERSION_CODES.R)
class CameraSketch : AppCompatActivity() {

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private var isFabOpen = false
    private var cameraController: CameraController? = null
    private lateinit var sketchView: SketchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_sketch_layout)

        cameraController = CameraController(
            previewView = camera_sketch_previewView,
            lifecycleOwner = this as LifecycleOwner,
            context = this
        )

        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close)

        setToolbar()

        addSketchView()
        setButtonListener()

        cameraController?.startCamera()
    }

    private fun setToolbar() {
        camera_sketch_toolbar.apply {
            title = ""
            subtitle = ""
        }
        setSupportActionBar(camera_sketch_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun setButtonListener() {
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
        camera_sketch_saveButton.setOnClickListener {
            saveImage()
        }
        camera_sketch_takeButton.setOnClickListener {
            if (camera_sketch_imageView.visibility == View.INVISIBLE) {
                cameraController?.takePicture(getOutputDirectory(), camera_sketch_imageView)
            } else if (camera_sketch_imageView.visibility == View.VISIBLE) {
                camera_sketch_imageView.visibility = View.INVISIBLE
                camera_sketch_imageView.setImageDrawable(null)
                cameraController?.startCamera()
            }
        }
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
                camera_sketch_toolbar.id,
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
            if (camera_sketch_imageView.visibility == View.INVISIBLE) {
                drawBitmap(
                    imageBitmap!!,
                    camera_sketch_previewView.x,
                    camera_sketch_previewView.y,
                    null
                )
            } else {
                drawBitmap(
                    getBitmapFromImageView(camera_sketch_imageView),
                    camera_sketch_imageView.x,
                    camera_sketch_imageView.y,
                    null
                )
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

    private fun getBitmapFromImageView(imageView: ImageView): Bitmap {
        var canvasBitmap =
            Bitmap.createBitmap(imageView.width, imageView.height, Bitmap.Config.ARGB_8888)

        var canvas = Canvas(canvasBitmap)

        imageView.draw(canvas)
        return canvasBitmap
    }
}