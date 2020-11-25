package com.example.facedecorater.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_menu_layout.*
import java.util.*

class GalleryMenuActivity : AppCompatActivity() {

    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val permissionsRequestCode = 0
    private val galleryRequestCode = 1
    private var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_menu_layout)

        setToolbar()

        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, permissions, permissionsRequestCode)
        } else {
            getImageFromGallery()
        }

        gallery_menu_sticker_button.setOnClickListener {
            startWork("sticker")
        }
        gallery_menu_sketch_button.setOnClickListener {
            startWork("sketch")
        }
        gallery_menu_reselect_button.setOnClickListener {
            startWork("re")
        }
        gallery_menu_filter.setOnClickListener {
            startWork("filter")
        }
    }

    private fun startWork(type : String){
        when(type){
            "sticker" -> {
                val intent = Intent(this, GalleryStickerActivity::class.java)
                intent.putExtra("uri", imageUri)
                startActivity(intent)
            }
            "sketch" -> {
                val intent = Intent(this, GallerySketchActivity::class.java)
                intent.putExtra("uri", imageUri)
                startActivity(intent)
            }
            "re" -> {
                getImageFromGallery()
            }
            "filter"->{
                val intent = Intent(this, OpenCvActivity::class.java)
                intent.putExtra("uri", imageUri)
                startActivity(intent)
            }
        }
    }

    private fun getImageFromGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        startActivityForResult(intent, galleryRequestCode)
    }

    private fun checkPermissions() = permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setToolbar() {
        gallery_menu_toolbar.apply {
            title = ""
            subtitle = ""
        }
        setSupportActionBar(gallery_menu_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionsRequestCode) {
            if (!checkPermissions()) {
                //Display alterDialog to explain
            } else {
                //All permissions granted
                getImageFromGallery()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.gallery_sticker_menu, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == galleryRequestCode && resultCode == RESULT_OK && Objects.nonNull(data)) {
            imageUri = data?.data
            gallery_menu_imageView.setImageURI(imageUri)
        }
    }
}