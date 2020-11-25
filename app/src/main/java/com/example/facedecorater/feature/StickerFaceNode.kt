package com.example.facedecorater.feature

import android.content.Context
import com.example.facedecorater.R
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.camera_sticker_element_layout.view.*

/**
 * 사실 가면기능임
 * 요즘 유행한다길래 만듬
 */

class StickerFaceNode(private val context: Context, augmentedFace: AugmentedFace?) :
    AugmentedFaceNode(augmentedFace) {

    private var eyeNodeLeft: Node? = null
    private var eyeNodeRight: Node? = null
    private var noseNode: Node? = null
    private var mouseNode: Node? = null

    override fun onActivate() {
        super.onActivate()
        eyeNodeLeft = Node()
        eyeNodeLeft!!.setParent(this)

        eyeNodeRight = Node()
        eyeNodeRight!!.setParent(this)

        noseNode = Node()
        noseNode!!.setParent(this)

        mouseNode = Node()
        mouseNode!!.setParent(this)

        ViewRenderable.builder()
            .setView(context, R.layout.camera_sticker_element_layout)
            .build()
            .thenAccept {
                it.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                }
                eyeNodeRight!!.renderable = it
                eyeNodeLeft!!.renderable = it
            }

        ViewRenderable.builder()
            .setView(context, R.layout.camera_sticker_element_layout)
            .build()
            .thenAccept {
                it.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                }
                it.view.camera_sticker_imageView.setImageResource(R.drawable.testnose)
                mouseNode?.renderable = it
            }
    }

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)
        augmentedFace?.let {
            getRegionPose(Region.LEFT_EYE)?.let {
                eyeNodeLeft?.localPosition = Vector3(it.x - 0.01f, it.y - 0.025f, it.z + 0.015f)
                eyeNodeLeft?.localScale = Vector3(0.055f, 0.055f, 0.055f)
                eyeNodeLeft?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), -10f)
            }

            getRegionPose(Region.RIGHT_EYE)?.let {
                eyeNodeRight?.localPosition = Vector3(it.x + 0.01f, it.y - 0.025f, it.z + 0.015f)
                eyeNodeRight?.localScale = Vector3(0.055f, 0.055f, 0.055f)
                eyeNodeRight?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), 10f)
            }

            getRegionPose(Region.NOSE)?.let {
                noseNode?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
                noseNode?.localScale = Vector3(0.055f, 0.055f, 0.055f)
//                noseNode?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), 10f)
            }

            getRegionPose(Region.MOUSE)?.let {
                mouseNode?.localPosition = Vector3(it.x, it.y - 0.1f, it.z + 0.015f)
                mouseNode?.localScale = Vector3(0.07f, 0.07f, 0.07f)
            }
        }
    }

    private fun getRegionPose(region: Region): Vector3? {
        val buffer = augmentedFace?.meshVertices
        if (buffer != null) {
            return when (region) {
                Region.LEFT_EYE ->
                    Vector3(buffer.get(374 * 3), buffer.get(374 * 3 + 1), buffer.get(374 * 3 + 2))
                Region.RIGHT_EYE ->
                    Vector3(buffer.get(145 * 3), buffer.get(145 * 3 + 1), buffer.get(145 * 3 + 2))
                Region.MOUSE ->
                    Vector3(
                        buffer.get(11 * 3),
                        buffer.get(11 * 3 + 1),
                        buffer.get(11 * 3 + 2)
                    )
                Region.NOSE ->
                    Vector3(
                        buffer.get(1 * 3),
                        buffer.get(1 * 3 + 1),
                        buffer.get(1 * 3 + 2)
                    )
            }
        }
        return null
    }

}