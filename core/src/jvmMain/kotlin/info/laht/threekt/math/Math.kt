package info.laht.threekt.math

import java.util.*
import kotlin.math.*

val LN2 = ln(2.0).toFloat()
const val DEG2RAD = (PI / 180.0).toFloat()
const val RAD2DEG =  (180.0 / PI).toFloat()

const val TWO_PI = (PI * 2).toFloat()

fun generateUUID(): String {
    return UUID.randomUUID().toString()
}

fun clamp(value: Int, min: Int, max: Int): Int {
    return max(min, min(max, value))
}

fun clamp(value: Float, min: Number, max: Number): Float {
    return max(min.toFloat(), min(max.toFloat(), value))
}

fun mapLinear(x: Float, a1: Float, a2: Float, b1: Float, b2: Float): Float {
    return b1 + ( x - a1 ) * ( b2 - b1 ) / ( a2 - a1 )
}

fun lerp(x: Float, y: Float, t: Float): Float {
    return ( 1 - t ) * x + t * y
}

fun smoothStep (x: Float, min: Float, max: Float): Float {
    if ( x <= min ) {
        return 0f
    }
    if ( x >= max ) {
        return 1f
    }

    @Suppress("NAME_SHADOWING")
    val x = ( x - min ) / ( max - min )

    return x * x * ( 3 - 2 * x )
}

fun isPowerOfTwo(value: Int): Boolean {
    return ( value and ( value - 1 ) ) == 0 && value != 0
}

fun ceilPowerOfTwo(value: Float): Float {
    return 2f.pow(ceil(ln(value) / LN2))
}

fun floorPowerOfTwo(value: Float): Float {
    return 2f.pow(floor(ln(value) / LN2))
}
