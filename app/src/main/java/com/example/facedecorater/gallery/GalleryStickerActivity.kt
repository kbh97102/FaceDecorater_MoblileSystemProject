package com.example.facedecorater.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sticker_layout.*
import java.util.*

class GalleryStickerActivity : AppCompatActivity() {

    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val permissionsRequestCode = 0
    private val galleryRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sticker_layout)

        setToolbar()

        if (!checkPermissions()) {
           ActivityCompat.requestPermissions(this, permissions, permissionsRequestCode)
        }


        var intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        startActivityForResult(intent, galleryRequestCode)
    }

    private fun checkPermissions() = permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setToolbar() {
        gallery_sticker_toolbar.apply {
            title = ""
            subtitle = ""
        }
        setSupportActionBar(gallery_sticker_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionsRequestCode){
            if (!checkPermissions()){
                //Display alterDialog to explain
            }
            else{
                //All permissions granted
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
            var imageUri = data?.data
            gallery_sticker_imageView.setImageURI(imageUri)
        }
    }
}