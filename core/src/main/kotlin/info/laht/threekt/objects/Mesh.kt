package info.laht.threekt.objects

import info.laht.threekt.TrianglesDrawMode
import info.laht.threekt.core.*
import info.laht.threekt.materials.Material
import info.laht.threekt.math.*


open class Mesh(
    override val geometry: BufferGeometry,
    override val materials: MutableList<Material>
) : Object3DImpl(), GeometryObject, MaterialsObject {

    constructor(geometry: BufferGeometry, material: Material)
            : this(geometry, mutableListOf(material))

    var drawMode = TrianglesDrawMode

    private val raycastHelper by lazy { RaycastHelper() }

    init {

//        updateMorphTargets()
    }

    override fun raycast(raycaster: Raycaster, intersects: List<Intersection>) {
        TODO()
    }

    fun updateMorphTargets() {
        TODO()
    }

    fun copy(source: Mesh): Mesh {

        super<Object3DImpl>.copy(source, true)

        this.drawMode = source.drawMode

        return this

    }

    override fun clone(): Mesh {
        return Mesh(geometry, material).copy(this)
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
