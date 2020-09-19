package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.math.atan2
import kotlin.math.sqrt

data class Cylindrical(
    var radius: Float = 1f,
    var theta: Float = 0f,
    var y: Float = 0f
) : Cloneable {

    fun set(radius: Float, theta: Float, y: Float): Cylindrical {
        this.radius = radius
        this.theta = theta
        this.y = y

        return this
    }

    fun setFromVector3(v: Vector3): Cylindrical {

        return this.setFromCartesianCoords(v.x, v.y, v.z)

    }

    fun setFromCartesianCoords(x: Float, y: Float, z: Float): Cylindrical {

        this.radius = sqrt(x * x + z * z)
        this.theta = atan2(x, z)
        this.y = y

        return this

    }

    override fun clone() = copy()

    fun copy(source: Cylindrical): Cylindrical {
        return set(source.radius, source.theta, source.y)
    }

}
