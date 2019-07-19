package info.laht.threekt.scenes

import info.laht.threekt.core.Object3D
import info.laht.threekt.materials.Material

class Scene: Object3D() {

    var background: Background? = null

    var fog: Fog? = null

    var overrideMaterial: Material? = null

    var autoUpdate = true

    fun dispose() {
        dispatchEvent("dispose", this)
    }

    fun copy( source: Scene, recursive: Boolean ): Scene {

        super.copy(source, recursive)

        source.background?.also { background = it }
        source.fog?.also { fog = it.clone() }
        source.overrideMaterial?.also { overrideMaterial = it.clone() }

        autoUpdate = source.autoUpdate

        return this

    }

}
