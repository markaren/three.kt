package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.math.*

private const val EPS = 0.000001f

data class Spherical(
        var radius: Float = 1f,
        var phi: Float = 0f,
        var theta: Float = 0f
) : Cloneable {

    fun set(radius: Float, phi: Float, theta: Float): Spherical {
        this.radius = radius
        this.phi = phi
        this.theta = theta

        return this
    }

    override fun clone() = copy()

    fun copy(source: Spherical): Spherical {
        return set(source.radius, source.phi, source.theta)
    }

    fun makeSafe(): Spherical {

        this.phi = max(EPS, min(PI.toFloat() - EPS, this.phi))

        return this
    }

    fun setFromVector3 ( v: Vector3 ): Spherical {

        return this.setFromCartesianCoords( v.x, v.y, v.z )

    }

    fun setFromCartesianCoords ( x: Float, y: Float, z: Float ): Spherical {

        this.radius = sqrt( x * x + y * y + z * z )

        if ( this.radius == 0f ) {

            this.theta = 0f
            this.phi = 0f

        } else {

            this.theta = atan2( x, z )
            this.phi = acos( clamp( y / this.radius, - 1, 1 ) )

        }

        return this

    }

}