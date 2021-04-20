package info.laht.threekt.textures

import java.nio.ByteBuffer

class Image(
    width: Int,
    height: Int,
    val data: ByteBuffer? = null
) {

    var width = width
        internal set

    var height = height
        internal set

    override fun toString(): String {
        return "Image(width=$width, height=$height)"
    }

}
