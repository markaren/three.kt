package info.laht.threekt.lights

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