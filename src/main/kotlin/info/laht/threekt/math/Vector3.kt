package info.laht.threekt.math

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.DoubleBufferAttribute
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Vector3(
    var x: Double,
    var y: Double,
    var z: Double
) {

    constructor() : this(0.0, 0.0, 0.0)

    /**
     * Sets value of Vector3 vector.
     */
    fun set(x: Number, y: Number, z: Number): Vector3 {
        this.x = x.toDouble()
        this.y = y.toDouble()
        this.z = z.toDouble()
        return this
    }

    /**
     * Sets all values of Vector3 vector.
     */
    fun setScalar(s: Number): Vector3 {
        return set(s, s, s)
    }

    fun setComponent(index: Int, value: Double): Vector3 {
        when (index) {
            0 -> x = value
            1 -> y = value
            else -> throw IndexOutOfBoundsException()
        }
        return this
    }

    fun getComponent(index: Int): Double {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException()
        }
    }

    /**
     * Clones Vector3 vector.
     */
    fun clone(): Vector3 {
        return Vector3(x, y, z)
    }

    /**
     * Copies value of v to Vector3 vector.
     */
    fun copy(v: Vector3): Vector3 {
        return set(x, y, z)
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

    fun addScalar(s: Double): Vector3 {
        this.x += s
        this.y += s
        this.z += s

        return this
    }

    fun addScaledVector(v: Vector3, s: Double): Vector3 {
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

    fun subScalar(s: Double): Vector3 {
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

        return this;
    }

    /**
     * Multiplies Vector3 vector by scalar s.
     */
    fun multiplyScalar(s: Double): Vector3 {
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
        TODO()
    }

    fun applyAxisAngle(axis: Vector3, angle: Double): Vector3 {
        TODO()
    }

    fun applyMatrix3(m: Matrix3): Vector3 {
        TODO()
    }

    fun applyMatrix4(m: Matrix4): Vector3 {
        TODO()
    }

    fun applyQuaternion(q: Quaternion): Vector3 {
        TODO()
    }

    fun project(camera: Camera): Vector3 {
        TODO()
    }

    fun unproject(camera: Camera): Vector3 {
        TODO()
    }

    fun transformDirection(m: Matrix4): Vector3 {
        val x = this.x
        val y = this.y
        val z = this.z
        val e = m.elements;

        this.x = e[0] * x + e[4] * y + e[8] * z;
        this.y = e[1] * x + e[5] * y + e[9] * z;
        this.z = e[2] * x + e[6] * y + e[10] * z;

        return this.normalize()
    }

    fun divide(v: Vector3): Vector3 {
        this.x /= v.x
        this.y /= v.y
        this.z /= v.z

        return this;
    }

    /**
     * Divides Vector3 vector by scalar s.
     * Set vector to ( 0, 0, 0 ) if s == 0.
     */
    fun divideScalar(s: Double): Vector3 {
        return this.multiplyScalar(1.0 / s)
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

    fun clampScalar(min: Double, max: Double): Vector3 {
        this.x = kotlin.math.max(min, kotlin.math.min(max, this.x))
        this.y = kotlin.math.max(min, kotlin.math.min(max, this.y))
        this.z = kotlin.math.max(min, kotlin.math.min(max, this.z))

        return this
    }

    fun clampLength(min: Double, max: Double): Vector3 {
        var length = this.length();
        if (length.isNaN()) length = 1.0
        return this.divideScalar(length).multiplyScalar(kotlin.math.max(min, kotlin.math.min(max, length)))
    }

    fun floor(): Vector3 {
        this.x = kotlin.math.floor(this.x)
        this.y = kotlin.math.floor(this.y)
        this.z = kotlin.math.floor(this.z)

        return this;
    }

    fun ceil(): Vector3 {
        this.x = kotlin.math.ceil(this.x)
        this.y = kotlin.math.ceil(this.y)
        this.z = kotlin.math.ceil(this.z)

        return this;
    }

    fun round(): Vector3 {
        this.x = this.x.roundToInt().toDouble()
        this.y = this.y.roundToInt().toDouble()
        this.z = this.z.roundToInt().toDouble()

        return this;
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
    fun dot(v: Vector3): Double {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    /**
     * Computes squared length of Vector3 vector.
     */
    fun lengthSq(): Double {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    /**
     * Computes length of Vector3 vector.
     */
    fun length(): Double {
        return sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
     * Computes the Manhattan length of Vector3 vector.
     *
     * @return {Double}
     *
     * @see {@link http://en.wikipedia.org/wiki/Taxicab_geometry|Wikipedia: Taxicab Geometry}
     */
    fun manhattanLength(): Double {
        return abs(this.x) + abs(this.y) + abs(this.z);
    }

    /**
     * Computes the Manhattan length (distance) from Vector3 vector to the given vector v
     *
     * @param {Vector3} v
     *
     * @return {Double}
     *
     * @see {@link http://en.wikipedia.org/wiki/Taxicab_geometry|Wikipedia: Taxicab Geometry}
     */
    fun manhattanDistanceTo(v: Vector3): Double {
        return abs(this.x - v.x) + abs(this.y - v.y) + abs(this.z - v.z);
    }

    /**
     * Normalizes Vector3 vector.
     */
    fun normalize(): Vector3 {
        var length = length()
        if (length.isNaN()) length = 1.0
        return this.divideScalar(length);
    }

    /**
     * Normalizes Vector3 vector and multiplies it by l.
     */
    fun setLength(length: Double): Vector3 {
        return this.normalize().multiplyScalar(length);
    }

    fun lerp(v: Vector3, alpha: Double): Vector3 {
        this.x += (v.x - this.x) * alpha;
        this.y += (v.y - this.y) * alpha;
        this.z += (v.z - this.z) * alpha;

        return this;
    }

    fun lerpVectors(v1: Vector3, v2: Vector3, alpha: Double): Vector3 {
        return this.subVectors(v2, v1).multiplyScalar(alpha).add(v1)
    }

    /**
     * Sets Vector3 vector to cross product of itself and v.
     */
    fun cross(v: Vector3): Vector3 {
        return this.crossVectors(this, v);
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
        val scalar = v.dot(this) / v.lengthSq();
        return this.copy(v).multiplyScalar(scalar);
    }

    fun projectOnPlane(planeNormal: Vector3): Vector3 {
        val v1 = Vector3()
        v1.copy(this).projectOnVector(planeNormal)
        return this.sub(v1)
    }

    fun reflect(normal: Vector3): Vector3 {
        val v1 = Vector3()
        return this.sub(v1.copy(normal).multiplyScalar(2 * this.dot(normal)));
    }

    fun angleTo(v: Vector3): Double {
        val theta = this.dot(v) / (sqrt(this.lengthSq() * v.lengthSq()));
        // clamp, to handle numerical problems
        return acos(clamp(theta, -1.0, 1.0));
    }

    /**
     * Computes distance of Vector3 vector to v.
     */
    fun distanceTo(v: Vector3): Double {
        return sqrt(this.distanceToSquared(v));
    }

    /**
     * Computes squared distance of Vector3 vector to v.
     */
    fun distanceToSquared(v: Vector3): Double {
        val dx = this.x - v.x
        val dy = this.y - v.y
        val dz = this.z - v.z
        return dx * dx + dy * dy + dz * dz
    }

    //    fun setFromSpherical( s: Spherical ): Vector3
//    fun setFromCylindrical( s: Cylindrical ): Vector3
    fun setFromMatrixPosition(m: Matrix4): Vector3 {
        val e = m.elements;

        this.x = e[12];
        this.y = e[13];
        this.z = e[14];

        return this;
    }

    fun setFromMatrixScale(m: Matrix4): Vector3 {
        val sx = this.setFromMatrixColumn(m, 0).length();
        val sy = this.setFromMatrixColumn(m, 1).length();
        val sz = this.setFromMatrixColumn(m, 2).length();

        this.x = sx;
        this.y = sy;
        this.z = sz;

        return this;
    }

    fun setFromMatrixColumn(m: Matrix4, index: Int): Vector3 {
        return this.fromArray(m.elements, index * 4);
    }

    fun fromArray(array: DoubleArray, offset: Int = 0): Vector3 {
        this.x = array[offset]
        this.y = array[offset + 1]
        this.z = array[offset + 2]

        return this;
    }

    /**
     * Returns an array [x, y, z], or copies x, y and z into the provided array.
     * @param array (optional) array to store the vector to. If Vector3 is not provided, a new array will be created.
     * @param offset (optional) optional offset into the array.
     * @return The created or provided array.
     */
    @JvmOverloads
    fun toArray(array: DoubleArray = DoubleArray(2), offset: Int = 0): DoubleArray {
        array[offset] = this.x;
        array[offset + 1] = this.y;
        array[offset + 2] = this.z;

        return array;
    }

    fun fromBufferAttribute( attribute: DoubleBufferAttribute, index: Int): Vector3 {
        this.x = attribute.getX( index )
        this.y = attribute.getY( index )
        this.z = attribute.getZ( index )

        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector3

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    override fun toString(): String {
        return "Vector3(x=$x, y=$y, z=$z)"
    }

    companion object {

        val X = Vector3(1.0, 0.0, 0.0)
        val Y = Vector3(0.0, 1.0, 0.0)
        val Z = Vector3(0.0, 0.0, 1.0)

    }

}
