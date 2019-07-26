package info.laht.threekt.objects

import info.laht.threekt.core.*
import info.laht.threekt.materials.Material
import info.laht.threekt.materials.PointsMaterial
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Ray
import info.laht.threekt.math.Sphere


class Points(
    override val geometry: BufferGeometry = BufferGeometry(),
    override val material: PointsMaterial = PointsMaterial()
): Object3D(), GeometryObject, MaterialsObject {

    override val materials = mutableListOf<Material>()

    private val raycastHelper by lazy { RaycastHelper() }

    override fun raycast(raycaster: Raycaster, intersects: List<Intersection>) {

        with(raycastHelper) {

        }

        TODO()
    }

    override fun clone(): Points {
        return Points(geometry, material).copy(this, true) as Points
    }

    private inner class RaycastHelper {

        val inverseMatrix= Matrix4()
        val ray = Ray()
        val sphere= Sphere()

    }

}