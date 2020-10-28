package com.example.facedecorater.gallery

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sticker_layout.*

class GalleryStickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sticker_layout)

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
}