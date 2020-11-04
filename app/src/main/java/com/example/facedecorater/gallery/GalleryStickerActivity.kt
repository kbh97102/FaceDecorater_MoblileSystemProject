package com.example.facedecorater.gallery

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sticker_layout.*

@SuppressLint("ClickableViewAccessibility")
class GalleryStickerActivity : AppCompatActivity() {

    private var stickerButtons: ArrayList<ImageButton>? = null
    private var stickers : ArrayList<ImageView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sticker_layout)

        stickers = ArrayList()
        stickerButtons = ArrayList<ImageButton>().apply {
            add(gallery_heartSticker)
            add(gallery_starSticker)
        }

        gallery_heartSticker.setOnClickListener {
            addSticker(R.drawable.heart_sticker)
        }
        gallery_starSticker.setOnClickListener {
            addSticker(R.drawable.star_sticker)
        }
    }

    private fun addSticker(src: Int) {
        var x: Double? = 0.0
        var y: Double? = 0.0
        var stickerBitmap = BitmapFactory.decodeResource(resources, src)
        var sticker = ImageView(this).apply {
            setImageBitmap(stickerBitmap)
            setBackgroundColor(Color.TRANSPARENT)
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = view.x.toDouble() - event.rawX
                        y = view.y.toDouble() - event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        this.animate()
                            .x(event.rawX + x!!.toFloat())
                            .y(event.rawY + y!!.toFloat())
                            .setDuration(0)
                            .start()
                        true
                    }
                    else -> true
                }
            }
        }
        stickers?.add(sticker)
        gallery_sticker_layout.addView(sticker)
    }

    private fun addButtonInRuntime() {
        var button = Button(this).apply {
            id = View.generateViewId()
            text = "testButton"
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
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