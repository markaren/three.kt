package info.laht.threekt.loaders

import info.laht.threekt.TextureFormat
import info.laht.threekt.TextureType
import info.laht.threekt.textures.Texture
import java.io.File

object TextureLoader {

    @JvmOverloads
    fun load(file: File, flipY: Boolean = true): Texture {

        val texture = Texture(
            image = ImageLoader.load(file, flipY),
            format = TextureFormat.RGBA,
            type = TextureType.UnsignedByte
        )
        texture.needsUpdate = true

        return texture

    }

}
