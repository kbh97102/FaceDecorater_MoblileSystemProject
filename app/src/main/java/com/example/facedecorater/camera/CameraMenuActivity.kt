package com.example.facedecorater.camera

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.camera_menu_layout.*

class CameraMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_menu_layout)

        setToolbar()

        camera_menu_sketch_button.setOnClickListener {
            startActivity(Intent(this, CameraSketch::class.java))
        }
        camera_menu_3d_button.setOnClickListener {
            startActivity(Intent(this, AugmentFace::class.java))
        }
        camera_menu_sticker_button.setOnClickListener {

        }
    }

    private fun setToolbar() {
        camera_menu_toolbar.apply {
            title = ""
            subtitle = ""
        }
        setSupportActionBar(camera_menu_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}