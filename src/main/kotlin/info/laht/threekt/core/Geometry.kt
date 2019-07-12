//package info.laht.threekt.core
//
//import info.laht.threekt.math.*
//import java.util.concurrent.atomic.AtomicInteger
//
//open class Geometry: GeometryBase<Geometry>()  {
//
//    val id = geometryIdCount.getAndAdd(2)
//
//    val vertices = mutableListOf<Vector3>()
//    val colors = mutableListOf<Color>()
//    val faces = mutableListOf<Face3>()
//
//    val faceVertexUvs by lazy { mutableListOf(mutableListOf(mutableListOf<Vector2>())) }
//
//    val morphTarget = mutableListOf<MorphTarget>()
//    val morphNormals = mutableListOf<MorphNormals>()
//
//    val skinWeights = listOf<Vector4d>()
//    val skinIndices = listOf<Vector4i>()
//
//    val lineDistances = mutableListOf<Double>()
//
//    val elementsNeedUpdate = false
//    var verticesNeedUpdate = false
//    val uvsNeedUpdate = false
//    var normalsNeedUpdate = false
//    val colorsNeedUpdate = false
//    val lineDistancesNeedUpdate = false
//    val groupsNeedUpdate = false
//
//    override fun applyMatrix(matrix: Matrix4d): Geometry {
//
//        val normalMatrix = Matrix3d().getNormalMatrix(matrix);
//
//        vertices.forEach {
//            it.applyMatrix4(matrix)
//        }
//
//        faces.forEach { face ->
//            face.normal.applyMatrix3(normalMatrix).normalize()
//
//            face.vertexNormals.forEach { vertexNormal ->
//                vertexNormal.applyMatrix3(normalMatrix).normalize()
//            }
//
//        }
//
//        boundingBox?.also {
//            computeBoundingBox()
//        }
//
//        boundingSphere?.also {
//            computeBoundingSphere()
//        }
//
//        this.verticesNeedUpdate = true
//        this.normalsNeedUpdate = true
//
//        return this
//    }
//
//    fun fromBufferGeometry() {
//        TODO()
//    }
//
//    fun computeFaceNormals() {
//
//        val cb = Vector3()
//        val ab = Vector3()
//
//        faces.forEach { face ->
//            val vA = this.vertices[face.a];
//            val vB = this.vertices[face.b];
//            val vC = this.vertices[face.c];
//
//            cb.subVectors(vC, vB);
//            ab.subVectors(vA, vB);
//            cb.cross(ab);
//
//            cb.normalize();
//
//            face.normal.copy(cb);
//        }
//
//    }
//
//    fun computeVertexNormals() {
//        TODO()
//    }
//
//    fun computeFlatVertexNormals() {
//        TODO()
//    }
//
//    fun computeMorphNormals() {
//        TODO()
//    }
//
//    override fun computeBoundingBox() {
//
//        if (this.boundingBox == null) {
//            this.boundingBox = Box3()
//        }
//
//        this.boundingBox!!.setFromPoints(this.vertices);
//
//    }
//
//    override fun computeBoundingSphere() {
//
//        if (this.boundingSphere == null) {
//            this.boundingSphere = Sphere()
//        }
//
//        this.boundingSphere!!.setFromPoints(this.vertices)
//
//    }
//
//    private companion object {
//        val geometryIdCount = AtomicInteger(0) // BufferGeometry uses even numbers as Id
//    }
//
//}
//
//data class MorphTarget(
//    val name: String,
//    val vertices: List<Vector3>
//)
//
//data class MorphColor(
//    val name: String,
//    val colors: List<Color>
//)
//
//data class MorphNormals(
//    val name: String,
//    val normals: List<Vector3>
//)