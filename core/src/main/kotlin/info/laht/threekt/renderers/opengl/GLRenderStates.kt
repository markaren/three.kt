package info.laht.threekt.renderers.opengl

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.Event
import info.laht.threekt.core.EventLister
import info.laht.threekt.scenes.Scene

internal class GLRenderStates {

    private var renderStates = mutableMapOf<Int, MutableMap<Int, GLRenderState>>()

    private val onSceneDispose = OnSceneDispose()

    fun get(scene: Scene, camera: Camera): GLRenderState {

        return if (renderStates[scene.id] == null) {

            GLRenderState().also { renderState ->
                renderStates[scene.id] = mutableMapOf(camera.id to renderState)
                scene.addEventListener("dispose", onSceneDispose)
            }

        } else {

            if (renderStates[scene.id]?.get(camera.id) == null) {
                GLRenderState().also { renderState ->
                    renderStates[scene.id]!![camera.id] = renderState
                }
            } else {
                renderStates[scene.id]!![camera.id]!!
            }

        }

    }

    fun dispose() {
        renderStates.clear()
    }

    private inner class OnSceneDispose : EventLister {

        override fun onEvent(event: Event) {
            val scene = event.target as Scene
            scene.removeEventListener("dispose", this)
            renderStates.remove(scene.id)
        }

    }

}
