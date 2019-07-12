package info.laht.threekt.scenes

import info.laht.threekt.math.Color

class Fog(
    color: Color,
    val near: Double = 1.0,
    val far: Double = 1000.0
) {

    val color = color.clone()

    fun clone(): Fog {
        return Fog(color, near, far)
    }

}