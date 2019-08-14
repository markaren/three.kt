package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import info.laht.threekt.core.Object3D
import info.laht.threekt.objects.Mesh
import kotlin.jvm.JvmOverloads
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Box3 @JvmOverloads constructor(
    var min: Vector3 = Vector3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    var max: Vector3 = Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY)
) : Cloneable {

    private val intersectsTriangleHelper by lazy { IntersectsTriangleHelper() }

    fun set(min: Vector3, max: Vector3): Box3 {
        this.min.copy(min)
        this.max.copy(max)

        return this
    }

    fun setFromArray(array: FloatArray): Box3 {
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY

        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY

        for (i in 0 until array.size step 3) {

            val x = array[i]
            val y = array[i + 1]
            val z = array[i + 2]

            if (x < minX) minX = x
            if (y < minY) minY = y
            if (z < minZ) minZ = z

            if (x > maxX) maxX = x
            if (y > maxY) maxY = y
            if (z > maxZ) maxZ = z

        }

        this.min.set(minX, minY, minZ)
        this.max.set(maxX, maxY, maxZ)

        return this
    }

    fun setFromPoints(points: List<Vector3>): Box3 {
        this.makeEmpty()

        points.forEach {
            this.expandByPoint(it)
        }

        return this
    }

    fun setFromCenterAndSize(center: Vector3, size: Vector3): Box3 {
        val halfSize = Vector3().copy(size).multiplyScalar(0.5f)

        this.min.copy(center).sub(halfSize)
        this.max.copy(center).add(halfSize)

        return this
    }

    fun makeEmpty(): Box3 {
        this.min.x = Float.POSITIVE_INFINITY
        this.min.y = Float.POSITIVE_INFINITY
        this.min.z = Float.POSITIVE_INFINITY

        this.max.x = Float.NEGATIVE_INFINITY
        this.max.y = Float.NEGATIVE_INFINITY
        this.max.z = Float.NEGATIVE_INFINITY

        return this
    }

    fun isEmpty(): Boolean {
        // this is a more robust check for empty than ( volume <= 0 ) because volume can get positive with two negative axes
        return (this.max.x < this.min.x) || (this.max.y < this.min.y) || (this.max.z < this.min.z)
    }

    @JvmOverloads
    fun getCenter(target: Vector3 = Vector3()): Vector3 {
        return if (this.isEmpty()) {
            target.set(0f, 0f, 0f)
        } else {
            target.addVectors(this.min, this.max).multiplyScalar(0.5f)
        }
    }

    @JvmOverloads
    fun getSize(target: Vector3 = Vector3()): Vector3 {
        return if (this.isEmpty()) {
            target.set(0f, 0f, 0f)
        } else {
            target.subVectors(this.max, this.min)
        }
    }

    fun expandByPoint(point: Vector3): Box3 {
        this.min.min(point)
        this.max.max(point)

        return this
    }

    fun expandByVector(vector: Vector3): Box3 {
        this.min.sub(vector)
        this.max.add(vector)

        return this
    }

    fun expandByScalar(scalar: Float): Box3 {
        this.min.addScalar(-scalar)
        this.max.addScalar(scalar)

        return this
    }

    fun containsPoint(point: Vector3): Boolean {
        return !(point.x < this.min.x || point.x > this.max.x ||
                point.y < this.min.y || point.y > this.max.y ||
                point.z < this.min.z || point.z > this.max.z)
    }

    fun containsBox(box: Box3): Boolean {
        return this.min.x <= box.min.x && box.max.x <= this.max.x &&
                this.min.y <= box.min.y && box.max.y <= this.max.y &&
                this.min.z <= box.min.z && box.max.z <= this.max.z
    }

    @JvmOverloads
    fun getParameter(point: Vector3, target: Vector3 = Vector3()): Vector3 {
        return target.set(
            (point.x - this.min.x) / (this.max.x - this.min.x),
            (point.y - this.min.y) / (this.max.y - this.min.y),
            (point.z - this.min.z) / (this.max.z - this.min.z)
        )
    }

    fun intersectsBox(box: Box3): Boolean {
        // using 6 splitting planes to rule out intersections.
        return !(box.max.x < this.min.x || box.min.x > this.max.x ||
                box.max.y < this.min.y || box.min.y > this.max.y ||
                box.max.z < this.min.z || box.min.z > this.max.z)
    }

    fun intersectsSphere(sphere: Sphere): Boolean {
        val closestPoint = Vector3()
        // Find the point on the AABB closest to the sphere center.
        this.clampPoint(sphere.center, closestPoint)

        // If that point is inside the sphere, the AABB and sphere intersect.
        return closestPoint.distanceToSquared(sphere.center) <= (sphere.radius * sphere.radius)
    }

    fun intersectsPlane(plane: Plane): Boolean {
        // We compute the minimum and maximum dot product values. If those values
        // are on the same side (back or front) of the plane, then there is no intersection.

        var min: Float
        var max: Float

        if (plane.normal.x > 0) {

            min = plane.normal.x * this.min.x
            max = plane.normal.x * this.max.x

        } else {

            min = plane.normal.x * this.max.x
            max = plane.normal.x * this.min.x

        }

        if (plane.normal.y > 0) {

            min += plane.normal.y * this.min.y
            max += plane.normal.y * this.max.y

        } else {

            min += plane.normal.y * this.max.y
            max += plane.normal.y * this.min.y

        }

        if (plane.normal.z > 0) {

            min += plane.normal.z * this.min.z
            max += plane.normal.z * this.max.z

        } else {

            min += plane.normal.z * this.max.z
            max += plane.normal.z * this.min.z

        }

        return -plane.constant in min..max
    }

    fun intersectsTriangle(triangle: Triangle): Boolean {

        with(intersectsTriangleHelper) {

            fun satForAxes(axes: FloatArray): Boolean {

                for (i in 0 until axes.size - 3 step 3) {

                    testAxis.fromArray(axes, i)
                    // project the aabb onto the seperating axis
                    val r = extents.x * abs(testAxis.x) + extents.y * abs(testAxis.y) + extents.z * abs(testAxis.z)
                    // project all 3 vertices of the triangle onto the seperating axis
                    val p0 = v0.dot(testAxis)
                    val p1 = v1.dot(testAxis)
                    val p2 = v2.dot(testAxis)
                    // actual test, basically see if either of the most extreme of the triangle points intersects r
                    if (max(-max(p0, max(p1, p2)), min(p0, min(p1, p2))) > r) {

                        // points of the projected triangle are outside the projected half-length of the aabb
                        // the axis is seperating and we can exit
                        return false

                    }

                }

                return true

            }

            if (isEmpty()) {

                return false

            }

            // compute box center and extents
            getCenter(center)
            extents.subVectors(max, center)

            // translate triangle to aabb origin
            v0.subVectors(triangle.a, center)
            v1.subVectors(triangle.b, center)
            v2.subVectors(triangle.c, center)

            // compute edge vectors for triangle
            f0.subVectors(v1, v0)
            f1.subVectors(v2, v1)
            f2.subVectors(v0, v2)

            // test against axes that are given by cross product combinations of the edges of the triangle and the edges of the aabb
            // make an axis testing of each of the 3 sides of the aabb against each of the 3 sides of the triangle = 9 axis of separation
            // axis_ij = u_i x f_j (u0, u1, u2 = face normals of aabb = x,y,z axes vectors since aabb is axis aligned)
            var axes = floatArrayOf(
                0f, -f0.z, f0.y, 0f, -f1.z, f1.y, 0f, -f2.z, f2.y,
                f0.z, 0f, -f0.x, f1.z, 0f, -f1.x, f2.z, 0f, -f2.x,
                -f0.y, f0.x, 0f, -f1.y, f1.x, 0f, -f2.y, f2.x, 0f
            )
            if (!satForAxes(axes)) {

                return false

            }

            // test 3 face normals from the aabb
            axes = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
            if (!satForAxes(axes)) {

                return false

            }

            // finally testing the face normal of the triangle
            // use already existing triangle edge vectors here
            triangleNormal.crossVectors(f0, f1)
            axes = floatArrayOf(triangleNormal.x, triangleNormal.y, triangleNormal.z)
            return satForAxes(axes)

        }

    }

    @JvmOverloads
    fun clampPoint(point: Vector3, target: Vector3 = Vector3()): Vector3 {
        return target.copy(point).clamp(this.min, this.max)
    }

    fun distanceToPoint(point: Vector3): Float {
        val clampedPoint = Vector3().copy(point).clamp(this.min, this.max)
        return clampedPoint.sub(point).length()
    }

    @JvmOverloads
    fun getBoundingSphere(target: Sphere = Sphere()): Sphere {
        this.getCenter(target.center)

        target.radius = this.getSize(Vector3()).length() * 0.5f

        return target
    }

    fun intersect(box: Box3): Box3 {
        this.min.max(box.min)
        this.max.min(box.max)

        // ensure that if there is no overlap, the result is fully empty, not slightly empty with non-inf/+inf values that will cause subsequence intersects to erroneously return valid values.
        if (this.isEmpty()) {
            this.makeEmpty()
        }

        return this
    }

    fun union(box: Box3): Box3 {
        this.min.min(box.min)
        this.max.max(box.max)

        return this
    }

    fun applyMatrix4(matrix: Matrix4): Box3 {

        // transform of empty box is an empty box.
        if (this.isEmpty()) {
            return this
        }

        // NOTE: I am using a binary pattern to specify all 2^3 combinations below
        points[0].set(this.min.x, this.min.y, this.min.z).applyMatrix4(matrix) // 000
        points[1].set(this.min.x, this.min.y, this.max.z).applyMatrix4(matrix) // 001
        points[2].set(this.min.x, this.max.y, this.min.z).applyMatrix4(matrix) // 010
        points[3].set(this.min.x, this.max.y, this.max.z).applyMatrix4(matrix) // 011
        points[4].set(this.max.x, this.min.y, this.min.z).applyMatrix4(matrix) // 100
        points[5].set(this.max.x, this.min.y, this.max.z).applyMatrix4(matrix) // 101
        points[6].set(this.max.x, this.max.y, this.min.z).applyMatrix4(matrix) // 110
        points[7].set(this.max.x, this.max.y, this.max.z).applyMatrix4(matrix) // 111

        this.setFromPoints(points)

        return this
    }

    fun translate(offset: Vector3): Box3 {
        this.min.add(offset)
        this.max.add(offset)

        return this
    }


    fun setFromObject(`object`: Object3D): Box3 {
        this.makeEmpty()

        return this.expandByObject(`object`)
    }

    fun expandByObject(`object`: Object3D): Box3 {

        val v1 = Vector3()

        `object`.updateMatrixWorld(true)
        `object`.traverse { node ->

            if (node is Mesh) {

                val geometry = node.geometry

                geometry.attributes.position?.also { attribute ->

                    for (i in 0 until attribute.count) {
                        attribute.toVector3(i, v1).applyMatrix4(node.matrixWorld)
                        expandByPoint(v1)
                    }

                }

            }

        }

        return this
    }


    override fun clone(): Box3 {
        return Box3().copy(this)
    }

    fun copy(box: Box3): Box3 {
        this.min.copy(box.min)
        this.max.copy(box.max)

        return this
    }

    companion object {

        private val points by lazy {
            List(8) { Vector3() }
        }

    }

    private inner class IntersectsTriangleHelper {

        // triangle centered vertices
        var v0 = Vector3()
        var v1 = Vector3()
        var v2 = Vector3()

        // triangle edge vectors
        var f0 = Vector3()
        var f1 = Vector3()
        var f2 = Vector3()

        var testAxis = Vector3()

        var center = Vector3()
        var extents = Vector3()

        var triangleNormal = Vector3()

    }

}
