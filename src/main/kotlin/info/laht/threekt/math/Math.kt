package info.laht.threekt.math

import java.util.*
import kotlin.math.*

val LN2 = ln(2.0)
const val DEG2RAD = PI / 180.0
const val RAD2DEG =  180.0 / PI


fun generateUUID(): String {
    return UUID.randomUUID().toString()
}

fun clamp(value: Int, min: Int, max: Int): Int {
    return kotlin.math.max(min, kotlin.math.min(max, value))
}

fun clamp(value: Double, min: Number, max: Number): Double {
    return kotlin.math.max(min.toDouble(), kotlin.math.min(max.toDouble(), value))
}

fun mapLinear(x: Double, a1: Double, a2: Double, b1: Double, b2: Double): Double {
    return b1 + ( x - a1 ) * ( b2 - b1 ) / ( a2 - a1 )
}

fun lerp(x: Double, y: Double, t: Double): Double {
    return ( 1 - t ) * x + t * y
}

fun smoothStep (x: Double, min: Double, max: Double): Double {
    val min = min.toDouble()
    val max = max.toDouble()
    if ( x <= min ) {
        return 0.0
    }
    if ( x >= max ) {
        return 1.0
    }

    val x = ( x - min ) / ( max - min );

    return x * x * ( 3 - 2 * x );
}

fun smootherStep (x: Double, min: Double, max: Double): Double {
    if ( x <= min ) {
        return 0.0
    }
    if ( x >= max ) {
        return 1.0
    }

    val x = ( x - min ) / ( max - min )

    return x * x * x * ( x * ( x * 6 - 15 ) + 10 )
}

fun isPowerOfTwo(value: Int): Boolean {
    return ( value and ( value - 1 ) ) == 0 && value != 0;
}

fun ceilPowerOfTwo(value: Double): Double {
    return 2.0.pow(ceil(ln(value) / LN2));
}

fun floorPowerOfTwo(value: Double): Double {
    return 2.0.pow(floor(ln(value) / LN2));
}