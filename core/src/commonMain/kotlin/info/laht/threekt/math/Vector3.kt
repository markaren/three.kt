package info.laht.threekt.math

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.Cloneable
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.*

data class Vector3(
        @JvmField
        var x: Float,
        @JvmField
        var y: Float,
        @JvmField
        var z: Float
) : Cloneable, Flattable {

    override val size = 3

    constructor() : this(0f, 0f, 0f)

    constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())

    /**
     * Sets value of Vector3 vector.
     */
    fun set(x: Number, y: Number, z: Number): Vector3 {
        this.x = x.toFloat()
        this.y = y.toFloat()
        this.z = z.toFloat()
        return this
    }

    /**
     * Sets all values of Vector3 vector.
     */
    fun setScalar(s: Number): Vector3 {
        return set(s, s, s)
    }

    operator fun set(index: Int, value: Float): Vector3 {
        when (index) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            else -> throw IndexOutOfBoundsException()
        }
        return this
    }

    operator fun get(index: Int): Float {
        return when (index) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IndexOutOfBoundsException()
        }
    }

    /**
     * Clones Vector3 vector.
     */
    override fun clone() = copy()

    /**
     * Copies value of v to Vector3 vector.
     */
    fun copy(v: Vector3): Vector3 {
        return set(v.x, v.y, v.z)
    }

    /**
     * Adds v to Vector3 vector.
     */
    fun add(v: Vector3): Vector3 {
        this.x += v.x
        this.y += v.y
        this.z += v.z

        return this
    }

    fun addScalar(s: Float): Vector3 {
        this.x += s
        this.y += s
        this.z += s

        return this
    }

    fun addScaledVector(v: Vector3, s: Float): Vector3 {
        this.x += v.x * s
        this.y += v.y * s
        this.z += v.z * s

        return this
    }

    /**
     * Sets Vector3 vector to a + b.
     */
    fun addVectors(a: Vector3, b: Vector3): Vector3 {
        this.x = a.x + b.x
        this.y = a.y + b.y
        this.z = a.z + b.z

        return this
    }

    /**
     * Subtracts v from Vector3 vector.
     */
    fun sub(v: Vector3): Vector3 {
        this.x -= v.x
        this.y -= v.y
        this.z -= v.z

        return this
    }

    fun subScalar(s: Float): Vector3 {
        this.x -= s
        this.y -= s
        this.z -= s

        return this
    }

    /**
     * Sets Vector3 vector to a - b.
     */
    fun subVectors(a: Vector3, b: Vector3): Vector3 {
        this.x = a.x - b.x
        this.y = a.y - b.y
        this.z = a.z - b.z

        return this
    }

    fun multiply(v: Vector3): Vector3 {
        this.x *= v.x
        this.y *= v.y
        this.z *= v.z

        return this
    }

    /**
     * Multiplies Vector3 vector by scalar s.
     */
    fun multiplyScalar(s: Float): Vector3 {
        this.x *= s
        this.y *= s
        this.z *= s

        return this
    }

    fun multiplyVectors(a: Vector3, b: Vector3): Vector3 {
        this.x = a.x * b.x
        this.y = a.y * b.y
        this.z = a.z * b.z

        return this
    }

    fun applyEuler(euler: Euler): Vector3 {
        val quaternion = Quaternion()
        return this.applyQuaternion(quaternion.setFromEuler(euler))
    }

    fun applyAxisAngle(axis: Vector3, angle: Float): Vector3 {
        val quaternion = Quaternion()
        return this.applyQuaternion(quaternion.setFromAxisAngle(axis, angle))
    }

    fun applyMatrix3(m: Matrix3): Vector3 {
        val x = this.x
        val y = this.y
        val z = this.z
        val e = m.elements

        this.x = e[0] * x + e[3] * y + e[6] * z
        this.y = e[1] * x + e[4] * y + e[7] * z
        this.z = e[2] * x + e[5] * y + e[8] * z

        return this
    }

    fun applyMatrix4(m: Matrix4): Vector3 {
        val x = this.x
        val y = this.y
        val z = this.z
        val e = m.elements

        val w = 1f / (e[3] * x + e[7] * y + e[11] * z + e[15])

        this.x = (e[0] * x + e[4] * y + e[8] * z + e[12]) * w
        this.y = (e[1] * x + e[5] * y + e[9] * z + e[13]) * w
        this.z = (e[2] * x + e[6] * y + e[10] * z + e[14]) * w

        return this
    }

    fun applyQuaternion(q: Quaternion): Vector3 {
        val x = this.x
        val y = this.y
        val z = this.z
        val qx = q.x
        val qy = q.y
        val qz = q.z
        val qw = q.w

        // calculate quat * vector

        val ix = qw * x + qy * z - qz * y
        val iy = qw * y + qz * x - qx * z
        val iz = qw * z + qx * y - qy * x
        val iw = -qx * x - qy * y - qz * z

        // calculate result * inverse quat

        this.x = ix * qw + iw * -qx + iy * -qz - iz * -qy
        this.y = iy * qw + iw * -qy + iz * -qx - ix * -qz
        this.z = iz * qw + iw * -qz + ix * -qy - iy * -qx

        return this
    }

    fun transformDirection(m: Matrix4): Vector3 {
        val x = this.x
        val y = this.y
        val z = this.z
        val e = m.elements

        this.x = e[0] * x + e[4] * y + e[8] * z
        this.y = e[1] * x + e[5] * y + e[9] * z
        this.z = e[2] * x + e[6] * y + e[10] * z

        return this.normalize()
    }

    fun divide(v: Vector3): Vector3 {
        this.x /= v.x
        this.y /= v.y
        this.z /= v.z

        return this
    }

    /**
     * Divides Vector3 vector by scalar s.
     * Set vector to ( 0, 0, 0 ) if s == 0.
     */
    fun divideScalar(s: Float): Vector3 {
        return this.multiplyScalar(1f / s)
    }

    fun min(v: Vector3): Vector3 {
        this.x = kotlin.math.min(this.x, v.x)
        this.y = kotlin.math.min(this.y, v.y)
        this.z = kotlin.math.min(this.z, v.z)

        return this
    }

    fun max(v: Vector3): Vector3 {
        this.x = kotlin.math.max(this.x, v.x)
        this.y = kotlin.math.max(this.y, v.y)
        this.z = kotlin.math.max(this.z, v.z)

        return this
    }

    fun clamp(min: Vector3, max: Vector3): Vector3 {
        this.x = kotlin.math.max(min.x, kotlin.math.min(max.x, this.x))
        this.y = kotlin.math.max(min.y, kotlin.math.min(max.y, this.y))
        this.z = kotlin.math.max(min.z, kotlin.math.min(max.z, this.z))

        return this
    }

    fun clampScalar(min: Float, max: Float): Vector3 {
        this.x = kotlin.math.max(min, kotlin.math.min(max, this.x))
        this.y = kotlin.math.max(min, kotlin.math.min(max, this.y))
        this.z = kotlin.math.max(min, kotlin.math.min(max, this.z))

        return this
    }

    fun clampLength(min: Float, max: Float): Vector3 {
        var length = this.length()
        if (length.isNaN()) length = 1f
        return this.divideScalar(length).multiplyScalar(kotlin.math.max(min, kotlin.math.min(max, length)))
    }

    fun floor(): Vector3 {
        this.x = kotlin.math.floor(this.x)
        this.y = kotlin.math.floor(this.y)
        this.z = kotlin.math.floor(this.z)

        return this
    }

    fun ceil(): Vector3 {
        this.x = kotlin.math.ceil(this.x)
        this.y = kotlin.math.ceil(this.y)
        this.z = kotlin.math.ceil(this.z)

        return this
    }

    fun round(): Vector3 {
        this.x = this.x.roundToInt().toFloat()
        this.y = this.y.roundToInt().toFloat()
        this.z = this.z.roundToInt().toFloat()

        return this
    }

    fun roundToZero(): Vector3 {
        this.x = if (this.x < 0) kotlin.math.ceil(this.x) else kotlin.math.floor(this.x)
        this.y = if (this.y < 0) kotlin.math.ceil(this.y) else kotlin.math.floor(this.y)
        this.z = if (this.z < 0) kotlin.math.ceil(this.z) else kotlin.math.floor(this.z)
        return this
    }

    /**
     * Inverts Vector3 vector.
     */
    fun negate(): Vector3 {
        this.x = -this.x
        this.y = -this.y
        this.z = -this.z

        return this
    }

    /**
     * Computes dot product of Vector3 vector and v.
     */
    fun dot(v: Vector3): Float {
        return this.x * v.x + this.y * v.y + this.z * v.z
    }

    /**
     * Computes squared length of Vector3 vector.
     */
    fun lengthSq(): Float {
        return this.x * this.x + this.y * this.y + this.z * this.z
    }

    /**
     * Computes length of Vector3 vector.
     */
    fun length(): Float {
        return sqrt(this.x * this.x + this.y * this.y + this.z * this.z)
    }

    /**
     * Computes the Manhattan length of Vector3 vector.
     *
     * @return {Float}
     *
     * @see {@link http://en.wikipedia.org/wiki/Taxicab_geometry|Wikipedia: Taxicab Geometry}
     */
    fun manhattanLength(): Float {
        return abs(this.x) + abs(this.y) + abs(this.z)
    }

    /**
     * Computes the Manhattan length (distance) from Vector3 vector to the given vector v
     *
     * @param {Vector3} v
     *
     * @return {Float}
     *
     * @see {@link http://en.wikipedia.org/wiki/Taxicab_geometry|Wikipedia: Taxicab Geometry}
     */
    fun manhattanDistanceTo(v: Vector3): Float {
        return abs(this.x - v.x) + abs(this.y - v.y) + abs(this.z - v.z)
    }

    fun setFromSpherical(s: Spherical): Vector3 {

        return this.setFromSphericalCoords(s.radius, s.phi, s.theta)

    }

    fun setFromSphericalCoords(radius: Float, phi: Float, theta: Float): Vector3 {

        var sinPhiRadius = sin(phi) * radius

        this.x = sinPhiRadius * sin(theta)
        this.y = cos(phi) * radius
        this.z = sinPhiRadius * cos(theta)

        return this

    }

    fun setFromCylindrical(c: Cylindrical): Vector3 {

        return this.setFromCylindricalCoords(c.radius, c.theta, c.y)

    }

    fun setFromCylindricalCoords(radius: Float, theta: Float, y: Float): Vector3 {

        this.x = radius * sin(theta)
        this.y = y
        this.z = radius * cos(theta)

        return this

    }

    fun project(camera: Camera): Vector3 {
        return this.applyMatrix4(camera.matrixWorldInverse).applyMatrix4(camera.projectionMatrix)
    }

    fun unproject(camera: Camera): Vector3 {
        return this.applyMatrix4(camera.projectionMatrixInverse).applyMatrix4(camera.matrixWorld)
    }

    /**
     * Normalizes Vector3 vector.
     */
    fun normalize(): Vector3 {
        var length = length()
        if (length.isNaN()) length = 1.toFloat()
        return this.divideScalar(length)
    }

    /**
     * Normalizes Vector3 vector and multiplies it by l.
     */
    fun setLength(length: Float): Vector3 {
        return this.normalize().multiplyScalar(length)
    }

    fun lerp(v: Vector3, alpha: Float): Vector3 {
        this.x += (v.x - this.x) * alpha
        this.y += (v.y - this.y) * alpha
        this.z += (v.z - this.z) * alpha

        return this
    }

    fun lerpVectors(v1: Vector3, v2: Vector3, alpha: Float): Vector3 {
        return this.subVectors(v2, v1).multiplyScalar(alpha).add(v1)
    }

    /**
     * Sets Vector3 vector to cross product of itself and v.
     */
    fun cross(v: Vector3): Vector3 {
        return this.crossVectors(this, v)
    }

    /**
     * Sets Vector3 vector to cross product of a and b.
     */
    fun crossVectors(a: Vector3, b: Vector3): Vector3 {
        val ax = a.x
        val ay = a.y
        val az = a.z
        val bx = b.x
        val by = b.y
        val bz = b.z

        this.x = ay * bz - az * by
        this.y = az * bx - ax * bz
        this.z = ax * by - ay * bx

        return this
    }

    fun projectOnVector(v: Vector3): Vector3 {
        val scalar = v.dot(this) / v.lengthSq()
        return this.copy(v).multiplyScalar(scalar)
    }

    fun projectOnPlane(planeNormal: Vector3): Vector3 {
        val v1 = Vector3()
        v1.copy(this).projectOnVector(planeNormal)
        return this.sub(v1)
    }

    fun reflect(normal: Vector3): Vector3 {
        val v1 = Vector3()
        return this.sub(v1.copy(normal).multiplyScalar(2 * this.dot(normal)))
    }

    fun angleTo(v: Vector3): Float {
        val theta = this.dot(v) / (sqrt(this.lengthSq() * v.lengthSq()))
        // clamp, to handle numerical problems
        return acos(clamp(theta, -1.0, 1.0))
    }

    /**
     * Computes distance of Vector3 vector to v.
     */
    fun distanceTo(v: Vector3): Float {
        return sqrt(this.distanceToSquared(v))
    }

    /**
     * Computes squared distance of Vector3 vector to v.
     */
    fun distanceToSquared(v: Vector3): Float {
        val dx = this.x - v.x
        val dy = this.y - v.y
        val dz = this.z - v.z
        return dx * dx + dy * dy + dz * dz
    }

    //    fun setFromSpherical( s: Spherical ): Vector3
