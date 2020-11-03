package com.example.facedecorater

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import kotlinx.android.synthetic.main.gallery_menu_layout.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_menu_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.gallery_menu, menu)
        return true
    }
}