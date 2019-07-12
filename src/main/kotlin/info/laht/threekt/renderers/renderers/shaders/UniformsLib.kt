package info.laht.threekt.renderers.renderers.shaders

import info.laht.threekt.math.Color
import info.laht.threekt.math.Matrix3

object UniformsLib {
    val common = mapOf(
        "diffuse" to Color.fromHex(0xeeeeee),
        "opacity" to 1.0,
        "map" to null,
        "uvTransform" to Matrix3(),
        "alphaMap" to null
    )
    val specularMap = mapOf(
        "specularMap" to null
    )
}