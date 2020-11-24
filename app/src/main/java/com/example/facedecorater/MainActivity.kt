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

        gallery_button.setOnClickListener {
            startActivity(Intent(this, GalleryMenuActivity::class.java))
        }
        camera_button.setOnClickListener {
            startActivity(Intent(this, CameraMenuActivity::class.java))
        }
    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val menuInflater = menuInflater
//        menuInflater.inflate(R.menu.gallery_menu, menu)
//        return true
//    }
}