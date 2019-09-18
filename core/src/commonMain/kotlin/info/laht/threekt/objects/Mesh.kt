package info.laht.threekt.objects

import info.laht.threekt.DrawMode
import info.laht.threekt.Side
import info.laht.threekt.core.*
import info.laht.threekt.materials.Material
import info.laht.threekt.materials.MaterialWithMorphTargets
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.*
import kotlin.math.max
import kotlin.math.min

open class Mesh(
        geometry: BufferGeometry? = null,
        materials: MutableList<Material>? = null
) : Object3DImpl(), GeometryObject, MaterialsObject, MorphTargetInfluencesObject {

    var drawMode = DrawMode.Triangles

    override var geometry: BufferGeometry = geometry ?: BufferGeometry()
    override val materials = materials ?: mutableListOf(MeshBasicMaterial() as Material)

    override val morphTargetInfluences by lazy { mutableListOf<Float>() }
    val morphTargetDictionary by lazy { mutableMapOf<String, Int>() }

    private val raycastHelper by lazy { RaycastHelper() }

    constructor() : this(null, null as MutableList<Material>?)

    constructor(geometry: BufferGeometry? = null) : this(geometry, null as MutableList<Material>?)

    constructor(geometry: BufferGeometry? = null, material: Material? = null)
            : this(geometry, material?.let { mutableListOf(material) })

    init {
        updateMorphTargets()
    }

    fun updateMorphTargets() {

        geometry.morphAttributes?.also { morphAttributes ->

            val keys = morphAttributes.keys.toList()

            if (keys.isNotEmpty()) {

                val morphAttribute = morphAttributes[keys[0]]

                if (morphAttribute != null) {

                    this.morphTargetInfluences.clear()
                    this.morphTargetDictionary.clear()

                    for (m in 0 until morphAttribute.size) {

                        name = m.toString()

                        this.morphTargetInfluences.add(0f)
                        this.morphTargetDictionary[name] = m

                    }

                }

            }

        }

    }

    override fun raycast(raycaster: Raycaster, intersects: MutableList<Intersection>) {

        with(raycastHelper) {

            fun checkIntersection(
                    `object`: Object3D,
                    material: Material,
                    pA: Vector3,
                    pB: Vector3,
                    pC: Vector3,
                    point: Vector3
            ): Intersection? {

                if (material.side == Side.Back) {

                    ray.intersectTriangle(pC, pB, pA, true, point)

                } else {

                    ray.intersectTriangle(pA, pB, pC, material.side != Side.Double, point)

                } ?: return null // when intersect == null

                intersectionPointWorld.copy(point)
                intersectionPointWorld.applyMatrix4(`object`.matrixWorld)

                val distance = raycaster.ray.origin.distanceTo(intersectionPointWorld)

                if (distance < raycaster.near || distance > raycaster.far) return null

                return Intersection(
                        distance = distance,
                        point = intersectionPointWorld.clone(),
                        `object` = `object`
                )

            }

            fun checkBufferGeometryIntersection(
                    `object`: Mesh,
                    material: Material,
                    position: FloatBufferAttribute,
                    morphPosition: List<FloatBufferAttribute>?,
                    uv: FloatBufferAttribute?,
                    uv2: FloatBufferAttribute?,
                    a: Int,
                    b: Int,
                    c: Int
            ): Intersection? {

                position.toVector3(a, vA)
                position.toVector3(b, vB)
                position.toVector3(c, vC)

                val morphInfluences = `object`.morphTargetInfluences

                if (material is MaterialWithMorphTargets && morphPosition != null) {

                    morphA.set(0, 0, 0)
                    morphB.set(0, 0, 0)
                    morphC.set(0, 0, 0)

                    for (i in 0 until morphPosition.size) {

                        val influence = morphInfluences[i]
                        val morphAttribute = morphPosition[i]

                        if (influence == 0f) continue

                        morphAttribute.toVector3(a, tempA)
                        morphAttribute.toVector3(b, tempB)
                        morphAttribute.toVector3(c, tempC)

                        morphA.addScaledVector(tempA.sub(vA), influence)
                        morphB.addScaledVector(tempB.sub(vB), influence)
                        morphC.addScaledVector(tempC.sub(vC), influence)

                    }

                    vA.add(morphA)
                    vB.add(morphB)
                    vC.add(morphC)

                }

                val intersection = checkIntersection(`object`, material, vA, vB, vC, intersectionPoint)

                if (intersection != null) {

                    if (uv != null) {

                        uv.toVector2(a, uvA)
                        uv.toVector2(b, uvB)
                        uv.toVector2(c, uvC)

                        intersection.uv = Triangle.getUV(intersectionPoint, vA, vB, vC, uvA, uvB, uvC)

                    }

                    if (uv2 != null) {

                        uv2.toVector2(a, uvA)
                        uv2.toVector2(b, uvB)
                        uv2.toVector2(c, uvC)

                        intersection.uv2 = Triangle.getUV(intersectionPoint, vA, vB, vC, uvA, uvB, uvC)

                    }

                    val face = Face3(a, b, c)
                    Triangle.getNormal(vA, vB, vC, face.normal)

                    intersection.face = face

                }

                return intersection

            }

            // Checking boundingSphere distance to ray

            if (geometry.boundingSphere == null) geometry.computeBoundingSphere()

            sphere.copy(geometry.boundingSphere!!)
            sphere.applyMatrix4(matrixWorld)

            if (!raycaster.ray.intersectsSphere(sphere)) return

            //

            inverseMatrix.getInverse(matrixWorld)
            ray.copy(raycaster.ray).applyMatrix4(inverseMatrix)

            // Check boundingBox before continuing

            if (geometry.boundingBox != null) {

                if (!ray.intersectsBox(geometry.boundingBox!!)) return

            }

            var intersection: Intersection?

            var a: Int
            var b: Int
            var c: Int
            val index = geometry.index
            val position = geometry.attributes.position
            val morphPosition = geometry.morphAttributes?.getValue("position") as List<FloatBufferAttribute>?
            val uv = geometry.attributes.uv
            val uv2 = geometry.attributes.uv2
//            var groups = geometry.groups
            val drawRange = geometry.drawRange
//                var i, j, il, jl;
//                var group, groupMaterial;
            val start: Int
            val end: Int

            if (index != null) {

                // indexed buffer geometry

                if (isMultiMaterial) {

                    TODO()

                } else {

                    start = max(0, drawRange.start)
                    end = min(index.count, (drawRange.start + drawRange.count))

                    for (i in start until end step 3) {

                        a = index.getX(i)
                        b = index.getX(i + 1)
                        c = index.getX(i + 2)

                        intersection = checkBufferGeometryIntersection(this@Mesh, material, position!!, morphPosition, uv, uv2, a, b, c)

                        if (intersection != null) {

                            intersection.faceIndex = i / 3 // triangle number in indexed buffer semantics
                            intersects.add(intersection)

                        }

                    }

                }

            } else if (position != null) {

                // non-indexed buffer geometry

                if (isMultiMaterial) {

                    TODO()

                } else {

                    start = max(0, drawRange.start)
                    end = min(position.count, (drawRange.start + drawRange.count))

                    for (i in start until end step 3) {

                        a = i
                        b = i + 1
                        c = i + 2

                        intersection = checkBufferGeometryIntersection(this@Mesh, material, position, morphPosition, uv, uv2, a, b, c)

                        if (intersection != null) {

                            intersection.faceIndex = i / 3 // triangle number in non-indexed buffer semantics
                            intersects.add(intersection)

                        }

                    }

                }


            }

        }


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

        var inverseMatrix = Matrix4()
        var ray = Ray()
        var sphere = Sphere()

        var vA = Vector3()
        var vB = Vector3()
        var vC = Vector3()

        var tempA = Vector3()
        var tempB = Vector3()
        var tempC = Vector3()

        var morphA = Vector3()
        var morphB = Vector3()
        var morphC = Vector3()

        var uvA = Vector2()
        var uvB = Vector2()
        var uvC = Vector2()

        var intersectionPoint = Vector3()
        var intersectionPointWorld = Vector3()

    }

}
