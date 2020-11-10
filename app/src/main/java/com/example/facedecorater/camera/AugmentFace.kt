package com.example.facedecorater.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.facedecorater.R
import com.example.facedecorater.camera.feature.FaceArFragment
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode

class AugmentFace : AppCompatActivity() {

    private lateinit var arFragment: FaceArFragment
    private lateinit var faceRegionsRenderable: ModelRenderable
    private lateinit var faceMeshTexture: Texture
    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_ar)

//
//        //https://developers.google.com/ar/develop/java/camera-configs
//
//
//        val sharedSession = Session(this, EnumSet.of(Session.Feature.SHARED_CAMERA))
//        val sharedCamera = sharedSession.sharedCamera
//        val filter = CameraConfigFilter(sharedSession)
//        filter.apply {
//            targetFps = EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30)
//            depthSensorUsage = EnumSet.of(CameraConfig.DepthSensorUsage.DO_NOT_USE)
//        }
//        val cameraConfigList = sharedSession.getSupportedCameraConfigs(filter)
//        sharedSession.cameraConfig = cameraConfigList[0]
//        val cameraId = sharedSession.cameraConfig.cameraId

    }
}