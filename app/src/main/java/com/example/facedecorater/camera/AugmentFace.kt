package com.example.facedecorater.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import com.example.facedecorater.camera.feature.FaceArFragment
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.AugmentedFaceNode
import java.util.*

class AugmentFace : AppCompatActivity() {


    private var arFragment: FaceArFragment? = null

    private var faceRegionsRenderable: ModelRenderable? = null

    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_ar)

        arFragment = supportFragmentManager.findFragmentById(R.id.face_fragment) as FaceArFragment


        // Load the face regions renderable.
        // This is a skinned model that renders 3D objects mapped to the regions of the augmented face.
        ModelRenderable.builder()
            .setSource(this, R.raw.fox_face)
            .build()
            .thenAccept { modelRenderable: ModelRenderable ->
                faceRegionsRenderable = modelRenderable
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }


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

            // Make new AugmentedFaceNodes for any new faces.
            for (face in faceList) {
                if (!faceNodeMap.containsKey(face)) {
                    val faceNode = AugmentedFaceNode(face)
                    faceNode.setParent(scene)
                    faceNode.faceRegionsRenderable = faceRegionsRenderable
                    faceNodeMap.put(face, faceNode)
                }
            }

            // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
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
}