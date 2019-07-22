package info.laht.threekt.scenes

import info.laht.threekt.core.Cloneable
import info.laht.threekt.math.Color

sealed class _Fog(
    val color: Color
): Cloneable {

    var name = ""

    abstract override fun clone(): _Fog

}

class Fog(
    color: Color,
    val near: Float = 1f,
    val far: Float = 1000f
): _Fog(color) {

    override fun clone(): Fog {
        return Fog(color, near, far)
    }

}

class FogExp2(
    color: Color,
    density: Float? = null
): _Fog(color) {

    var density = density ?: 0.00025f

    override fun clone(): FogExp2 {
        return FogExp2(color.clone(), density)
    }

}
