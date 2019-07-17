package info.laht.threekt.objects

import info.laht.threekt.core.Object3D
import info.laht.threekt.materials.Material

class ImmidiateRenderObject(
    val material : Material
) : Object3D() {

    fun render( renderCallback: () -> Unit) {

    }

}