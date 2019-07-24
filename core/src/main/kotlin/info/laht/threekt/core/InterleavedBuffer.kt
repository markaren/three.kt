package info.laht.threekt.core

sealed class InterleavedBuffer(
    val stride: Int
) {

    internal var version = 0

    abstract val size: Int

    internal val count: Int
        get() = size / stride

    var dynamic: Boolean = false
    internal var updateRange = UpdateRange(0, -1)

}
