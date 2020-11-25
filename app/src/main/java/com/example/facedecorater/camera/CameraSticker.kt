package com.example.facedecorater.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.PixelCopy
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import com.example.facedecorater.feature.FaceArFragment
import com.example.facedecorater.feature.StickerFaceNode
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import kotlinx.android.synthetic.main.camera_sticker_2d_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@SuppressLint("ClickableViewAccessibility")
class CameraSticker : AppCompatActivity() {

    private lateinit var arFragment: FaceArFragment
    private var faceNodeMap = HashMap<AugmentedFace, StickerFaceNode>()
    private lateinit var stickers: ArrayList<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_sticker_2d_layout)

        stickers = ArrayList()

        camera_heartSticker.setOnClickListener {
            addSticker(R.drawable.heart_sticker)
        }
        camera_starSticker.setOnClickListener {
            addSticker(R.drawable.star_sticker)
        }
        camera_sticker_saveButton.setOnClickListener {
            saveImage()
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
}