package info.laht.threekt.math


class Plane @JvmOverloads constructor(
    val normal: Vector3 = Vector3(0.0, 0.0, 1.0),
    constant: Double = 0.0
) {

    var constant = constant
        private set

    fun set(normal: Vector3, constant: Double): Plane {
        this.normal.copy(normal)
        this.constant = constant

        return this
    }

    fun setComponents(x: Double, y: Double, z: Double, w: Double): Plane {
        TODO()
    }

    fun setFromNormalAndCoplanarPoint(normal: Vector3, point: Vector3): Plane {
        TODO()
    }

    fun setFromCoplanarPoints(a: Vector3, b: Vector3, c: Vector3): Plane {
        TODO()
    }

    fun clone(): Plane {
        return Plane().copy(this)
    }

    fun copy(plane: Plane): Plane {
        this.normal.copy(plane.normal)
        this.constant = plane.constant

        return this
    }

    fun normalize(): Plane {
        TODO()
    }

    fun negate(): Plane {
        TODO()
    }

    fun distanceToPoint(point: Vector3): Double {
        TODO()
    }

    fun distanceToSphere(sphere: Sphere): Double {
        TODO()
    }

    fun projectPoint(point: Vector3, target: Vector3): Vector3 {
        TODO()
    }

    fun orthoPoint(point: Vector3, target: Vector3): Vector3 {
        TODO()
    }

    fun intersectLine(line: Line3, target: Vector3): Vector3 {
        TODO()
    }

    fun intersectsLine(line: Line3): Boolean {
        TODO()
    }

    fun intersectsBox(box: Box3): Boolean {
        TODO()
    }

    fun intersectsSphere(sphere: Sphere): Boolean {
        TODO()
    }

    fun coplanarPoint(target: Vector3): Vector3 {
        TODO()
    }

    fun applyMatrix4(matrix: Matrix4, optionalNormalMatrix: Matrix3? = null): Plane {
        TODO()
    }

    fun translate(offset: Vector3): Plane {
        TODO()
    }

    fun equals(plane: Plane): Boolean {
        TODO()
    }

}
