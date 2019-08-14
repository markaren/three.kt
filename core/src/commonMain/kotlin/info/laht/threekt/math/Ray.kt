package info.laht.threekt.math

import kotlin.jvm.JvmOverloads
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class Ray @JvmOverloads constructor(
        var origin: Vector3 = Vector3(),
        val direction: Vector3 = Vector3()
) {

    private val v = Vector3()

    private var segCenter = Vector3()
    private var segDir = Vector3()
    private var diff = Vector3()

    private var edge1 = Vector3()
    private var edge2 = Vector3()
    private var normal = Vector3()


    fun set(origin: Vector3, direction: Vector3): Ray {

        this.origin.copy(origin)
        this.direction.copy(direction)

        return this

    }

    fun clone(): Ray {

        return Ray().copy(this)

    }

    fun copy(ray: Ray): Ray {

        this.origin.copy(ray.origin)
        this.direction.copy(ray.direction)

        return this

    }

    @JvmOverloads
    fun at(t: Float, target: Vector3 = Vector3()): Vector3 {

        return target.copy(this.direction).multiplyScalar(t).add(this.origin)

    }

    fun lookAt(v: Vector3): Ray {

        this.direction.copy(v).sub(this.origin).normalize()

        return this
    }

    fun recast(t: Float): Ray {

        this.origin.copy(this.at(t, v))

        return this

    }

    @JvmOverloads
    fun closestPointToPoint(point: Vector3, target: Vector3 = Vector3()): Vector3 {

        target.subVectors(point, this.origin)

        val directionDistance = target.dot(this.direction)

        if (directionDistance < 0) {

            return target.copy(this.origin)

        }

        return target.copy(this.direction).multiplyScalar(directionDistance).add(this.origin)

    }

    fun distanceToPoint(point: Vector3): Float {

        return sqrt(this.distanceSqToPoint(point))

    }

    fun distanceSqToPoint(point: Vector3): Float {

        val directionDistance = v.subVectors(point, this.origin).dot(this.direction)

        // point behind the ray

        if (directionDistance < 0) {

            return this.origin.distanceToSquared(point)

        }

        v.copy(this.direction).multiplyScalar(directionDistance).add(this.origin)

        return v.distanceToSquared(point)

    }

    fun distanceSqToSegment(
            v0: Vector3,
            v1: Vector3,
            optionalPointOnRay: Vector3? = null,
            optionalPointOnSegment: Vector3? = null
    ): Float {

        // from http://www.geometrictools.com/GTEngine/Include/Mathematics/GteDistRaySegment.h
        // It returns the min distance between the ray and the segment
        // defined by v0 and v1
        // It can also set two optional targets :
        // - The closest point on the ray
        // - The closest point on the segment

        segCenter.copy(v0).add(v1).multiplyScalar(0.5f)
        segDir.copy(v1).sub(v0).normalize()
        diff.copy(this.origin).sub(segCenter)

        val segExtent = v0.distanceTo(v1) * 0.5f
        val a01 = -this.direction.dot(segDir)
        val b0 = diff.dot(this.direction)
        val b1 = -diff.dot(segDir)
        val c = diff.lengthSq()
        val det = abs(1 - a01 * a01)

        var s0: Float
        var s1: Float
        val sqrDist: Float

        if (det > 0) {

            // The ray and segment are not parallel.

            s0 = a01 * b1 - b0
            s1 = a01 * b0 - b1
            val extDet = segExtent * det

            if (s0 >= 0) {

                if (s1 >= -extDet) {

                    if (s1 <= extDet) {

                        // region 0
                        // Minimum at interior points of ray and segment.

                        val invDet = 1f / det
                        s0 *= invDet
                        s1 *= invDet
                        sqrDist = s0 * (s0 + a01 * s1 + 2 * b0) + s1 * (a01 * s0 + s1 + 2 * b1) + c

                    } else {

                        // region 1

                        s1 = segExtent
                        s0 = max(0f, -(a01 * s1 + b0))
                        sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c

                    }

                } else {

                    // region 5

                    s1 = -segExtent
                    s0 = max(0f, -(a01 * s1 + b0))
                    sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c

                }

            } else {

                if (s1 <= -extDet) {

                    // region 4

                    s0 = max(0f, -(-a01 * segExtent + b0))
                    s1 = if (s0 > 0) -segExtent else min(max(-segExtent, -b1), segExtent)
                    sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c

                } else if (s1 <= extDet) {

                    // region 3

                    s0 = 0f
                    s1 = min(max(-segExtent, -b1), segExtent)
                    sqrDist = s1 * (s1 + 2 * b1) + c

                } else {

                    // region 2

                    s0 = max(0f, -(a01 * segExtent + b0))
                    s1 = if (s0 > 0) segExtent else min(max(-segExtent, -b1), segExtent)
                    sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c

                }

            }

        } else {

            // Ray and segment are parallel.

            s1 = if (a01 > 0) -segExtent else segExtent
            s0 = max(0f, -(a01 * s1 + b0))
            sqrDist = -s0 * s0 + s1 * (s1 + 2 * b1) + c

        }

        optionalPointOnRay?.copy(this.direction)?.multiplyScalar(s0)?.add(this.origin)

        optionalPointOnSegment?.copy(segDir)?.multiplyScalar(s1)?.add(segCenter)

        return sqrDist

    }

    fun intersectSphere(sphere: Sphere, target: Vector3): Vector3? {

        v.subVectors(sphere.center, this.origin)
        val tca = v.dot(this.direction)
        val d2 = v.dot(v) - tca * tca
        val radius2 = sphere.radius * sphere.radius

        if (d2 > radius2) return null

        val thc = sqrt(radius2 - d2)

        // t0 = first intersect point - entrance on front of sphere
        val t0 = tca - thc

        // t1 = second intersect point - exit point on back of sphere
        val t1 = tca + thc

        // test to see if both t0 and t1 are behind the ray - if so, return null
        if (t0 < 0 && t1 < 0) return null

        // test to see if t0 is behind the ray:
        // if it is, the ray is inside the sphere, so return the second exit point scaled by t1,
        // in order to always return an intersect point that is in front of the ray.
        if (t0 < 0) return this.at(t1, target)

        // else t0 is in front of the ray, so return the first collision point scaled by t0
        return this.at(t0, target)
    }

    fun intersectsSphere(sphere: Sphere): Boolean {

        return this.distanceSqToPoint(sphere.center) <= (sphere.radius * sphere.radius)

    }

    fun distanceToPlane(plane: Plane): Float? {

        val denominator = plane.normal.dot(this.direction)

        if (denominator == 0f) {

            // line is coplanar, return origin
            if (plane.distanceToPoint(this.origin) == 0f) {

                return 0f

            }

            // Null is preferable to undefined since undefined means.... it is undefined

            return null

        }

        val t = -(this.origin.dot(plane.normal) + plane.constant) / denominator

        // Return if the ray never intersects the plane

        return if (t >= 0) t else null

    }

    @JvmOverloads
    fun intersectPlane(plane: Plane, target: Vector3 = Vector3()): Vector3? {

        val t = this.distanceToPlane(plane) ?: return null

        return this.at(t, target)

    }

    fun intersectsPlane(plane: Plane): Boolean {

        // check if the ray lies on the plane first

        val distToPoint = plane.distanceToPoint(this.origin)

        if (distToPoint == 0f) {

            return true

        }

        val denominator = plane.normal.dot(this.direction)

        if (denominator * distToPoint < 0) {

            return true

        }

        // ray origin is behind the plane (and is pointing behind it)

        return false

    }

    fun intersectBox(box: Box3, target: Vector3): Vector3? {

        var tmin: Float
        var tmax: Float
        val tymin: Float
        val tymax: Float
        val tzmin: Float
        val tzmax: Float

        val invdirx = 1f / this.direction.x
        val invdiry = 1f / this.direction.y
        val invdirz = 1f / this.direction.z

        if (invdirx >= 0) {

            tmin = (box.min.x - origin.x) * invdirx
            tmax = (box.max.x - origin.x) * invdirx

        } else {

            tmin = (box.max.x - origin.x) * invdirx
            tmax = (box.min.x - origin.x) * invdirx

        }

        if (invdiry >= 0) {

            tymin = (box.min.y - origin.y) * invdiry
            tymax = (box.max.y - origin.y) * invdiry

        } else {

            tymin = (box.max.y - origin.y) * invdiry
            tymax = (box.min.y - origin.y) * invdiry

        }

        if ((tmin > tymax) || (tymin > tmax)) return null

        // These lines also handle the case where tmin or tmax is NaN
        // (result of 0 * Infinity). x !== x returns true if x is NaN

        if (tymin > tmin || tmin != tmin) tmin = tymin

        if (tymax < tmax || tmax != tmax) tmax = tymax

        if (invdirz >= 0) {

            tzmin = (box.min.z - origin.z) * invdirz
            tzmax = (box.max.z - origin.z) * invdirz

        } else {

            tzmin = (box.max.z - origin.z) * invdirz
            tzmax = (box.min.z - origin.z) * invdirz

        }

        if ((tmin > tzmax) || (tzmin > tmax)) return null

        if (tzmin > tmin || tmin != tmin) tmin = tzmin

        if (tzmax < tmax || tmax != tmax) tmax = tzmax

        //return point closest to the ray (positive side)

        if (tmax < 0) return null

        return this.at(if (tmin >= 0) tmin else tmax, target)

    }

    fun intersectsBox(box: Box3): Boolean {

        return this.intersectBox(box, v) !== null

    }

    fun intersectTriangle(
            a: Vector3,
            b: Vector3,
            c: Vector3,
            backfaceCulling: Boolean,
            target: Vector3
    ): Vector3? {

        // from http://www.geometrictools.com/GTEngine/Include/Mathematics/GteIntrRay3Triangle3.h

        edge1.subVectors(b, a)
        edge2.subVectors(c, a)
        normal.crossVectors(edge1, edge2)

        // Solve Q + t*D = b1*E1 + b2*E2 (Q = kDiff, D = ray direction,
        // E1 = kEdge1, E2 = kEdge2, N = Cross(E1,E2)) by
        //   |Dot(D,N)|*b1 = sign(Dot(D,N))*Dot(D,Cross(Q,E2))
        //   |Dot(D,N)|*b2 = sign(Dot(D,N))*Dot(D,Cross(E1,Q))
        //   |Dot(D,N)|*t = -sign(Dot(D,N))*Dot(Q,N)
        var DdN = this.direction.dot(normal)
        val sign: Int

        if (DdN > 0) {

            if (backfaceCulling) return null
            sign = 1

        } else if (DdN < 0) {

            sign = -1
            DdN = -DdN

        } else {

            return null

        }

        diff.subVectors(this.origin, a)
        val DdQxE2 = sign * this.direction.dot(edge2.crossVectors(diff, edge2))

        // b1 < 0, no intersection
        if (DdQxE2 < 0) {

            return null

        }

        val DdE1xQ = sign * this.direction.dot(edge1.cross(diff))

        // b2 < 0, no intersection
        if (DdE1xQ < 0) {

            return null

        }

        // b1+b2 > 1, no intersection
        if (DdQxE2 + DdE1xQ > DdN) {

            return null

        }

        // Line intersects triangle, check if ray does.
        val QdN = -sign * diff.dot(normal)

        // t < 0, no intersection
        if (QdN < 0) {

            return null

        }

        // Ray intersects triangle.
        return this.at(QdN / DdN, target)

    }

    fun applyMatrix4(matrix4: Matrix4): Ray {

        this.origin.applyMatrix4(matrix4)
        this.direction.transformDirection(matrix4)

        return this

    }

}
