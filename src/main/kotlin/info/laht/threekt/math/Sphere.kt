package info.laht.threekt.math

import info.laht.threekt.core.Cloneable

class Sphere(
    val center: Vector3 = Vector3(),
    var radius: Float = 0f
): Cloneable {

    fun set(center: Vector3, radius: Float): Sphere {
        this.center.copy(center)
        this.radius = radius
        return this
    }

    @JvmOverloads
    fun setFromPoints(points: List<Vector3>, optionalCenter: Vector3 = Vector3()): Sphere {
        TODO()
    }

    fun empty(): Boolean {
        TODO()
    }

    fun containsPoint(point: Vector3): Boolean {
        TODO()
    }

    fun distanceToPoint(point: Vector3): Float {
        TODO()
    }

    fun intersectsSphere(sphere: Sphere): Boolean {
        TODO()
    }

    fun intersectsBox(box: Box3): Boolean {
        TODO()
    }

    fun intersectsPlane(plane: Plane): Boolean {
        TODO()
    }

    fun clampPoint(point: Vector3, target: Vector3): Vector3 {
        TODO()
    }

    fun getBoundingBox(target: Box3): Box3 {
        TODO()
    }

    fun applyMatrix4(matrix: Matrix4): Sphere {
        TODO()
    }

    fun translate(offset: Vector3): Sphere {
        TODO()
    }

    override fun clone(): Sphere {
        return Sphere().copy(this)
    }

    fun copy(sphere: Sphere): Sphere {
        this.center.copy(sphere.center)
        this.radius = sphere.radius
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sphere

        if (center != other.center) return false
        if (radius != other.radius) return false

        return true
    }

    override fun hashCode(): Int {
        var result = center.hashCode()
        result = 31 * result + radius.hashCode()
        return result
    }

    override fun toString(): String {
        return "Sphere(center=$center, radius=$radius)"
    }

}