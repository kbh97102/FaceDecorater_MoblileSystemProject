package com.example.facedecorater.gallery

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sticker_layout.*
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ClickableViewAccessibility")
class GalleryStickerActivity : AppCompatActivity() {

    private val galleryRequestCode = 3
    private var stickerButtons: ArrayList<ImageButton>? = null
    private var stickers: ArrayList<ImageView>? = null

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
        gallery_sticker_addButton.setOnClickListener {
            getImageFromGallery()
        }
    }

    private fun getImageFromGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        startActivityForResult(intent, galleryRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == galleryRequestCode && resultCode == RESULT_OK && Objects.nonNull(data)) {
            val imageUri = data?.data
            addStickerButtonInRuntime(imageUri!!)
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

    private fun addSticker(imageUri: Uri) {
        var x: Double? = 0.0
        var y: Double? = 0.0
        var sticker = ImageView(this).apply {
            setImageURI(imageUri)
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

    private fun addStickerButtonInRuntime(imageUri: Uri) {
        var stickerButton = ImageButton(this).apply {
            id = View.generateViewId()
            setImageURI(imageUri)
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { addSticker(imageUri) }
        }

        val constraintSet = ConstraintSet()

        gallery_sticker_layout.addView(stickerButton)

        constraintSet.apply {
            clone(gallery_sticker_layout)
            connect(
                stickerButton.id,
                ConstraintSet.START,
                stickerButtons?.get(stickerButtons!!.lastIndex)!!.id,
                ConstraintSet.END
            )
            connect(
                stickerButton.id,
                ConstraintSet.TOP,
                gallery_sticker_addButton_topGuideline.id,
                ConstraintSet.BOTTOM
            )
            connect(
                stickerButton.id,
                ConstraintSet.BOTTOM,
                gallery_sticker_addButton_bottomGuideline.id,
                ConstraintSet.TOP
            )
        }.run { applyTo(gallery_sticker_layout) }
        stickerButtons?.add(stickerButton)
    }
}