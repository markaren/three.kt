package info.laht.threekt.math

import info.laht.threekt.core.Cloneable


class Vector4(
    var x: Float,
    var y: Float,
    var z: Float,
    var w: Float
) : Cloneable {

    constructor() : this(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())

    fun set(x: Float, y: Float, z: Float, w: Float): Vector4 {
        this.x = x
        this.y = y
        this.z = z
        this.w = w

        return this
    }

    fun multiplyScalar(scalar: Number): Vector4 {

        this.x *= scalar.toFloat()
        this.y *= scalar.toFloat()
        this.z *= scalar.toFloat()
        this.w *= scalar.toFloat()

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
