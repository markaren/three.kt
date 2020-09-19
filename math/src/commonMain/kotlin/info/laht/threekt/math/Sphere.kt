package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.jvm.JvmOverloads
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

data class Sphere @JvmOverloads constructor(
    val center: Vector3 = Vector3(),
    var radius: Float = 0f
) : Cloneable {

    fun set(center: Vector3, radius: Float): Sphere {
        this.center.copy(center)
        this.radius = radius
        return this
    }

    @JvmOverloads
    fun setFromPoints(points: List<Vector3>, optionalCenter: Vector3? = null): Sphere {

        val box = Box3()

        val center = this.center

        if (optionalCenter != null) {
            center.copy(optionalCenter)
        } else {
            box.setFromPoints(points).getCenter(center)
        }

        var maxRadiusSq = 0f

        points.forEach { point ->

            maxRadiusSq = max(maxRadiusSq, center.distanceToSquared(point))

        }

        this.radius = sqrt(maxRadiusSq)

        return this

    }

    fun empty(): Boolean {
        return (this.radius <= 0)
    }

    fun containsPoint(point: Vector3): Boolean {
        return (point.distanceToSquared(this.center) <= (this.radius * this.radius))
    }

    fun distanceToPoint(point: Vector3): Float {
        return (point.distanceTo(this.center) - this.radius)
    }

    fun intersectsSphere(sphere: Sphere): Boolean {
        val radiusSum = this.radius + sphere.radius

        return sphere.center.distanceToSquared(this.center) <= (radiusSum * radiusSum)
    }

    fun intersectsBox(box: Box3): Boolean {
        return box.intersectsSphere(this)
    }

    fun intersectsPlane(plane: Plane): Boolean {
        return abs(plane.distanceToPoint(this.center)) <= this.radius
    }

    fun clampPoint(point: Vector3, target: Vector3 = Vector3()): Vector3 {
        val deltaLengthSq = this.center.distanceToSquared(point)

        target.copy(point)

        if (deltaLengthSq > (this.radius * this.radius)) {

            target.sub(this.center).normalize()
            target.multiplyScalar(this.radius).add(this.center)

        }

        return target

    }

    @JvmOverloads
    fun getBoundingBox(target: Box3 = Box3()): Box3 {
        target.set(this.center, this.center)
        target.expandByScalar(this.radius)

        return target
    }

    fun applyMatrix4(matrix: Matrix4): Sphere {
        this.center.applyMatrix4(matrix)
        this.radius = this.radius * matrix.getMaxScaleOnAxis()

        return this
    }

    fun translate(offset: Vector3): Sphere {
        this.center.add(offset)

        return this
    }

    override fun clone(): Sphere {
        return Sphere().copy(this)
    }

    fun copy(sphere: Sphere): Sphere {
        this.center.copy(sphere.center)
        this.radius = sphere.radius
        return this
    }

}
