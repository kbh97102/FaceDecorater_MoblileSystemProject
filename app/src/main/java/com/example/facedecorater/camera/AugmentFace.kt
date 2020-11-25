package com.example.facedecorater.camera

import android.graphics.Bitmap
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import com.example.facedecorater.camera.feature.FaceArFragment
import com.google.ar.core.AugmentedFace
import com.google.ar.core.Frame
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.camera_ar.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class AugmentFace : AppCompatActivity() {


    private var arFragment: FaceArFragment? = null
    private var faceRegionsRenderable: ModelRenderable? = null
    private var modelList: ArrayList<ModelRenderable> = ArrayList()
    private var changeModel = false

    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_ar)

        arFragment = face_fragment as FaceArFragment

        buildRenderable(R.raw.yellow_sunglasses)
        buildRenderable(R.raw.sunglasses)
        buildRenderable(R.raw.fox_face)

        setArcore()

        camera_ar_takeButton.setOnClickListener {
            takeShot()
        }
        camera_ar_sticker_1.setOnClickListener {
            faceRegionsRenderable = modelList[0]
            changeModel = true
        }
        camera_ar_sticker_2.setOnClickListener {
            faceRegionsRenderable = modelList[1]
            changeModel = true
        }

    }

    private fun takeShot() {
        val photoFile = File(getOutputDirectory(), "test.jpeg")
        photoFile.createNewFile()

        val bitmap = Bitmap.createBitmap(
            arFragment!!.arSceneView.width,
            arFragment!!.arSceneView.height,
            Bitmap.Config.ARGB_8888
        )

        val handlerThread = HandlerThread("Save Image")
        handlerThread.start()
        PixelCopy.request(arFragment!!.arSceneView, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    val fileOutputStream = FileOutputStream(photoFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)
                    fileOutputStream.apply {
                        flush()
                        close()
                    }
                } catch (e: IOException) {
                    return@request
                }
            }
            handlerThread.quitSafely()
        }, Handler(handlerThread.looper))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun setArcore() {
        val sceneView: ArSceneView = arFragment!!.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        scene.addOnUpdateListener { frameTime: FrameTime? ->
            if (faceRegionsRenderable == null) {
                return@addOnUpdateListener
            }
            val faceList =
                sceneView.session!!.getAllTrackables(
                    AugmentedFace::class.java
                )

            for (face in faceList) {
                if (!faceNodeMap.containsKey(face)) {
                    val faceNode = AugmentedFaceNode(face)
                    faceNode.setParent(scene)
                    faceNode.faceRegionsRenderable = faceRegionsRenderable

                    faceNodeMap[face] = faceNode
                }
                if (changeModel) {
                    faceNodeMap.getValue(face).faceRegionsRenderable = faceRegionsRenderable
                    changeModel = false
                }
            }

            val iter: MutableIterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> =
                faceNodeMap.entries.iterator()
            while (iter.hasNext()) {
                val entry =
                    iter.next()
                val face = entry.key
                if (face.trackingState == TrackingState.STOPPED) {
                    val faceNode = entry.value
                    faceNode.setParent(null)
                    iter.remove()
                }
            }
        }
    }

    private fun buildRenderable(modelSource: Int) {
        ModelRenderable.builder()
            .setSource(this, modelSource)
            .build()
            .thenAccept { modelRenderable: ModelRenderable ->
                modelList.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
                faceRegionsRenderable = modelRenderable
            }
    }
}