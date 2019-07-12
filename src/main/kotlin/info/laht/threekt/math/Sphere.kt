package info.laht.threekt.math

class Sphere(
    var center: Vector3 = Vector3(),
    var radius: Double = 0.0
) {

    fun set(center: Vector3, radius: Double): Sphere {
        TODO()
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

    fun distanceToPoint(point: Vector3): Double {
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

    fun clone(): Sphere {
        TODO()
    }

    fun copy(sphere: Sphere): Sphere {
        TODO()
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