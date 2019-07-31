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
    near: Number? = null,
    far: Number? = null
): _Fog(color) {

    val near = near?.toFloat() ?: 1f
    val far = far?.toFloat() ?: 1000f

    constructor(color: Int, near: Number? = null, far: Number? = null): this(Color(color), near, far)

    override fun clone(): Fog {
        return Fog(color, near, far)
    }

}

class FogExp2(
    color: Color,
    density: Number? = null
): _Fog(color) {

    var density = density?.toFloat() ?: 0.00025f

    constructor(color: Int, density: Float?): this(Color(color), density)

    override fun clone(): FogExp2 {
        return FogExp2(color.clone(), density)
    }

}
