package com.example.facedecorater.camera

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.example.facedecorater.R
import com.example.facedecorater.camera.feature.CameraController
import kotlinx.android.synthetic.main.camera_menu_layout.*
import java.util.*

class CameraMenuActivity : AppCompatActivity() {

    private var controller: CameraController? = null

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
            startWork("sketch")
        }
        camera_menu_3d_button.setOnClickListener {
            startWork("3d")
        }
        camera_menu_sticker_button.setOnClickListener {
            startWork("sticker")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        if (Objects.nonNull(controller)) {
            controller!!.startCamera()
        }
    }

    private fun startWork(type: String) {
        controller!!.unbind()
        when (type) {
            "sketch" -> startActivity(Intent(this, CameraSketch::class.java))
            "3d" -> startActivity(Intent(this, AugmentFace::class.java))
            "sticker" -> startActivity(Intent(this, CameraSticker::class.java))
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