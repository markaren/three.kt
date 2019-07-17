package info.laht.threekt.scenes

import info.laht.threekt.core.Object3D

class Scene: Object3D() {

    val background = null

    val autoUpdate = true

    fun dispose() {
        dispatchEvent("dispose", this)
    }

}
