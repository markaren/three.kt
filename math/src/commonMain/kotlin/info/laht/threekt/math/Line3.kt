package info.laht.threekt.math

import kotlin.jvm.JvmOverloads

data class Line3 @JvmOverloads constructor(
    var start: Vector3 = Vector3(),
    var end: Vector3 = Vector3()
) {

    fun set(start: Vector3, end: Vector3): Line3 {
        this.start.copy(start)
        this.end.copy(end)

        return this
    }

    fun clone(): Line3 {
        return Line3().copy(this)
    }

    fun copy(line: Line3): Line3 {
        this.start.copy(line.start)
        this.end.copy(line.end)

        return this
    }

    fun getCenter(target: Vector3): Vector3 {
        return target.addVectors(this.start, this.end).multiplyScalar(0.5.toFloat())
    }

    fun delta(target: Vector3): Vector3 {
        return target.subVectors(this.end, this.start)
    }

    fun distanceSq(): Float {
        return this.start.distanceToSquared(this.end)
    }

    fun distance(): Float {
        return this.start.distanceTo(this.end)
    }

    fun at(t: Float, target: Vector3): Vector3 {
        return this.delta(target).multiplyScalar(t).add(this.start)
    }

    fun closestPointToPointParameter(point: Vector3, clampToLine: Boolean = false): Float {
        val startP = Vector3()
        val startEnd = Vector3()

        startP.subVectors(point, this.start)
        startEnd.subVectors(this.end, this.start)

        val startEnd2 = startEnd.dot(startEnd)
        val startEnd_startP = startEnd.dot(startP)

        var t = startEnd_startP / startEnd2

        if (clampToLine) {

            t = clamp(t, 0, 1)

        }

        return t
    }

    fun closestPointToPoint(
        point: Vector3,
        clampToLine: Boolean,
        target: Vector3
    ): Vector3 {
        val t = this.closestPointToPointParameter(point, clampToLine)
        return this.delta(target).multiplyScalar(t).add(this.start)
    }

    fun applyMatrix4(matrix: Matrix4): Line3 {
        this.start.applyMatrix4(matrix)
        this.end.applyMatrix4(matrix)

        return this
    }

}
