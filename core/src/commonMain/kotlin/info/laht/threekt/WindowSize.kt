package info.laht.threekt

import info.laht.threekt.math.Vector2
import kotlin.jvm.JvmOverloads

data class WindowSize(
        var width: Int,
        var height: Int
) {

    val aspect: Float
        get() = width.toFloat() / height

    @JvmOverloads
    fun toVector2(optionalTarget: Vector2 = Vector2()): Vector2 {
        return optionalTarget.set(width.toFloat(), height.toFloat())
    }

}
