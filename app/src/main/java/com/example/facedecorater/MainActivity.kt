package com.example.facedecorater

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import com.example.facedecorater.camera.CameraMenuActivity
import com.example.facedecorater.gallery.GalleryMenuActivity
import kotlinx.android.synthetic.main.gallery_menu_layout.*
import kotlinx.android.synthetic.main.start_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        select_gallery.setOnClickListener {
            startActivity(Intent(this, GalleryMenuActivity::class.java))
        }
        select_camera.setOnClickListener {
            startActivity(Intent(this, CameraMenuActivity::class.java))
        }
    }
}