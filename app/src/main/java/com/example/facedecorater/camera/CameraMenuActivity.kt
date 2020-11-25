package com.example.facedecorater.camera

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.example.facedecorater.R
import com.example.facedecorater.camera.feature.CameraController
import kotlinx.android.synthetic.main.camera_menu_layout.*

class CameraMenuActivity : AppCompatActivity() {

    private var controller : CameraController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_menu_layout)

        setToolbar()

        controller = CameraController(
            previewView = camera_menu_preview,
            lifecycleOwner = this as LifecycleOwner,
            context = this
        )

        controller!!.startCamera()

        camera_menu_sketch_button.setOnClickListener {
            startActivity(Intent(this, CameraSketch::class.java))
        }
        camera_menu_3d_button.setOnClickListener {
            startActivity(Intent(this, AugmentFace::class.java))
        }
        camera_menu_sticker_button.setOnClickListener {
            startActivity(Intent(this, CameraSticker::class.java))
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