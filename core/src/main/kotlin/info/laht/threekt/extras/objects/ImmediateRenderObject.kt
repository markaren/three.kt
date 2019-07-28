package info.laht.threekt.extras.objects

import info.laht.threekt.core.MaterialObject
import info.laht.threekt.core.Object3D
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.materials.Material

class ImmediateRenderObject(
    override val material: Material
): Object3DImpl(), MaterialObject {

    var render: () -> Unit = {}

}
