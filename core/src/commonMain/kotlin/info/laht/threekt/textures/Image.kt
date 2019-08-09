package info.laht.threekt.textures

import kotlinx.io.core.IoBuffer

class Image(
    width: Int,
    height: Int,
    val data: IoBuffer? = null
) {

    var width = width
        internal set

    var height = height
        internal set

    override fun toString(): String {
        return "Image(width=$width, height=$height)"
    }

}
