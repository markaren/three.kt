package info.laht.threekt.loaders

import info.laht.threekt.textures.Image
import kotlin.jvm.JvmOverloads

expect object ImageLoader {

    @JvmOverloads
    fun load(path: String, flipY: Boolean = true): Image

}
