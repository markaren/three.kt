package info.laht.threekt.math.curves

import info.laht.threekt.math.Curve3
import info.laht.threekt.math.Vector3
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow

class CatmullRomCurve3(
        val points: List<Vector3> = mutableListOf(),
        var closed: Boolean = false,
        var curveType: CurveType = CurveType.centripetal,
        var tension: Float = 0.5f
) : Curve3() {

    private val tmp = Vector3()
    private val px = CubicPoly()
    private val py = CubicPoly()
    private val pz = CubicPoly()

    override fun getPoint(t: Float, optionalTarget: Vector3): Vector3 {
        val point = optionalTarget

        val points = this.points
        val l = points.size

        val p = (l - (if (this.closed) 0 else 1)) * t
        var intPoint = floor(p).toInt()
        var weight = p - intPoint

        if (this.closed) {

            if (intPoint <= 0) {
                intPoint += (floor(abs(intPoint).toFloat() / l).toInt() + 1) * l
            }

        } else if (weight == 0f && intPoint == l - 1) {

            intPoint = l - 2
            weight = 1f

        }

        val p0 = if (this.closed || intPoint > 0) {

            points[((intPoint - 1) % l)]

        } else {

            // extrapolate first point
            tmp.apply {
                subVectors(points[0], points[1]).add(points[0])
            }

        }

        val p1 = points[intPoint % l]
        val p2 = points[(intPoint + 1) % l]

        val p3 = if (this.closed || intPoint + 2 < l) {

            points[(intPoint + 2) % l]

        } else {

            // extrapolate last point
            tmp.apply {
                subVectors(points[l - 1], points[l - 2]).add(points[l - 1])
            }

        }

        if (this.curveType == CurveType.centripetal || this.curveType == CurveType.chordal) {

            // init Centripetal / Chordal Catmull-Rom
            val pow = if (this.curveType == CurveType.chordal) 0.5f else 0.25f
            var dt0 = p0.distanceToSquared(p1).pow(pow)
            var dt1 = p1.distanceToSquared(p2).pow(pow)
            var dt2 = p2.distanceToSquared(p3).pow(pow)

            // safety check for repeated points
            if (dt1 < 1e-4) dt1 = 1f
            if (dt0 < 1e-4) dt0 = dt1
            if (dt2 < 1e-4) dt2 = dt1

            px.initNonuniformCatmullRom(p0.x, p1.x, p2.x, p3.x, dt0, dt1, dt2)
            py.initNonuniformCatmullRom(p0.y, p1.y, p2.y, p3.y, dt0, dt1, dt2)
            pz.initNonuniformCatmullRom(p0.z, p1.z, p2.z, p3.z, dt0, dt1, dt2)

        } else if (this.curveType === CurveType.catmullrom) {

            px.initCatmullRom(p0.x, p1.x, p2.x, p3.x, this.tension)
            py.initCatmullRom(p0.y, p1.y, p2.y, p3.y, this.tension)
            pz.initCatmullRom(p0.z, p1.z, p2.z, p3.z, this.tension)

        }

        point.set(
            px.calc(weight),
            py.calc(weight),
            pz.calc(weight)
        )

        return point
    }
}

enum class CurveType {

    centripetal,
    chordal,
    catmullrom
}

class CubicPoly {

    var c0 = 0f
    var c1 = 0f
    var c2 = 0f
    var c3 = 0f

    fun init(x0: Float, x1: Float, t0: Float, t1: Float) {
        c0 = x0
        c1 = t0
        c2 = -3 * x0 + 3 * x1 - 2 * t0 - t1
        c3 = 2 * x0 - 2 * x1 + t0 + t1
    }

    fun initNonuniformCatmullRom(
        x0: Float,
        x1: Float,
        x2: Float,
        x3: Float,
        dt0: Float,
        dt1: Float,
        dt2: Float
    ) {

        // compute tangents when parameterized in [t1,t2]
        var t1 = (x1 - x0) / dt0 - (x2 - x0) / (dt0 + dt1) + (x2 - x1) / dt1
        var t2 = (x2 - x1) / dt1 - (x3 - x1) / (dt1 + dt2) + (x3 - x2) / dt2

        // rescale tangents for parametrization in [0,1]
        t1 *= dt1
        t2 *= dt1

        // initCubicPoly
        this.init(x1, x2, t1, t2)

    }

    /**
     * Standard Catmull-Rom spline: interpolate between x1 and x2 with
     * previous/following points x1/x4
     */
    fun initCatmullRom(x0: Float, x1: Float, x2: Float, x3: Float, tension: Float) {
        this.init(x1, x2, tension * (x2 - x0), tension * (x3 - x1))
    }

    fun calc(t: Float): Float {
        val t2 = t * t
        val t3 = t2 * t
        return this.c0 + this.c1 * t + this.c2 * t2 + this.c3 * t3
    }

}
