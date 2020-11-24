package com.example.facedecorater.camera

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import com.example.facedecorater.camera.feature.FaceArFragment
import com.example.facedecorater.camera.feature.StickerFaceNode
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import kotlinx.android.synthetic.main.camera_sticker_2d_layout.*
import kotlinx.android.synthetic.main.gallery_sticker_layout.*

@SuppressLint("ClickableViewAccessibility")
class CameraSticker : AppCompatActivity() {

    private lateinit var arFragment: FaceArFragment
    private var faceNodeMap = HashMap<AugmentedFace, StickerFaceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_sticker_2d_layout)

        camera_heartSticker.setOnClickListener {
            addSticker(R.drawable.heart_sticker)
        }
        camera_starSticker.setOnClickListener {
            addSticker(R.drawable.star_sticker)
        }

        setAr()
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