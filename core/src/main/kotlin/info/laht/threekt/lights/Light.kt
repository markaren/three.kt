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
    intensity: Number? = null
) : Object3D() {

    var intensity = intensity?.toFloat() ?: DEFAULT_INTENSITY

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
    intensity: Number? = null
) : Light(color, intensity) {

    constructor(color: Int, intensity: Number? = null) : this(Color(color), intensity)

    init {
        castShadow = false
    }

}

internal class LightProbe(
    var sh: SphericalHarmonics3 = SphericalHarmonics3(),
    intensity: Number? = null
) : Light(Color(), intensity) {

    fun copy(source: LightProbe): LightProbe {
        this.sh.copy(source.sh)
        this.intensity = source.intensity

        return this
    }

}

class PointLight(
    color: Color,
    intensity: Number? = null,
    distance: Number? = null,
    decay: Number? = null
) : Light(color, intensity), LightWithShadow {

    var distance = distance?.toFloat() ?: 0f
    var decay = decay?.toFloat() ?: 1f

    override val shadow = LightShadow(PerspectiveCamera(90, 1f, 0.5f, 500f))

    constructor(color: Int, intensity: Number? = null, distance: Number? = null, decay: Number? = null) : this(
        Color(color), intensity, distance, decay
    )

    fun copy(source: PointLight): PointLight{

        super.copy(source)

        this.distance = source.distance
        this.decay = source.decay

        this.shadow.copy(source.shadow)

        return this

    }

    override fun clone(): PointLight {
        return super.clone() as PointLight
    }

}

class SpotLight(
    color: Color,
    intensity: Number? = null,
    distance: Number? = null,
    angle: Number? = null,
    penumbra: Number? = null,
    decay: Number? = null
) : Light(color, intensity) {

    var distance = distance?.toFloat() ?: 0f
    var angle = angle?.toFloat() ?: (PI / 3).toFloat()
    var penumbra = penumbra?.toFloat() ?: 0f
    var decay = decay?.toFloat() ?: 1f

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

        position.copy(defaultUp)
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

    override fun clone(): SpotLight {
        return super.clone() as SpotLight
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