//    fun setFromCylindrical( s: Cylindrical ): Vector3
    fun setFromMatrixPosition(m: Matrix4): Vector3 {
        val e = m.elements

        this.x = e[12]
        this.y = e[13]
        this.z = e[14]

        return this
    }

    fun setFromMatrixScale(m: Matrix4): Vector3 {
        val sx = this.setFromMatrixColumn(m, 0).length()
        val sy = this.setFromMatrixColumn(m, 1).length()
        val sz = this.setFromMatrixColumn(m, 2).length()

        this.x = sx
        this.y = sy
        this.z = sz

        return this
    }

    fun setFromMatrixColumn(m: Matrix4, index: Int): Vector3 {
        return this.fromArray(m.elements, index * 4)
    }

    fun fromArray(array: FloatArray, offset: Int = 0): Vector3 {
        this.x = array[offset]
        this.y = array[offset + 1]
        this.z = array[offset + 2]

        return this
    }

    /**
     * Returns an array [x, y, z], or copies x, y and z into the provided array.
     * @param array (optional) array to store the vector to. If Vector3 is not provided, a new array will be created.
     * @param offset (optional) optional offset into the array.
     * @return The created or provided array.
     */
    override fun toArray(array: FloatArray?, offset: Int): FloatArray {

        @Suppress("NAME_SHADOWING")
        val array = array ?: FloatArray(3)

        array[offset + 0] = this.x
        array[offset + 1] = this.y
        array[offset + 2] = this.z

        return array
    }

    companion object {

        @JvmField
        val X = Vector3(1f, 0f, 0f)
        @JvmField
        val Y = Vector3(0f, 1f, 0f)
        @JvmField
        val Z = Vector3(0f, 0f, 1f)

    }

}

operator fun Vector3.plus(v: Vector3): Vector3 {
    return clone().add(v)
}

operator fun Vector3.plusAssign(v: Vector3) {
    add(v)
}

operator fun Vector3.minus(v: Vector3): Vector3 {
    return clone().sub(v)
}

operator fun Vector3.minusAssign(v: Vector3) {
    sub(v)
}
