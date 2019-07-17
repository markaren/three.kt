package info.laht.threekt.objects

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Intersection
import info.laht.threekt.core.Object3D
import info.laht.threekt.core.Raycaster
import info.laht.threekt.materials.Material
import info.laht.threekt.materials.PointsMaterial
import info.laht.threekt.math.Ray
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Sphere


class Points(
    val geometry: BufferGeometry = BufferGeometry(),
    val material: Material = PointsMaterial()
): Object3D() {

    private val raycastHelper by lazy { RaycastHelper() }

    override fun raycast(raycaster: Raycaster, intersects: List<Intersection>) {

        with(raycastHelper) {

        }

        TODO()
    }

    override fun clone(): Points {
        return Points(geometry, material).copy(this) as Points
    }

    private inner class RaycastHelper {

        val inverseMatrix= Matrix4()
        val ray = Ray()
        val sphere= Sphere()

    }

}