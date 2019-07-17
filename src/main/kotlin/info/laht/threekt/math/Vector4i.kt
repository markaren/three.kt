package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.math.floor

class Vector4i(
    var x: Int,
    var y: Int,
    var z: Int,
    var w: Int
) : Cloneable {
    constructor() : this(0, 0, 0, 0)

    var width: Int
        get() = z
        set(value) {
            z = value
        }
    var height: Int
        get() = w
        set(value) {
            w = value
        }

    fun set(x: Int, y: Int, z: Int, w: Int): Vector4i {
        this.x = x
        this.y = y
        this.z = z
        this.w = w

        return this
    }

    fun multiplyScalar(scalar: Number): Vector4i {

        this.x = floor(this.x * scalar.toFloat()).toInt()
        this.y = floor(this.y * scalar.toFloat()).toInt()
        this.z = floor(this.z * scalar.toFloat()).toInt()
        this.w = floor(this.w * scalar.toFloat()).toInt()

        return this

    }

    override fun clone(): Vector4i {
        return Vector4i(x, y, z, w)
    }

    fun copy(v: Vector4i): Vector4i {
        return set(v.x, v.y, v.z, v.w)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector4i

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (w != other.w) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + w
        return result
    }

    override fun toString(): String {
        return "Vector4i(x=$x, y=$y, z=$z, w=$w)"
    }

}