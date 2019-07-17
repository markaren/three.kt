package info.laht.threekt.textures

import info.laht.threekt.*

class DepthTexture(
    val width: Int,
    val height: Int,
    type: Int? = null,
    mapping: Int? = null,
    wrapS: Int? = null,
    wrapT: Int? = null,
    magFilter: Int? = null,
    minFilter: Int? = null,
    anisotropy: Int? = null,
    format: Int? = null
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

        this.format = format ?: DepthFormat

        if (type == null && format == DepthFormat) {
            this.type = UnsignedShortType
        }
        if (type == null && format == DepthStencilFormat) {
            this.type = UnsignedInt248Type
        }

        this.magFilter = magFilter ?: NearestFilter
        this.minFilter = minFilter ?: NearestFilter

        this.flipY = false
        this.generateMipmaps = false

    }

}