package com.example.facedecorater.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.facedecorater.R
import com.example.facedecorater.feature.FaceArFragment
import com.example.facedecorater.feature.StickerFaceNode
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import kotlinx.android.synthetic.main.camera_sticker_2d_layout.*
import kotlinx.android.synthetic.main.gallery_sticker_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("ClickableViewAccessibility")
class CameraSticker : AppCompatActivity() {

    private lateinit var arFragment: FaceArFragment
    private var faceNodeMap = HashMap<AugmentedFace, StickerFaceNode>()
    private lateinit var stickers: ArrayList<ImageView>
    private var stickerButtons: ArrayList<ImageButton>? = null
    private var stickerRequestCode = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_sticker_2d_layout)

        stickerButtons = ArrayList()
        stickers = ArrayList()

        stickerButtons?.apply {
            add(camera_heartSticker)
            add(camera_starSticker)
            add(camera_sun)
        }

        camera_heartSticker.setOnClickListener {
            addSticker(R.drawable.heart_sticker)
        }
        camera_starSticker.setOnClickListener {
            addSticker(R.drawable.star_sticker)
        }
        camera_sun.setOnClickListener {
            addSticker(R.drawable.sun)
        }
        camera_sticker_saveButton.setOnClickListener {
            saveImage()
        }
        camera_sticker_addButton.setOnClickListener {
            getImageFromGallery()
        }

        setAr()
    }

    private fun saveImage() {
        val photoFile = File(getOutputDirectory(), "test.jpeg")
        photoFile.createNewFile()

        val bitmap = Bitmap.createBitmap(
            arFragment.arSceneView.width,
            arFragment.arSceneView.height,
            Bitmap.Config.ARGB_8888
        )

        val handlerThread = HandlerThread("Save Image")
        handlerThread.start()
        PixelCopy.request(arFragment.arSceneView, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                saveImageWithSticker(bitmap)
            }
            handlerThread.quitSafely()
        }, Handler(handlerThread.looper))
    }

    private fun saveImageWithSticker(background: Bitmap) {
        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val canvasBitmap = Bitmap.createBitmap(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            Bitmap.Config.ARGB_8888
        )

        var canvas = Canvas(canvasBitmap).apply {
            drawBitmap(
                background,
                camera_sticker_toolbar.x,
                camera_sticker_toolbar.y + camera_sticker_toolbar.height,
                null
            )
            for (view in stickers) {
                val stickerBitmap = getBitmapFromImageView(view)
                drawBitmap(stickerBitmap, view.x, view.y, null)
            }
        }

        val photoFile = File(getOutputDirectory(), "StickerTest.png")
        photoFile.createNewFile()
        val bos = ByteArrayOutputStream()
        canvasBitmap.compress(Bitmap.CompressFormat.PNG, 90, bos)
        val fos = FileOutputStream(photoFile)
        fos.write(bos.toByteArray())
        fos.flush()
        fos.close()
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
    }

    private fun getBitmapFromImageView(view: ImageView): Bitmap {
        val test = view.drawable as BitmapDrawable
        return test.bitmap
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
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
            setX((displayMetrics.widthPixels / 2).toFloat())
            setY((displayMetrics.heightPixels / 2).toFloat())
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
        stickers.add(sticker)
        camera_sticker_2d_layout.addView(sticker)
    }

    private fun setAr() {
        arFragment = camera_sticker_fragment as FaceArFragment

        val sceneView = arFragment.arSceneView
        val scene = sceneView.scene

        scene.addOnUpdateListener {
            sceneView.session?.getAllTrackables(AugmentedFace::class.java)?.let {
                for (face in it) {
                    if (!faceNodeMap.containsKey(face)) {
                        val faceNode = StickerFaceNode(this, face)
                        faceNode.setParent(scene)
                        faceNodeMap[face] = faceNode
                    }
                }

                val iter = faceNodeMap.entries.iterator()
                while (iter.hasNext()) {
                    val entry = iter.next()
                    val face = entry.key
                    if (face.trackingState == TrackingState.STOPPED) {
                        val faceNode = entry.value
                        faceNode.setParent(null)
                        iter.remove()
                    }
                }
            }
        }
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
        stickers.add(sticker)
        camera_sticker_2d_layout.addView(sticker)
    }

    private fun getImageFromGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        startActivityForResult(intent, stickerRequestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == stickerRequestCode && resultCode == RESULT_OK && Objects.nonNull(data)) {
            val imageUri = data?.data
            addStickerButtonInRuntime(imageUri!!)
        }
    }

    private fun addStickerButtonInRuntime(imageUri: Uri) {
        val src = ImageDecoder.createSource(this.contentResolver, imageUri)
        val bitmap = ImageDecoder.decodeBitmap(src)

        val image = Bitmap.createScaledBitmap(bitmap, bitmap.width,bitmap.height, false)

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

        camera_sticker_2d_layout.addView(stickerButton)

        if (stickerButtons!!.size >= 5) {
            stickerButtons!!.removeAt(stickerButtons!!.lastIndex)
        }
        constraintSet.apply {
            clone(camera_sticker_2d_layout)
            connect(
                stickerButton.id,
                ConstraintSet.START,
                stickerButtons?.get(stickerButtons!!.lastIndex)!!.id,
                ConstraintSet.END
            )
            connect(
                stickerButton.id,
                ConstraintSet.TOP,
                camera_sticker_addButton_topGuideline.id,
                ConstraintSet.BOTTOM
            )
            connect(
                stickerButton.id,
                ConstraintSet.BOTTOM,
                camera_sticker_addButton_bottomGuideline.id,
                ConstraintSet.TOP
            )
        }.run { applyTo(camera_sticker_2d_layout) }
        stickerButtons?.add(stickerButton)
    }

    private fun dpToPixel(size: Int): Int {
        val scale = resources.displayMetrics.density
        return (size * scale).toInt()
    }
}