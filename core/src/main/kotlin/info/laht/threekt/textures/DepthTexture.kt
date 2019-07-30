package info.laht.threekt.textures

import info.laht.threekt.*

class DepthTexture(
    val width: Int,
    val height: Int,
    type: TextureType? = null,
    mapping: TextureMapping? = null,
    wrapS: TextureWrapping? = null,
    wrapT: TextureWrapping? = null,
    magFilter: TextureFilter? = null,
    minFilter: TextureFilter? = null,
    anisotropy: Int? = null,
    format: TextureFormat? = null
) : Texture(
    image = null,
    mapping = mapping,
    wrapS = wrapS,
    wrapT = wrapT,
    magFilter = magFilter,
    minFilter = minFilter,
    format = format,
    type = type,
    anisotropy = anisotropy
) {

    init {

        this.format = format ?: TextureFormat.Depth

        if (type == null && format == TextureFormat.Depth) {
            this.type = TextureType.UnsignedShort
        }
        if (type == null && format == TextureFormat.DepthStencil) {
            this.type = TextureType.UnsignedInt248
        }

        this.magFilter = magFilter ?: TextureFilter.Nearest
        this.minFilter = minFilter ?: TextureFilter.Nearest

        this.generateMipmaps = false

    }

}
