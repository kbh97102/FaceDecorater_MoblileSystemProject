package com.example.facedecorater.gallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sketch_layout.*

class GallerySketchActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sketch_layout)

        setToolbar()
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