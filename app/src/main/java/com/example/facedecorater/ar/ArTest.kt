package com.example.facedecorater.ar

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.AugmentedFaceNode
import java.util.*
import java.util.function.Consumer

class ArTest() : AppCompatActivity() {

    private lateinit var faceFragment: ArFragment
    private var faceRegionsRenderable: ModelRenderable? = null
    private var faceMeshTexture: Texture? = null
    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ar_screen)

        faceFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        // Load the face regions renderable.
        // This is a skinned model that renders 3D objects mapped to the regions of the augmented face.


        //텍스쳐는 없어도 돌아간다 render할 물체만 잘 구해서 적용해보자
        


        // Load the face mesh texture.
//        Texture.builder()
//            .setSource(this, R.drawable.fox_face_mesh_texture)
//            .build()
//            .thenAccept(Consumer { texture: Texture ->
//                faceMeshTexture = texture
//            })

        setMask();

        val sceneView = faceFragment.arSceneView

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
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
//                    faceNode.faceMeshTexture = faceMeshTexture
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

    private fun setMask(){
        ModelRenderable.builder()
//            .setSource(this, R.drawable.test)
            .setSource(this, R.raw.testobj)
            .build()
            .thenAccept(
                Consumer { modelRenderable: ModelRenderable ->
                    faceRegionsRenderable = modelRenderable
                    modelRenderable.isShadowCaster = false
                    modelRenderable.isShadowReceiver = false
                })
    }
}