package info.laht.threekt.scenes

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.Object3D
import info.laht.threekt.materials.Material
import info.laht.threekt.renderers.GLRenderTarget
import info.laht.threekt.renderers.GLRenderer

class Scene: Object3D() {

    var background: Background? = null

    var fog: _Fog? = null

    var overrideMaterial: Material? = null

    var autoUpdate = true

    var onBeforeRenderScene: ((GLRenderer, Scene, Camera, GLRenderTarget?) -> Unit)? = null

    fun copy( source: Scene, recursive: Boolean ): Scene {

        super.copy(source, recursive)

        source.background?.also { background = it }
        source.fog?.also { fog = it.clone() }
        source.overrideMaterial?.also { overrideMaterial = it.clone() }

        autoUpdate = source.autoUpdate

        return this

    }

    fun dispose() {
        dispatchEvent("dispose", this)
    }

}
