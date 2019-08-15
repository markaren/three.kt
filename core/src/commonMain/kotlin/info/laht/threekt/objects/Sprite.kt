package info.laht.threekt.objects

import info.laht.threekt.core.*
import info.laht.threekt.materials.SpriteMaterial
import info.laht.threekt.math.Vector2

class Sprite(
    override var material: SpriteMaterial = SpriteMaterial()
) : Object3DImpl(), GeometryObject, MaterialObject {

    override var geometry = BufferGeometry()

    val center = Vector2(0.5f, 0.5f)

    init {

        val float32Array = floatArrayOf(
            -0.5f, -0.5f, 0f, 0f, 0f,
            0.5f, -0.5f, 0f, 1f, 0f,
            0.5f, 0.5f, 0f, 1f, 1f,
            -0.5f, 0.5f, 0f, 0f, 1f
        )

        geometry.setIndex(IntBufferAttribute(intArrayOf(0, 1, 2, 0, 2, 3), 1))
        TODO()
//        geometry.addAttribute( "position",  InterleavedBufferAttribute( interleavedBuffer, 3, 0, false ) );
//        geometry.addAttribute( "uv",  InterleavedBufferAttribute( interleavedBuffer, 2, 3, false ) );


    }

    override fun raycast(raycaster: Raycaster, intersects: MutableList<Intersection>) {
        TODO()
    }

}
