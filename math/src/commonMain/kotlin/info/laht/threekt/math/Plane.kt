package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.jvm.JvmOverloads

data class Plane @JvmOverloads constructor(
    val normal: Vector3 = Vector3(0f, 0f, 1f),
    var constant: Float = 0f
) : Cloneable {

    fun set(normal: Vector3, constant: Float): Plane {
        this.normal.copy(normal)
        this.constant = constant

        return this
    }

    fun setComponents(x: Float, y: Float, z: Float, w: Float): Plane {
        this.normal.set(x, y, z)
        this.constant = w

        return this
    }

    fun setFromNormalAndCoplanarPoint(normal: Vector3, point: Vector3): Plane {
        this.normal.copy(normal)
        this.constant = -point.dot(this.normal)

        return this
    }

    fun setFromCoplanarPoints(a: Vector3, b: Vector3, c: Vector3): Plane {

        val v1 = Vector3()
        val v2 = Vector3()

        val normal = v1.subVectors(c, b).cross(v2.subVectors(a, b)).normalize()

        // Q: should an error be thrown if normal is zero (e.g. degenerate plane)?

        this.setFromNormalAndCoplanarPoint(normal, a)

        return this
    }

    override fun clone(): Plane {
        return Plane().copy(this)
    }

    fun copy(plane: Plane): Plane {
        this.normal.copy(plane.normal)
        this.constant = plane.constant

        return this
    }

    fun normalize(): Plane {
        val inverseNormalLength = 1f / this.normal.length()
        this.normal.multiplyScalar(inverseNormalLength)
        this.constant *= inverseNormalLength

        return this
    }

    fun negate(): Plane {
        this.constant *= -1
        this.normal.negate()

        return this
    }

    fun distanceToPoint(point: Vector3): Float {
        return this.normal.dot(point) + this.constant
    }

    fun distanceToSphere(sphere: Sphere): Float {
        return this.distanceToPoint(sphere.center) - sphere.radius
    }

    @JvmOverloads
    fun projectPoint(point: Vector3, target: Vector3 = Vector3()): Vector3 {
        return target.copy(this.normal).multiplyScalar(-this.distanceToPoint(point)).add(point)
    }

    @JvmOverloads
    fun intersectLine(line: Line3, target: Vector3 = Vector3()): Vector3? {
        val v1 = Vector3()

        val direction = line.delta(v1)

        val denominator = this.normal.dot(direction)

        if (denominator == 0f) {

            // line is coplanar, return origin
            if (this.distanceToPoint(line.start) == 0f) {

                return target.copy(line.start)

            }

            // Unsure if this is the correct method to handle this case.
            return null

        }

        val t = -(line.start.dot(this.normal) + this.constant) / denominator

        if (t < 0 || t > 1) {

            return null

        }

        return target.copy(direction).multiplyScalar(t).add(line.start)

    }

    fun intersectsLine(line: Line3): Boolean {
        // Note: this tests if a line intersects the plane, not whether it (or its end-points) are coplanar with it.

        val startSign = this.distanceToPoint(line.start)
        val endSign = this.distanceToPoint(line.end)

        return (startSign < 0 && endSign > 0) || (endSign < 0 && startSign > 0)
    }

    fun intersectsBox(box: Box3): Boolean {
        return box.intersectsPlane(this)
    }

    fun intersectsSphere(sphere: Sphere): Boolean {
        return sphere.intersectsPlane(this)
    }

    @JvmOverloads
    fun coplanarPoint(target: Vector3 = Vector3()): Vector3 {
        return target.copy(this.normal).multiplyScalar(-this.constant)
    }

    @JvmOverloads
    fun applyMatrix4(matrix: Matrix4, optionalNormalMatrix: Matrix3? = null): Plane {

        val v1 = Vector3()
        val m1 = Matrix3()

        val normalMatrix = optionalNormalMatrix ?: m1.getNormalMatrix(matrix)

        val referencePoint = this.coplanarPoint(v1).applyMatrix4(matrix)

        val normal = this.normal.applyMatrix3(normalMatrix).normalize()

        this.constant = -referencePoint.dot(normal)

        return this
    }

    fun translate(offset: Vector3): Plane {
        this.constant -= offset.dot(this.normal)

        return this
    }

}
