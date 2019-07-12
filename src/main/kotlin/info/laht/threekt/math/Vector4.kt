package info.laht.threekt.math


class Vector4(
    var x: Double,
    var y: Double,
    var z: Double,
    var w: Double
) {

    constructor(): this(0.0, 0.0, 0.0, 0.0)

    fun set(x: Double, y: Double, z: Double, w: Double): Vector4 {
        this.x = x
        this.y = y
        this.z = z
        this.w = w

        return this
    }

    fun clone(v: Vector4): Vector4 {
        return Vector4().copy(this)
    }

    fun copy(v: Vector4): Vector4 {
        this.x = v.x
        this.y = v.y
        this.z = v.z
        this.w = v.w
        return this
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
