package info.laht.threekt.math

interface Flattable {

    val size: Int

    fun toArray(array: FloatArray? = null, offset: Int = 0): FloatArray

}
