package info.laht.threekt.extras.core

import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector3
import info.laht.threekt.math.clamp
import kotlin.math.abs
import kotlin.math.acos

abstract class Curve3 {

    var needsUpdate = false

    internal val type = javaClass.simpleName

    private val arcLengthDivisions = 200
    private var cacheArcLengths: FloatArray? = null


    abstract fun getPoint(t: Float, optionalTarget: Vector3 = Vector3()): Vector3

    fun getPointAt(u: Float, optionalTarget: Vector3 = Vector3()): Vector3 {
        val t = getUtoTmapping(u)
        return getPoint(t, optionalTarget)
    }

    fun getPoints(divisions: Int = 5): List<Vector3> {

        return (0..divisions).map {
            getPoint(it.toFloat())
        }

    }

    fun getSpacedPoints(divisions: Int = 5): List<Vector3> {

        return (0..divisions).map {
            getPointAt(it.toFloat())
        }

    }

    fun getLength(): Float {
        val lengths = getLengths()
        return lengths[lengths.size - 1]
    }

    fun getLengths(divisions: Int = arcLengthDivisions): FloatArray {

        if (this.cacheArcLengths != null &&
            (this.cacheArcLengths?.size == divisions + 1) &&
            !this.needsUpdate
        ) {

            return this.cacheArcLengths!!

        }

        this.needsUpdate = false

        val cache = FloatArray(divisions + 1)
        var last = this.getPoint(0f)
        var sum = 0f

        cache[0] = 0f

        for (p in 1..divisions) {

            val current = this.getPoint(p.toFloat() / divisions)
            sum += current.distanceTo(last)
            cache[p] = sum
            last = current

        }

        this.cacheArcLengths = cache

        return cache // { sums: cache, sum: sum }; Sum is in the last element.

    }

    fun updateArcLengths() {
        needsUpdate = true
        getLengths()
    }

    fun getUtoTmapping(u: Float, distance: Float? = null): Float {
        val arcLengths = this.getLengths()

        val il = arcLengths.size

        val targetArcLength = distance ?: u * arcLengths[il - 1]

        // binary search for the index with largest value smaller than target u distance

        var low = 0
        var high = il - 1

        var i = 0
        while (low <= high) {

            i = low + (high - low) / 2

            val comparison = arcLengths[i] - targetArcLength

            if (comparison < 0) {

                low = i + 1

            } else if (comparison > 0) {

                high = i - 1

            } else {

                high = i
                break

                // DONE

            }

        }

        i = high

        if (arcLengths[i] == targetArcLength) {

            return i.toFloat() / (il - 1)

        }

        // we could get finer grain at lengths, or use simple interpolation between two points

        val lengthBefore = arcLengths[i]
        val lengthAfter = arcLengths[i + 1]

        val segmentLength = lengthAfter - lengthBefore

        // determine where we are between the 'before' and 'after' points

        val segmentFraction = (targetArcLength - lengthBefore) / segmentLength

        // add that fractional amount to t

        val t = (i + segmentFraction) / (il - 1)

        return t
    }

    /**
     *  Returns a unit vector tangent at t
     *  In case any sub curve does not implement its tangent derivation,
     *  2 points a small delta apart will be used to find its gradient
     *  which seems to give a reasonable approximation
     */
    fun getTangent(t: Float): Vector3 {

        val delta = 0.0001f
        var t1 = t - delta
        var t2 = t + delta

        // Capping in case of danger

        if (t1 < 0) t1 = 0f
        if (t2 > 1) t2 = 1f

        val pt1 = this.getPoint(t1)
        val pt2 = this.getPoint(t2)

        val vec = pt2.clone().sub(pt1)
        return vec.normalize()

    }

    fun getTangentAt(u: Float): Vector3 {

        val t = this.getUtoTmapping(u)
        return this.getTangent(t)

    }

    fun computeFrenetFrames(segments: Int, closed: Boolean): FrenetFrames {

        // see http://www.cs.indiana.edu/pub/techreports/TR425.pdf

        val normal = Vector3()

        val tangents = mutableListOf<Vector3>()
        val normals = mutableListOf<Vector3>()
        val binormals = mutableListOf<Vector3>()

        val vec = Vector3()
        val mat = Matrix4()

        // compute the tangent vectors for each segment on the curve

        for (i in 0..segments) {

            val u = i.toFloat() / segments

            tangents.add(this.getTangentAt(u))
            tangents[i].normalize()

        }

        // select an initial normal vector perpendicular to the first tangent vector,
        // and in the direction of the minimum tangent xyz component

        normals.add(Vector3())
        binormals.add(Vector3())
        var min = Float.MAX_VALUE
        val tx = abs(tangents[0].x)
        val ty = abs(tangents[0].y)
        val tz = abs(tangents[0].z)

        if (tx <= min) {

            min = tx
            normal.set(1, 0, 0)

        }

        if (ty <= min) {

            min = ty
            normal.set(0, 1, 0)

        }

        if (tz <= min) {

            normal.set(0, 0, 1)

        }

        vec.crossVectors(tangents[0], normal).normalize()

        normals[0].crossVectors(tangents[0], vec)
        binormals[0].crossVectors(tangents[0], normals[0])


        // compute the slowly-varying normal and binormal vectors for each segment on the curve

        var theta: Float

        for (i in 1..segments) {

            normals.add(normals[i - 1].clone())

            binormals.add(binormals[i - 1].clone())

            vec.crossVectors(tangents[i - 1], tangents[i])

            if (vec.length() > 1E-14 /*Number.EPSILON*/) {

                vec.normalize()

                theta = acos(clamp(tangents[i - 1].dot(tangents[i]), -1, 1)) // clamp for floating pt errors

                normals[i].applyMatrix4(mat.makeRotationAxis(vec, theta))

            }

            binormals[i].crossVectors(tangents[i], normals[i])

        }

        // if the curve is closed, postprocess the vectors so the first and last normal vectors are the same

        if (closed) {

            theta = acos(clamp(normals[0].dot(normals[segments]), -1, 1))
            theta /= segments

            if (tangents[0].dot(vec.crossVectors(normals[0], normals[segments])) > 0) {

                theta = -theta

            }

            for (i in 1..segments) {

                // twist a little...
                normals[i].applyMatrix4(mat.makeRotationAxis(tangents[i], theta * i))
                binormals[i].crossVectors(tangents[i], normals[i])

            }

        }

        return FrenetFrames(tangents, normals, binormals)

    }

}

class FrenetFrames(
    val tangents: List<Vector3>,
    val normals: List<Vector3>,
    val binormals: List<Vector3>
)
