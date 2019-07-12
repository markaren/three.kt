package info.laht.threekt.objects

import info.laht.threekt.TrianglesDrawMode
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Intersection
import info.laht.threekt.core.Object3D
import info.laht.threekt.core.Raycaster
import info.laht.threekt.materials.Material
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.*


class Mesh(
    val geometry: BufferGeometry = BufferGeometry(),
    val material: Material = MeshBasicMaterial()
): Object3D() {

    var drawMode = TrianglesDrawMode

    private val raycastHelper by lazy { RaycastHelper() }

    init {
       // updateMorphTargets()
    }

    private inner class RaycastHelper {

        internal var inverseMatrix = Matrix4()
        internal var ray = Ray()
        internal var sphere = Sphere()

        internal var vA = Vector3()
        internal var vB = Vector3()
        internal var vC = Vector3()

        internal var tempA = Vector3()
        internal var tempB = Vector3()
        internal var tempC = Vector3()

        internal var morphA = Vector3()
        internal var morphB = Vector3()
        internal var morphC = Vector3()

        internal var uvA = Vector2()
        internal var uvB = Vector2()
        internal var uvC = Vector2()

        internal var intersectionPoint = Vector3()
        internal var intersectionPointWorld = Vector3()

    }
}