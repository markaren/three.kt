package info.laht.threekt.loaders

import info.laht.threekt.TextureFormat
import info.laht.threekt.TextureType
import info.laht.threekt.textures.Texture

object TextureLoader {

    @JvmOverloads
    fun load(path: String, flipY: Boolean = true): Texture {


        val isJpg = path.endsWith(".jpg", true) || path.endsWith(".jpeg", true)

        val texture = Texture(
                image = ImageLoader.load(path, flipY),
            format = if (isJpg) TextureFormat.RGB else TextureFormat.RGBA,
            type = TextureType.UnsignedByte
        )
        texture.needsUpdate = true

        return texture

    }

}
