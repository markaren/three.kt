package info.laht.threekt.lights

import info.laht.threekt.cameras.Camera
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector2i

open class LightShadow (
    camera: Camera
) {

    private var camera = camera

    var bias = 0
    var radius = 1

    var mapSize = Vector2i( 512, 512 )

    var map = null
    var matrix = Matrix4()

    fun copy( source: LightShadow ): LightShadow {

        this.camera = source.camera.clone()

        this.bias = source.bias
        this.radius = source.radius

        this.mapSize.copy( source.mapSize )

        return this
    }

    fun clone(): LightShadow {
        TODO()
    }

}
