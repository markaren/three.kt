package info.laht.threekt.objects

import info.laht.threekt.core.*
import info.laht.threekt.materials.SpriteMaterial

class Sprite(
    val material: SpriteMaterial = SpriteMaterial()
) : Object3D(), GeometryObject {

    override val geometry = BufferGeometry()

    private val float32Array = floatArrayOf(
        -0.5f, -0.5f, 0f, 0f, 0f,
        0.5f, -0.5f, 0f, 1f, 0f,
        0.5f, 0.5f, 0f, 1f, 1f,
        -0.5f, 0.5f, 0f, 0f, 1f
    )

    override fun raycast(raycaster: Raycaster, intersects: List<Intersection>) {
        super.raycast(raycaster, intersects)
    }

}
