package info.laht.threekt.loaders

import info.laht.threekt.RGBAFormat
import info.laht.threekt.UnsignedByteType
import info.laht.threekt.textures.Texture
import java.io.File

object TextureLoader {

    fun load(file: File): Texture {

        val texture = Texture(
            image = ImageLoader.load(file),
            format = RGBAFormat,
            type = UnsignedByteType
        )
        texture.needsUpdate = true

        return texture

    }

}
