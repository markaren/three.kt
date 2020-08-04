package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.jvm.JvmField
import kotlin.math.*

data class Vector2(
        @JvmField
        var x: Float,
        @JvmField
        var y: Float
) : Cloneable, Flattable {

    override val size = 2

    constructor() : this(0f, 0f)

    constructor(x: Number, y: Number) : this(x.toFloat(), y.toFloat())

    var width: Float
        get() {
            return x
        }
        set(value) {
            x = value
        }

    var height: Float
        get() {
            return y
        }
        set(value) {
            y = value
        }

    /**
     * Sets value of this vector.
     */
    fun set(x: Float, y: Float): Vector2 {
        this.x = x
        this.y = y

        return this
    }

    /**
     * Sets the x and y values of this vector both equal to scalar.
     */
    fun setScalar(scalar: Float): Vector2 {
        return set(scalar, scalar)
    }

    /**
     * Sets a component of this vector.
     */
    operator fun set(index: Int, value: Float): Vector2 {
        when (index) {
            0 -> x = value
            1 -> y = value
            else -> throw IndexOutOfBoundsException()
        }

        return this
    }

    /**
     * Gets a component of this vector.
     */
    operator fun get(index: Int): Float {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException()
        }
    }

    /**
     * Returns a new Vector2 instance with the same `x` and `y` values.
     */
    override fun clone() = copy()

    /**
     * Copies value of v to this vector.
     */
    fun copy(v: Vector2): Vector2 {
        return set(v.x, v.y)
    }

    /**
     * Adds v to this vector.
     */
    fun add(v: Vector2): Vector2 {
        this.x += v.x
        this.y += v.y

        return this
    }

    /**
     * Adds the scalar value s to this vector's x and y values.
     */
    fun addScalar(s: Float): Vector2 {
        this.x += s
        this.y += s

        return this
    }

    /**
     * Sets this vector to a + b.
     */
    fun addVectors(a: Vector2, b: Vector2): Vector2 {
        this.x = a.x + b.x
        this.y = a.y + b.y

        return this
    }

    /**
     * Adds the multiple of v and s to this vector.
     */
    fun addScaledVector(v: Vector2, s: Float): Vector2 {
        this.x += v.x * s
        this.y += v.y * s

        return this
    }

    /**
     * Subtracts v from this vector.
     */
    fun sub(v: Vector2): Vector2 {
        this.x -= v.x
        this.y -= v.y

        return this
    }

    /**
     * Subtracts s from this vector's x and y components.
     */
    fun subScalar(s: Float): Vector2 {
        this.x -= s
        this.y -= s

        return this
    }

    /**
     * Sets this vector to a - b.
     */
    fun subVectors(a: Vector2, b: Vector2): Vector2 {
        this.x = a.x - b.x
        this.y = a.y - b.y

        return this
    }

    /**
     * Multiplies this vector by v.
     */
    fun multiply(v: Vector2): Vector2 {
        this.x *= v.x
        this.y *= v.y

        return this
    }

    /**
     * Multiplies this vector by scalar s.
     */
    fun multiplyScalar(scalar: Float): Vector2 {
        this.x *= scalar
        this.y *= scalar

        return this
    }

    /**
     * Divides this vector by v.
     */
    fun divide(v: Vector2): Vector2 {
        this.x /= v.x
        this.y /= v.y

        return this
    }

    /**
     * Divides this vector by scalar s.
     * Set vector to ( 0, 0 ) if s == 0.
     */
    fun divideScalar(s: Float): Vector2 {
        return this.multiplyScalar(1.toFloat() / s)
    }

    /**
     * Multiplies this vector (with an implicit 1 as the 3rd component) by m.
     */
    fun applyMatrix3(m: Matrix3): Vector2 {
        val x = this.x
        val y = this.y
        val e = m.elements

        this.x = e[0] * x + e[3] * y + e[6]
        this.y = e[1] * x + e[4] * y + e[7]

        return this
    }

    /**
     * If this vector's x or y value is greater than v's x or y value, replace that value with the corresponding min value.
     */
    fun min(v: Vector2): Vector2 {
        this.x = min(this.x, v.x)
        this.y = min(this.y, v.y)

        return this
    }

    /**
     * If this vector's x or y value is less than v's x or y value, replace that value with the corresponding max value.
     */
    fun max(v: Vector2): Vector2 {
        this.x = max(this.x, v.x)
        this.y = max(this.y, v.y)

        return this
    }

    /**
     * If this vector's x or y value is greater than the max vector's x or y value, it is replaced by the corresponding value.
     * If this vector's x or y value is less than the min vector's x or y value, it is replaced by the corresponding value.
     * @param min the minimum x and y values.
     * @param max the maximum x and y values in the desired range.
     */
    fun clamp(min: Vector2, max: Vector2): Vector2 {
        this.x = max(min.x, min(max.x, this.x))
        this.y = max(min.y, min(max.y, this.y))

        return this
    }

    /**
     * If this vector's x or y values are greater than the max value, they are replaced by the max value.
     * If this vector's x or y values are less than the min value, they are replaced by the min value.
     * @param min the minimum value the components will be clamped to.
     * @param max the maximum value the components will be clamped to.
     */
    fun clampScalar(min: Float, max: Float): Vector2 {
        this.x = max(min, min(max, this.x))
        this.y = max(min, min(max, this.y))

        return this
    }

    /**
     * If this vector's length is greater than the max value, it is replaced by the max value.
     * If this vector's length is less than the min value, it is replaced by the min value.
     * @param min the minimum value the length will be clamped to.
     * @param max the maximum value the length will be clamped to.
     */
    fun clampLength(min: Float, max: Float): Vector2 {
        var length = this.length()
        if (length.isNaN()) length = 1f

        return this.divideScalar(length).multiplyScalar(max(min, min(max, length)))
    }

    /**
     * The components of the vector are rounded down to the nearest integer value.
     */
    fun floor(): Vector2 {
        this.x = floor(this.x)
        this.y = floor(this.y)

        return this
    }

    /**
     * The x and y components of the vector are rounded up to the nearest integer value.
     */
    fun ceil(): Vector2 {
        this.x = ceil(this.x)
        this.y = ceil(this.y)

        return this
    }

    /**
     * The components of the vector are rounded to the nearest integer value.
     */
    fun round(): Vector2 {
        this.x = this.x.roundToInt().toFloat()
        this.y = this.y.roundToInt().toFloat()

        return this
    }

    /**
     * The components of the vector are rounded towards zero (up if negative, down if positive) to an integer value.
     */
    fun roundToZero(): Vector2 {
        this.x = if (this.x < 0) ceil(this.x) else floor(this.x)
        this.y = if (this.y < 0) ceil(this.y) else floor(this.y)

        return this
    }

    /**
     * Inverts this vector.
     */
    fun negate(): Vector2 {
        this.x = -this.x
        this.y = -this.y

        return this
    }

    /**
     * Computes dot product of this vector and v.
     */
    fun dot(v: Vector2): Float {
        return this.x * v.x + this.y * v.y
    }

    /**
     * Computes cross product of this vector and v.
     */
    fun cross(v: Vector2): Float {
        return this.x * v.y - this.y * v.x
    }

    /**
     * Computes squared length of this vector.
     */
    fun lengthSq(): Float {
        return this.x * this.x + this.y * this.y
    }

    /**
     * Computes length of this vector.
     */
    fun length(): Float {
        return sqrt(this.x * this.x + this.y * this.y)
    }

    /**
     * Computes the Manhattan length of this vector.
     *
     * @return {Float}
     *
     * @see {@link http://en.wikipedia.org/wiki/Taxicab_geometry|Wikipedia: Taxicab Geometry}
     */
    fun manhattanLength(): Float {
        return abs(this.x) + abs(this.y)
    }

    /**
     * Normalizes this vector.
     */
    fun normalize(): Vector2 {
        var length = this.length()
        if (length.isNaN()) {
            length = 1.toFloat()
        }
        this.divideScalar(length)

        return this
    }

    /**
     * computes the angle in radians with respect to the positive x-axis
     */
    fun angle(): Float {
        var angle = atan2(this.y, this.x)
        if (angle < 0) {
            angle += 2 * PI.toFloat()
        }
        return angle
    }

    /**
     * Computes distance of this vector to v.
     */
    fun distanceTo(v: Vector2): Float {
        return sqrt(this.distanceToSquared(v))
    }

    /**
     * Computes squared distance of this vector to v.
     */
    fun distanceToSquared(v: Vector2): Float {
        val dx = this.x - v.x
        val dy = this.y - v.y
        return dx * dx + dy * dy
    }

    /**
     * Computes the Manhattan length (distance) from this vector to the given vector v
     *
     * @param {Vector2} v
     *
     * @return {Float}
     *
     * @see {@link http://en.wikipedia.org/wiki/Taxicab_geometry|Wikipedia: Taxicab Geometry}
     */
    fun manhattanDistanceTo(v: Vector2): Float {
        return abs(this.x - v.x) + abs(this.y - v.y)
    }

    /**
     * Normalizes this vector and multiplies it by l.
     */
    fun setLength(length: Float) {
        this.normalize().also {
            multiplyScalar(length)
        }
    }

    /**
     * Linearly interpolates between this vector and v, where alpha is the distance along the line - alpha = 0 will be this vector, and alpha = 1 will be v.
     * @param v vector to interpolate towards.
     * @param alpha interpolation factor in the closed interval [0, 1].
     */
    fun lerp(v: Vector2, alpha: Float): Vector2 {
        this.x += (v.x - this.x) * alpha
        this.y += (v.y - this.y) * alpha

        return this
    }

    /**
     * Sets this vector to be the vector linearly interpolated between v1 and v2 where alpha is the distance along the line connecting the two vectors - alpha = 0 will be v1, and alpha = 1 will be v2.
     * @param v1 the starting vector.
     * @param v2 vector to interpolate towards.
     * @param alpha interpolation factor in the closed interval [0, 1].
     */
    fun lerpVectors(v1: Vector2, v2: Vector2, alpha: Float): Vector2 {
        return this.subVectors(v2, v1).multiplyScalar(alpha).add(v1)
    }

    /**
     * Rotates the vector around center by angle radians.
     * @param center the point around which to rotate.
     * @param angle the angle to rotate, in radians.
     */
    fun rotateAround(center: Vector2, angle: Float): Vector2 {
        val c = cos(angle)
        val s = sin(angle)

        val x = this.x - center.x
        val y = this.y - center.y

        this.x = x * c - y * s + center.x
        this.y = x * s + y * c + center.y

        return this
    }

    /**
     * Sets this vector's x value to be array[offset] and y value to be array[offset + 1].
     * @param array the source array.
     * @param offset (optional) offset into the array. Default is 0.
     */
    fun fromArray(array: FloatArray, offset: Int = 0): Vector2 {
        this.x = array[offset]
        this.y = array[offset + 1]

        return this
    }

    /**
     * Returns an array [x, y], or copies x and y into the provided array.
     * @param array (optional) array to store the vector to. If this is not provided, a new array will be created.
     * @param offset (optional) optional offset into the array.
     * @return The created or provided array.
     */

    override fun toArray(array: FloatArray?, offset: Int): FloatArray {

        @Suppress("NAME_SHADOWING")
        val array = array ?: FloatArray(2)

        return array.also {
            array[0] = x
            array[1] = y
        }
    }

}

operator fun Vector2.plus(v: Vector2): Vector2 {
    return clone().add(v)
}

operator fun Vector2.plusAssign(v: Vector2) {
    add(v)
}

operator fun Vector2.minus(v: Vector2): Vector2 {
    return clone().sub(v)
}

operator fun Vector2.minusAssign(v: Vector2) {
    sub(v)
}
