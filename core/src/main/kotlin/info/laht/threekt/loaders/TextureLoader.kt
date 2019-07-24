package info.laht.threekt.loaders

import info.laht.threekt.RGBAFormat
import info.laht.threekt.UnsignedByteType
import info.laht.threekt.textures.Texture
import java.io.File

object TextureLoader {

    fun load(file: File): Texture {

        return ImageLoader.load(file).let {

            Texture(
                image = it,
                format = RGBAFormat,
                type = UnsignedByteType
            )

        }

    }

}