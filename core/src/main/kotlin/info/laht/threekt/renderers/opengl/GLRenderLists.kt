package info.laht.threekt.renderers.opengl

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.*
import info.laht.threekt.core.GeometryGroup
import info.laht.threekt.materials.Material
import info.laht.threekt.objects.Group
import info.laht.threekt.scenes.Scene
import kotlin.math.roundToInt

internal class GLRenderLists {

    private val onSceneDispose = OnSceneDispose()

    private val lists = mutableMapOf<Int, MutableMap<Int, GLRenderList>>()

    fun get(scene: Scene, camera: Camera): GLRenderList {

        val cameras = lists[scene.id]

        val list: GLRenderList
        if (cameras == null) {
            list = GLRenderList()
            lists[scene.id] = mutableMapOf(camera.id to list)
            scene.addEventListener("dispose", onSceneDispose)
        } else {
            list = cameras[camera.id] ?: GLRenderList().also {
                cameras[camera.id] = it
            }
        }

        return list

    }

    fun dispose() {
        lists.clear()
    }

    private inner class OnSceneDispose : EventLister {

        override fun onEvent(event: Event) {
            val scene = event.target as Scene
            scene.removeEventListener("dispose", this)
        }

    }

}

