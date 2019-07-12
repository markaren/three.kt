package info.laht.threekt.math

class Vector4i(
    var x: Int,
    var y: Int,
    var z: Int,
    var w: Int
) {
    constructor(): this(0, 0, 0, 0)

    fun set(x: Int, y: Int, z: Int, w: Int): Vector4i {
        this.x = x
        this.y = y
        this.z = z
        this.w = w

        return this
    }

    fun clone(): Vector4i {
        return Vector4i().copy(this)
    }

    fun copy(v: Vector4i): Vector4i {
        this.x = v.x
        this.y = v.y
        this.z = v.z
        this.w = v.w
        return this
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