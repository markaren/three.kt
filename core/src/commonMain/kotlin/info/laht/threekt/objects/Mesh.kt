package info.laht.threekt.objects

import info.laht.threekt.DrawMode
import info.laht.threekt.core.*
import info.laht.threekt.materials.Material
import info.laht.threekt.math.*

open class Mesh(
    override var geometry: BufferGeometry,
    override val materials: MutableList<Material>
) : Object3DImpl(), GeometryObject, MaterialsObject {

    var drawMode = DrawMode.Triangles

    private val raycastHelper by lazy { RaycastHelper() }
    private val morphTargetInfluences by lazy { mutableListOf<Int>() }
    private val morphTargetDictionary by lazy { mutableMapOf<String, Int>() }

    constructor(geometry: BufferGeometry, material: Material)
            : this(geometry, mutableListOf(material))

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

                        this.morphTargetInfluences.add(0)
                        this.morphTargetDictionary[name] = m

                    }


                }

            }


        }

    }


    override fun raycast(raycaster: Raycaster, intersects: List<Intersection>) {

        TODO()

//        with(raycastHelper) {
//
//            fun checkIntersection(
//                `object`: Object3D,
//                material: Material,
//                pA: Vector3,
//                pB: Vector3,
//                pC: Vector3,
//                point: Vector3
//            ): Intersection? {
//
//                val intersect = if (material.side == Side.Back) {
//
//                    ray.intersectTriangle(pC, pB, pA, true, point);
//
//                } else {
//
//                    ray.intersectTriangle(pA, pB, pC, material.side != Side.Double, point);
//
//                }
//
//                if (intersect == null) return null
//
//                intersectionPointWorld.copy(point);
//                intersectionPointWorld.applyMatrix4(`object`.matrixWorld);
//
//                val distance = raycaster.ray.origin.distanceTo(intersectionPointWorld);
//
//                if (distance < raycaster.near || distance > raycaster.far) return null;
//
//                return Intersection(
//                    distance = distance,
//                    point = intersectionPointWorld.clone(),
//                    `object` = `object`
//                )
//
//            }
//
//            fun checkBufferGeometryIntersection(
//                `object`: Object3D,
//                material: Material,
//                position: Vector3,
//                morphPosition: Vector3,
//                uv: Vector2,
//                uv2: Vector2,
//                a: Int,
//                b: Int,
//                c: Int
//            ): Intersection {
//
//                vA.fromBufferAttribute( position, a );
//                vB.fromBufferAttribute( position, b );
//                vC.fromBufferAttribute( position, c );
//
//                var morphInfluences = object.morphTargetInfluences;
//
//                if ( material.morphTargets && morphPosition && morphInfluences ) {
//
//                    morphA.set( 0, 0, 0 );
//                    morphB.set( 0, 0, 0 );
//                    morphC.set( 0, 0, 0 );
//
//                    for ( var i = 0, il = morphPosition.length; i < il; i ++ ) {
//
//                        var influence = morphInfluences[ i ];
//                        var morphAttribute = morphPosition[ i ];
//
//                        if ( influence === 0 ) continue;
//
//                        tempA.fromBufferAttribute( morphAttribute, a );
//                        tempB.fromBufferAttribute( morphAttribute, b );
//                        tempC.fromBufferAttribute( morphAttribute, c );
//
//                        morphA.addScaledVector( tempA.sub( vA ), influence );
//                        morphB.addScaledVector( tempB.sub( vB ), influence );
//                        morphC.addScaledVector( tempC.sub( vC ), influence );
//
//                    }
//
//                    vA.add( morphA );
//                    vB.add( morphB );
//                    vC.add( morphC );
//
//                }
//
//                val intersection = checkIntersection( `object`, material, vA, vB, vC, intersectionPoint );
//
//                if ( intersection != null ) {
//
//                    if ( uv ) {
//
//                        uvA.fromBufferAttribute( uv, a );
//                        uvB.fromBufferAttribute( uv, b );
//                        uvC.fromBufferAttribute( uv, c );
//
//                        intersection.uv = Triangle.getUV( intersectionPoint, vA, vB, vC, uvA, uvB, uvC, new Vector2() );
//
//                    }
//
//                    if ( uv2 ) {
//
//                        uvA.fromBufferAttribute( uv2, a );
//                        uvB.fromBufferAttribute( uv2, b );
//                        uvC.fromBufferAttribute( uv2, c );
//
//                        intersection.uv2 = Triangle.getUV( intersectionPoint, vA, vB, vC, uvA, uvB, uvC, new Vector2() );
//
//                    }
//
//                    var face = new Face3( a, b, c );
//                    Triangle.getNormal( vA, vB, vC, face.normal );
//
//                    intersection.face = face;
//
//                }
//
//                return intersection;
//
//            }
//
//        }

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
