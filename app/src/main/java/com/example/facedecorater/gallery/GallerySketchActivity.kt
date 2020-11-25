package com.example.facedecorater.gallery

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.example.facedecorater.R
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.gallery_sketch_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class GallerySketchActivity : AppCompatActivity() {

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var sketchView: SketchView
    private lateinit var saveDirectory: File
    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sketch_layout)

        val uri = intent.getParcelableExtra<Uri>("uri")
        gallery_sketch_imageView.setImageURI(uri)

        saveDirectory = getOutputDirectory()
        setToolbar()
        addSketchView()

        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close)

        gallery_brush_button.setOnClickListener {
            animFab()
        }
        gallery_sketch_color_button.setOnClickListener {
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
        gallery_sketch_size_button.setOnClickListener {
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
        gallery_sketch_saveButton.setOnClickListener {
            saveImage()
        }
    }

    private fun saveImage() {
        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val imageBitmap = getBitmapFromImageView(gallery_sketch_imageView)

        val canvasBitmap = Bitmap.createBitmap(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            Bitmap.Config.ARGB_8888
        )

        var canvas = Canvas(canvasBitmap).apply {
            drawBitmap(imageBitmap, gallery_sketch_imageView.x, gallery_sketch_imageView.y, null)
            drawBitmap(sketchView.getBitmap(), sketchView.x, sketchView.y, null)
        }

        val photoFile = File(saveDirectory, "${System.currentTimeMillis()}.png")
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

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun animFab() {
        if (isFabOpen) {
            gallery_sketch_color_button.apply {
                startAnimation(fab_close)
                isClickable = false
            }
            gallery_sketch_size_button.apply {
                startAnimation(fab_close)
                isClickable = false
            }
            isFabOpen = false
        } else {
            gallery_sketch_color_button.apply {
                startAnimation(fab_open)
                isClickable = true
            }
            gallery_sketch_size_button.apply {
                startAnimation(fab_open)
                isClickable = true
            }
            isFabOpen = true
        }
    }

    private fun addSketchView() {
        sketchView = SketchView(this).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.TRANSPARENT)
        }
        gallery_sketch_layout.addView(sketchView)
        val constraintSet = ConstraintSet()
        constraintSet.apply {
            clone(gallery_sketch_layout)
            connect(
                sketchView.id,
                ConstraintSet.START,
                gallery_sketch_layout.id,
                ConstraintSet.START
            )
            connect(sketchView.id, ConstraintSet.END, gallery_sketch_layout.id, ConstraintSet.END)
            connect(
                sketchView.id,
                ConstraintSet.TOP,
                gallery_sketch_toolbar.id,
                ConstraintSet.BOTTOM
            )
            connect(
                sketchView.id,
                ConstraintSet.BOTTOM,
                gallery_sketch_layout.id,
                ConstraintSet.BOTTOM
            )
        }.run { applyTo(gallery_sketch_layout) }
    }

    private fun setToolbar() {
        gallery_sketch_toolbar.apply {
            title = ""
            subtitle = ""
        }
        setSupportActionBar(gallery_sketch_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return false
    }
}