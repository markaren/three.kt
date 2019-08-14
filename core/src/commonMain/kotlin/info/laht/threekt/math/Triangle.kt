package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.jvm.JvmOverloads
import kotlin.math.sqrt

data class Triangle @JvmOverloads constructor(
    var a: Vector3 = Vector3(),
    var b: Vector3 = Vector3(),
    var c: Vector3 = Vector3()
) : Cloneable {

    private val closestPointToPointHelper by lazy { ClosestPointToPointHelper() }

    override fun clone() = copy()

    fun set(a: Vector3, b: Vector3, c: Vector3): Triangle {

        this.a.copy(a)
        this.b.copy(b)
        this.c.copy(c)

        return this

    }

    fun setFromPointsAndIndices(points: List<Vector3>, i0: Int, i1: Int, i2: Int): Triangle {

        this.a.copy(points[i0])
        this.b.copy(points[i1])
        this.c.copy(points[i2])

        return this

    }

    fun copy(triangle: Triangle): Triangle {

        this.a.copy(triangle.a)
        this.b.copy(triangle.b)
        this.c.copy(triangle.c)

        return this

    }

    fun getArea(): Float {

        v0.subVectors(this.c, this.b)
        v1.subVectors(this.a, this.b)

        return v0.cross(v1).length() * 0.5f

    }

    @JvmOverloads
    fun getMidpoint(target: Vector3 = Vector3()): Vector3 {
        return target.addVectors(this.a, this.b).add(this.c).multiplyScalar(1f / 3)
    }

    @JvmOverloads
    fun getNormal(target: Vector3 = Vector3()): Vector3 {
        return getNormal(this.a, this.b, this.c, target)
    }

    @JvmOverloads
    fun getPlane(target: Plane): Plane {
        return target.setFromCoplanarPoints(this.a, this.b, this.c)
    }

    @JvmOverloads
    fun getBarycoord(point: Vector3, target: Vector3): Vector3 {
        return getBarycoord(point, this.a, this.b, this.c, target)
    }

    @JvmOverloads
    fun getUV(point: Vector3, uv1: Vector2, uv2: Vector2, uv3: Vector2, target: Vector2 = Vector2()): Vector2 {

        return getUV(point, this.a, this.b, this.c, uv1, uv2, uv3, target)

    }

    fun containsPoint(point: Vector3): Boolean {

        return containsPoint(point, this.a, this.b, this.c)

    }

    fun isFrontFacing(direction: Vector3): Boolean {

        return isFrontFacing(this.a, this.b, this.c, direction)

    }

    fun intersectsBox(box: Box3): Boolean {

        return box.intersectsTriangle(this)

    }

    @JvmOverloads
    fun closestPointToPoint(p: Vector3, target: Vector3 = Vector3()): Vector3 {

        with(closestPointToPointHelper) {

            val v: Float
            val w: Float

            // algorithm thanks to Real-Time Collision Detection by Christer Ericson,
            // published by Morgan Kaufmann Publishers, (c) 2005 Elsevier Inc.,
            // under the accompanying license; see chapter 5.1.5 for detailed explanation.
            // basically, we're distinguishing which of the voronoi regions of the triangle
            // the point lies in with the minimum amount of redundant computation.

            vab.subVectors(b, a)
            vac.subVectors(c, a)
            vap.subVectors(p, a)
            val d1 = vab.dot(vap)
            val d2 = vac.dot(vap)
            if (d1 <= 0 && d2 <= 0) {

                // vertex region of A; barycentric coords (1, 0, 0)
                return target.copy(a)

            }

            vbp.subVectors(p, b)
            val d3 = vab.dot(vbp)
            val d4 = vac.dot(vbp)
            if (d3 >= 0 && d4 <= d3) {

                // vertex region of B; barycentric coords (0, 1, 0)
                return target.copy(b)

            }

            val vc = d1 * d4 - d3 * d2
            if (vc <= 0 && d1 >= 0 && d3 <= 0) {

                v = d1 / (d1 - d3)
                // edge region of AB; barycentric coords (1-v, v, 0)
                return target.copy(a).addScaledVector(vab, v)

            }

            vcp.subVectors(p, c)
            val d5 = vab.dot(vcp)
            val d6 = vac.dot(vcp)
            if (d6 >= 0 && d5 <= d6) {

                // vertex region of C; barycentric coords (0, 0, 1)
                return target.copy(c)

            }

            val vb = d5 * d2 - d1 * d6
            if (vb <= 0 && d2 >= 0 && d6 <= 0) {

                w = d2 / (d2 - d6)
                // edge region of AC; barycentric coords (1-w, 0, w)
                return target.copy(a).addScaledVector(vac, w)

            }

            val va = d3 * d6 - d5 * d4
            if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {

                vbc.subVectors(c, b)
                w = (d4 - d3) / ((d4 - d3) + (d5 - d6))
                // edge region of BC; barycentric coords (0, 1-w, w)
                return target.copy(b).addScaledVector(vbc, w) // edge region of BC

            }

            // face region
            val denom = 1f / (va + vb + vc)
            // u = va * denom
            v = vb * denom
            w = vc * denom
            return target.copy(a).addScaledVector(vab, v).addScaledVector(vac, w)

        }

    }

    companion object {

        private val v0 by lazy { Vector3(); }
        private val v1 by lazy { Vector3(); }
        private val v2 by lazy { Vector3(); }
        private val barycoord by lazy { Vector3() }

        @JvmOverloads
        fun getNormal(a: Vector3, b: Vector3, c: Vector3, target: Vector3 = Vector3()): Vector3 {

            target.subVectors(c, b)
            v0.subVectors(a, b)
            target.cross(v0)

            val targetLengthSq = target.lengthSq()
            if (targetLengthSq > 0) {

                return target.multiplyScalar(1f / sqrt(targetLengthSq))

            }

            return target.set(0, 0, 0)

        }

        fun getBarycoord(point: Vector3, a: Vector3, b: Vector3, c: Vector3, target: Vector3 = Vector3()): Vector3 {

            v0.subVectors(c, a)
            v1.subVectors(b, a)
            v2.subVectors(point, a)

            val dot00 = v0.dot(v0)
            val dot01 = v0.dot(v1)
            val dot02 = v0.dot(v2)
            val dot11 = v1.dot(v1)
            val dot12 = v1.dot(v2)

            val denom = (dot00 * dot11 - dot01 * dot01)

            // collinear or singular triangle
            if (denom == 0f) {

                // arbitrary location outside of triangle?
                // not sure if this is the best idea, maybe should be returning undefined
                return target.set(-2, -1, -1)

            }

            val invDenom = 1f / denom
            val u = (dot11 * dot02 - dot01 * dot12) * invDenom
            val v = (dot00 * dot12 - dot01 * dot02) * invDenom

            // barycentric coordinates must always sum to 1
            return target.set(1 - u - v, v, u)

        }

        fun containsPoint(point: Vector3, a: Vector3, b: Vector3, c: Vector3): Boolean {

            getBarycoord(point, a, b, c, v1)

            return (v1.x >= 0) && (v1.y >= 0) && ((v1.x + v1.y) <= 1)

        }

        fun getUV(
            point: Vector3,
            p1: Vector3,
            p2: Vector3,
            p3: Vector3,
            uv1: Vector2,
            uv2: Vector2,
            uv3: Vector2,
            target: Vector2
        ): Vector2 {

            this.getBarycoord(point, p1, p2, p3, barycoord)

            target.set(0f, 0f)
            target.addScaledVector(uv1, barycoord.x)
            target.addScaledVector(uv2, barycoord.y)
            target.addScaledVector(uv3, barycoord.z)

            return target

        }

        fun isFrontFacing(a: Vector3, b: Vector3, c: Vector3, direction: Vector3): Boolean {
            v0.subVectors(c, b)
            v1.subVectors(a, b)

            // strictly front facing
            return v0.cross(v1).dot(direction) < 0
        }

    }

    private class ClosestPointToPointHelper {
        val vab = Vector3()
        val vac = Vector3()
        val vbc = Vector3()
        val vap = Vector3()
        val vbp = Vector3()
        val vcp = Vector3()
    }

}
