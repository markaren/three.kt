package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import info.laht.threekt.core.FloatBufferAttribute


class Vector4(
    var x: Float,
    var y: Float,
    var z: Float,
    var w: Float
) : Cloneable, Flattable {

    override val size = 4

    constructor() : this(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())

    constructor(x: Int, y: Int, z: Int, w: Int) : this(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())

    fun set(x: Number, y: Number, z: Number, w: Number): Vector4 {
        this.x = x.toFloat()
        this.y = y.toFloat()
        this.z = z.toFloat()
        this.w = w.toFloat()

        return this
    }

    fun multiplyScalar(scalar: Number): Vector4 {

        this.x *= scalar.toFloat()
        this.y *= scalar.toFloat()
        this.z *= scalar.toFloat()
        this.w *= scalar.toFloat()

        return this

    }

    fun floor (): Vector4 {

        this.x = kotlin.math.floor( this.x )
        this.y = kotlin.math.floor( this.y )
        this.z = kotlin.math.floor( this.z )
        this.w = kotlin.math.floor( this.w )

        return this

    }

    fun applyMatrix4(m: Matrix4): Vector4 {

        val x = this.x
        val y = this.y
        val z = this.z
        val w = this.w
        val e = m.elements

        this.x = e[0] * x + e[4] * y + e[8] * z + e[12] * w
        this.y = e[1] * x + e[5] * y + e[9] * z + e[13] * w
        this.z = e[2] * x + e[6] * y + e[10] * z + e[14] * w
        this.w = e[3] * x + e[7] * y + e[11] * z + e[15] * w

        return this

    }

    fun fromBufferAttribute(attribute: FloatBufferAttribute, index: Int): Vector4 {

        this.x = attribute.getX(index)
        this.y = attribute.getY(index)
        this.z = attribute.getZ(index)
        this.w = attribute.getW(index)

        return this

    }

    fun fromArray(array: FloatArray, offset: Int = 0): Vector4 {

        this.x = array[offset]
        this.y = array[offset + 1]
        this.z = array[offset + 2]
        this.w = array[offset + 3]

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
        val array = array ?: FloatArray(4)

        array[offset] = this.x
        array[offset + 1] = this.y
        array[offset + 2] = this.z
        array[offset + 3] = this.w

        return array
    }

    override fun clone(): Vector4 {
        return Vector4(x, y, z, w)
    }

    fun copy(v: Vector4): Vector4 {
        return set(v.x, v.y, v.z, v.w)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector4

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (w != other.w) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + w.hashCode()
        return result
    }

    override fun toString(): String {
        return "Vector4(x=$x, y=$y, z=$z, w=$w)"
    }

}
