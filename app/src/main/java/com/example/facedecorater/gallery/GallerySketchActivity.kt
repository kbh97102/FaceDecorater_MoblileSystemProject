package com.example.facedecorater.gallery

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sketch_layout.*

class GallerySketchActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sketch_layout)

        setToolbar()

        val sketchView = SketchView(this).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.TRANSPARENT)
        }
        gallery_sketch_layout.addView(sketchView)
        val constraintSet = ConstraintSet()
        constraintSet.apply {
            clone(gallery_sketch_layout)
            connect(sketchView.id, ConstraintSet.START, gallery_sketch_layout.id, ConstraintSet.START)
            connect(sketchView.id, ConstraintSet.END, gallery_sketch_layout.id, ConstraintSet.END)
            connect(sketchView.id, ConstraintSet.TOP, gallery_sketch_toolbar.id, ConstraintSet.BOTTOM)
            connect(sketchView.id, ConstraintSet.BOTTOM, gallery_sketch_layout.id, ConstraintSet.BOTTOM)
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