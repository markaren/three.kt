package info.laht.threekt.lights

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Object3D
import info.laht.threekt.math.Color
import info.laht.threekt.math.SphericalHarmonics3
import kotlin.math.PI

private const val DEFAULT_INTENSITY = 1f

interface LightWithShadow {
    val shadow: LightShadow
}

interface LightWithTarget {
    val target: Object3D
}

sealed class Light(
    val color: Color,
    intensity: Float? = null
) : Object3D() {

    var intensity = intensity ?: DEFAULT_INTENSITY

    init {
        receiveShadow = false
    }

    fun copy(source: Light): Light {
        super.copy(this, true)

        color.copy(source.color)
        this.intensity = source.intensity

        return this
    }

}

class AmbientLight(
    color: Color,
    intensity: Float? = null
) : Light(color, intensity) {

    init {
        castShadow = false
    }

}

class LightProbe(
    var sh: SphericalHarmonics3 = SphericalHarmonics3(),
    intensity: Float? = null
) : Light(Color(), intensity) {

    fun copy(source: LightProbe): LightProbe {
        this.sh.copy(source.sh)
        this.intensity = source.intensity

        return this
    }

}

class PointLight(
    color: Color,
    intensity: Float? = null,
    var distance: Float = 0f,
    var decay: Float = 1f
) : Light(color, intensity), LightWithShadow {

    override val shadow = LightShadow(PerspectiveCamera(90, 1f, 0.5f, 500f))

    fun copy(source: PointLight): PointLight {

        super.copy(source)

        this.distance = source.distance
        this.decay = source.decay

        this.shadow.copy(source.shadow)

        return this

    }

}

class SpotLight(
    color: Color,
    intensity: Float? = null,
    var distance: Float = 0f,
    var angle: Float = (PI / 3).toFloat(),
    var penumbra: Float = 0f,
    var decay: Float = 1f
) : Light(color, intensity) {

    var target = Object3D()
    var shadow = SpotLightShadow()

    var power: Float
        get() {
            return intensity * PI.toFloat()
        }
        set(value) {
            this.intensity = value / PI.toFloat()
        }

    init {

        position.copy(Object3D.defaultUp)
        updateMatrix()

    }

    fun copy(source: SpotLight): SpotLight {

        this.distance = source.distance;
        this.angle = source.angle;
        this.penumbra = source.penumbra;
        this.decay = source.decay;

        this.target = source.target.clone();

        this.shadow = source.shadow.clone();

        return this;

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