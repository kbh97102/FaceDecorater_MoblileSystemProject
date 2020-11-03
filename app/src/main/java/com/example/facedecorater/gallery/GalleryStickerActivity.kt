package com.example.facedecorater.gallery

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sticker_layout.*
import java.util.*

class GalleryStickerActivity : AppCompatActivity() {

    private val galleryRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sticker_layout)

        setToolbar()

        var intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        startActivityForResult(intent, galleryRequestCode)
    }

    private fun setToolbar() {
        gallery_sticker_toolbar.apply {
            title = ""
            subtitle = ""
        }
        setSupportActionBar(gallery_sticker_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.gallery_sticker_menu, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == galleryRequestCode && resultCode == RESULT_OK && Objects.nonNull(data)){
            var imageUri = data?.data
            gallery_sticker_imageView.setImageURI(imageUri)
        }
    }
}