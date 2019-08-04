package info.laht.threekt.lights

import info.laht.threekt.cameras.CameraWithNearAndFar
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Cloneable
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.RAD2DEG
import info.laht.threekt.math.Vector2
import info.laht.threekt.renderers.GLRenderTarget

open class LightShadow<E : CameraWithNearAndFar>(
    val camera: E
) : Cloneable {

    var bias = 0f
    var radius = 1f

    var mapSize = Vector2(512, 512)

    var map: GLRenderTarget? = null
    var matrix = Matrix4()

    fun copy(source: LightShadow<E>): LightShadow<E> {

        this.camera.copy(source.camera, true)

        this.bias = source.bias
        this.radius = source.radius

        this.mapSize.copy(source.mapSize)

        return this
    }

    override fun clone(): LightShadow<E> {
        return LightShadow(camera).copy(this)
    }

}

class SpotLightShadow : LightShadow<PerspectiveCamera>(PerspectiveCamera(50, 1f, 0.5f, 500f)) {

    fun update(light: SpotLight) {

        val fov = RAD2DEG * 2 * light.angle
        val aspect = this.mapSize.width / this.mapSize.height
        val far = light.distance

        if (fov != camera.fov || aspect != camera.aspect || far != camera.far) {

            camera.fov = fov
            camera.aspect = aspect
            camera.far = far
            camera.updateProjectionMatrix()

        }
    }

    override fun clone(): SpotLightShadow {
        return SpotLightShadow().copy(this) as SpotLightShadow
    }

}

class DirectionalLightShadow : LightShadow<OrthographicCamera>(OrthographicCamera(-5f, 5f, 5f, -5f, 0.5f, 500f))
