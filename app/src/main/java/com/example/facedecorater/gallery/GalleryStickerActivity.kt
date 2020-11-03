package com.example.facedecorater.gallery

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sticker_layout.*

class GalleryStickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sticker_layout)



    }

    private fun addButtonInRuntime(){
        var button = Button(this).apply {
            id = View.generateViewId()
            text = "testButton"
            layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        }

        var constraintSet = ConstraintSet()

        gallery_sticker_layout.addView(button)

        constraintSet.apply {
            clone(gallery_sticker_layout)
            connect(button.id, ConstraintSet.START, gallery_sticker_layout.id, ConstraintSet.START)
            connect(button.id, ConstraintSet.TOP, gallery_sticker_layout.id, ConstraintSet.TOP)
        }.run { applyTo(gallery_sticker_layout) }
    }
}