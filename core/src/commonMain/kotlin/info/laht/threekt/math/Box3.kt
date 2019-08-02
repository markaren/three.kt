package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.jvm.JvmOverloads

class Box3 @JvmOverloads constructor(
    var min: Vector3 = Vector3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    var max: Vector3 = Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY)
): Cloneable {

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
        val halfSize = Vector3().copy( size ).multiplyScalar( 0.5.toFloat() )

        this.min.copy( center ).sub( halfSize )
        this.max.copy( center ).add( halfSize )

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

    fun getCenter(target: Vector3): Vector3 {
        return if (this.isEmpty()) {
            target.set(0, 0, 0)
        } else {
            target.addVectors(this.min, this.max).multiplyScalar(0.5.toFloat())
        }
    }

    fun getSize(target: Vector3): Vector3 {
        return if (this.isEmpty()) {
            target.set(0, 0, 0)
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

    fun getParameter(point: Vector3, target: Vector3): Vector3 {
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
        this.clampPoint( sphere.center, closestPoint )

        // If that point is inside the sphere, the AABB and sphere intersect.
        return closestPoint.distanceToSquared( sphere.center ) <= ( sphere.radius * sphere.radius )
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

    fun clampPoint(point: Vector3, target: Vector3): Vector3 {
        return target.copy(point).clamp(this.min, this.max)
    }

    fun distanceToPoint(point: Vector3): Float {
        val clampedPoint = Vector3().copy(point).clamp(this.min, this.max)
        return clampedPoint.sub(point).length()
    }

    fun getBoundingSphere(target: Sphere): Sphere {
        this.getCenter(target.center)

        target.radius = this.getSize(Vector3()).length() * 0.5.toFloat()

        return target
    }

    fun intersect(box: Box3): Box3 {
        this.min.max( box.min )
        this.max.min( box.max )

        // ensure that if there is no overlap, the result is fully empty, not slightly empty with non-inf/+inf values that will cause subsequence intersects to erroneously return valid values.
        if ( this.isEmpty() ) {
            this.makeEmpty()
        }

        return this
    }

    fun union(box: Box3): Box3 {
        this.min.min( box.min )
        this.max.max( box.max )

        return this
    }

    fun applyMatrix4(matrix: Matrix4): Box3 {

        // transform of empty box is an empty box.
        if (this.isEmpty()) {
            return this
        }
        // NOTE: I am using a binary pattern to specify all 2^3 combinations below

        synchronized(points)  {
            points[0].set(this.min.x, this.min.y, this.min.z).applyMatrix4(matrix) // 000
            points[1].set(this.min.x, this.min.y, this.max.z).applyMatrix4(matrix) // 001
            points[2].set(this.min.x, this.max.y, this.min.z).applyMatrix4(matrix) // 010
            points[3].set(this.min.x, this.max.y, this.max.z).applyMatrix4(matrix) // 011
            points[4].set(this.max.x, this.min.y, this.min.z).applyMatrix4(matrix) // 100
            points[5].set(this.max.x, this.min.y, this.max.z).applyMatrix4(matrix) // 101
            points[6].set(this.max.x, this.max.y, this.min.z).applyMatrix4(matrix) // 110
            points[7].set(this.max.x, this.max.y, this.max.z).applyMatrix4(matrix) // 111

            this.setFromPoints(points)
        }

        return this
    }

    fun translate(offset: Vector3): Box3 {
        this.min.add(offset)
        this.max.add(offset)

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Box3) return false

        if (min != other.min) return false
        if (max != other.max) return false

        return true
    }

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        return result
    }

    override fun toString(): String {
        return "Box3(min=$min, max=$max)"
    }

    companion object {

        private val points by lazy {
            List(8) { Vector3() }
        }

    }

}
