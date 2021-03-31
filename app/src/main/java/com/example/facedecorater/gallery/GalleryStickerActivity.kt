package com.example.facedecorater.gallery

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.facedecorater.R
import kotlinx.android.synthetic.main.gallery_sketch_layout.*
import kotlinx.android.synthetic.main.gallery_sticker_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ClickableViewAccessibility")
@RequiresApi(Build.VERSION_CODES.P)
class GalleryStickerActivity : AppCompatActivity() {

    private val galleryRequestCode = 3
    private lateinit var saveDirectory: File
    private var stickerButtons: ArrayList<ImageButton>? = null
    private var stickers: ArrayList<ImageView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_sticker_layout)

        val uri = intent.getParcelableExtra<Uri>("uri")
        gallery_sticker_imageView.setImageURI(uri)


        saveDirectory = getOutputDirectory()

        setToolbar()
        stickers = ArrayList()
        stickerButtons = ArrayList<ImageButton>().apply {
            add(gallery_heartSticker)
            add(gallery_starSticker)
            add(gallery_sun)
        }

        gallery_heartSticker.setOnClickListener {
            addSticker(R.drawable.heart_sticker)
        }
        gallery_starSticker.setOnClickListener {
            addSticker(R.drawable.star_sticker)
        }
        gallery_sun.setOnClickListener {
            addSticker(R.drawable.bitsunglasses)
        }
        gallery_sticker_addButton.setOnClickListener {
            getImageFromGallery()
        }
        gallery_sticker_saveButton.setOnClickListener {
            saveImage()
        }
    }

    private fun setToolbar() {
        gallery_sticker_toolbar.apply {
            title = ""
            subtitle = ""
        }
        setSupportActionBar(gallery_sticker_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getImageFromGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        startActivityForResult(intent, galleryRequestCode)
    }

    private fun saveImage() {
        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val imageBitmap = getBitmapFromImageView(gallery_sticker_imageView)

        val canvasBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888)

        var canvas = Canvas(canvasBitmap).apply {
            drawBitmap(imageBitmap, gallery_sticker_imageView.x,gallery_sticker_imageView.y, null)
            for(view in stickers!!){
                val stickerBitmap = getBitmapFromImageView(view)
                drawBitmap(stickerBitmap, view.x, view.y, null)
            }
        }

        val photoFile = File(saveDirectory, "${System.currentTimeMillis()}.png")
        photoFile.createNewFile()
        val bos = ByteArrayOutputStream()
        canvasBitmap.compress(Bitmap.CompressFormat.PNG, 90, bos)
        val fos = FileOutputStream(photoFile)
        fos.write(bos.toByteArray())
        fos.flush()
        fos.close()
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
    }
    

    private fun getBitmapFromImageView(imageView: ImageView) : Bitmap{
        var canvasBitmap = Bitmap.createBitmap(imageView.width, imageView.height, Bitmap.Config.ARGB_8888)

        var canvas = Canvas(canvasBitmap)

        imageView.draw(canvas)
        return canvasBitmap
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == galleryRequestCode && resultCode == RESULT_OK && Objects.nonNull(data)) {
            val imageUri = data?.data
            addStickerButtonInRuntime(imageUri!!)
        }
    }

    /**
     *  I must use deprecated method because new way is required level is too high(android 11)
     */
    private fun addSticker(src: Int) {
        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var x: Double? = 0.0
        var y: Double? = 0.0
        var stickerBitmap = BitmapFactory.decodeResource(resources, src)
        var sticker = ImageView(this).apply {
            setX((displayMetrics.widthPixels/2).toFloat())
            setY((displayMetrics.heightPixels/2).toFloat())
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

    private fun addSticker(bitmap: Bitmap) {
        var x: Double? = 0.0
        var y: Double? = 0.0
        var sticker = ImageView(this).apply {
            setImageBitmap(bitmap)
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
        val src = ImageDecoder.createSource(this.contentResolver, imageUri)
        val bitmap = ImageDecoder.decodeBitmap(src)

        val image = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, false)

        var stickerButton = ImageButton(this).apply {
            id = View.generateViewId()
            setImageBitmap(image)
            setBackgroundColor(Color.TRANSPARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = ConstraintLayout.LayoutParams(
                dpToPixel(64),
                dpToPixel(64)
            ).also {
                it.marginStart = 5
            }

            setOnClickListener { addSticker(image) }
        }

        val constraintSet = ConstraintSet()

        gallery_sticker_layout.addView(stickerButton)

        if (stickerButtons!!.size >= 5){
            stickerButtons!!.removeAt(stickerButtons!!.lastIndex)
        }
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
    private fun dpToPixel(size : Int):Int{
        val scale = resources.displayMetrics.density
        return (size*scale).toInt()
    }
}
