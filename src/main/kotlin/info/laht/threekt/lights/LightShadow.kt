package info.laht.threekt.lights

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector2i
import info.laht.threekt.renderers.GLRenderTarget

open class LightShadow (
    val camera: PerspectiveCamera
) {

    var bias = 0f
    var radius = 1f

    var mapSize = Vector2i( 512, 512 )

    var map: GLRenderTarget? = null
    var matrix = Matrix4()

    fun copy( source: LightShadow ): LightShadow {

        this.camera.copy(source.camera, true)

        this.bias = source.bias
        this.radius = source.radius

        this.mapSize.copy( source.mapSize )

        return this
    }

    fun clone(): LightShadow {
        TODO()
    }

}
