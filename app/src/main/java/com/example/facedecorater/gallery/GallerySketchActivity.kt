package com.example.facedecorater.gallery

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.example.facedecorater.R
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.gallery_sketch_layout.*

class GallerySketchActivity : AppCompatActivity() {

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var sketchView : SketchView
    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sketch_layout)

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
}