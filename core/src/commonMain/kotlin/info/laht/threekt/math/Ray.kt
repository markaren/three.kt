package info.laht.threekt.math

import kotlin.jvm.JvmOverloads

data class Ray @JvmOverloads constructor(
    var origin: Vector3 = Vector3(),
    val direction: Vector3 = Vector3()
) {

    fun set(origin: Vector3, direction: Vector3): Ray {
        TODO()
    }

    fun clone(): Ray {
        TODO()
    }

    fun copy(ray: Ray): Ray {
        TODO()
    }

    fun at(t: Float, target: Vector3): Vector3 {
        TODO()
    }

    fun lookAt(v: Vector3): Vector3 {
        TODO()
    }

    fun recast(t: Float): Ray {
        TODO()
    }

    fun closestPointToPoint(point: Vector3, target: Vector3): Vector3 {
        TODO()
    }

    fun distanceToPoint(point: Vector3): Float {
        TODO()
    }

    fun distanceSqToPoint(point: Vector3): Float {
        TODO()
    }

    fun distanceSqToSegment(
        v0: Vector3,
        v1: Vector3,
        optionalPointOnRay: Vector3? = null,
        optionalPointOnSegment: Vector3? = null
    ): Float {
        TODO()
    }

    fun intersectSphere(sphere: Sphere, target: Vector3): Vector3 {
        TODO()
    }

    fun intersectsSphere(sphere: Sphere): Boolean {
        TODO()
    }

    fun distanceToPlane(plane: Plane): Float {
        TODO()
    }

    fun intersectPlane(plane: Plane, target: Vector3): Vector3 {
        TODO()
    }

    fun intersectsPlane(plane: Plane): Boolean {
        TODO()
    }

    fun intersectBox(box: Box3, target: Vector3): Vector3 {
        TODO()
    }

    fun intersectsBox(box: Box3): Boolean {
        TODO()
    }

    fun intersectTriangle(
        a: Vector3,
        b: Vector3,
        c: Vector3,
        backfaceCulling: Boolean,
        target: Vector3
    ): Vector3 {
        TODO()
    }

    fun applyMatrix4(matrix4: Matrix4): Ray {
        TODO()
    }

}
