package info.laht.threekt.core

sealed class InterleavedBuffer(
    val stride: Int
) {

    abstract val size: Int

    internal val count: Int
        get() = size / stride

    var dynamic: Boolean = false
    internal var updateRange = UpdateRange(0, -1)

    internal var version = 0

    var needsUpdate: Boolean = false
        set(value) {
            if (value) {
                version++
            }
            field = value
        }

}

class InterleavedIntBuffer(
        val array: IntArray,
        stride: Int
) : InterleavedBuffer(stride) {

    override val size: Int
        get() = array.size

}

class InterleavedFloatBuffer(
        val array: FloatArray,
        stride: Int
) : InterleavedBuffer(stride) {

    override val size: Int
        get() = array.size

}
