package info.laht.threekt.lights

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Object3D
import info.laht.threekt.math.Color

private const val DEFAULT_INTENSITY = 1

sealed class Light(
    val color: Color,
    var intensity: Int = DEFAULT_INTENSITY
): Object3D() {

    init {
        receiveShadow = false
    }

    fun copy ( source: Light ): Light {
        super.copy(this, true)

        color.copy(source.color)
        this.intensity = source.intensity

        return this
    }

}

class AmbientLight(
    color: Color,
    intensity: Int = DEFAULT_INTENSITY
): Light(color, intensity) {

    init {
        castShadow = false
    }

}

class PointLight(
    color: Color,
    intensity: Int = DEFAULT_INTENSITY,
    var distance: Float = 0.toFloat(),
    var decay: Float = 1.toFloat()
): Light(color) {

    var shadow = LightShadow(PerspectiveCamera(90, 1f, 0.5f, 500f))

    fun copy( source: PointLight ): PointLight {

        super.copy(source)

        this.distance = source.distance
        this.decay = source.decay

        this.shadow = source.shadow.clone()

        return this

    }

}

//class DirectionalLight(
//    color: Color
//): Light(color) {
//
//
//
//    var target = Object3D()
//
//    var shadow = DirectionalLightShadow()
//
//    init {
//
//        this.position.copy( Object3D.defaultUp )
//        this.updateMatrix();
//
//    }
//
//}