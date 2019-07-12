package info.laht.threekt.textures

import info.laht.threekt.*
import info.laht.threekt.core.EventDispatcher
import info.laht.threekt.math.Matrix3
import info.laht.threekt.math.Vector2i
import info.laht.threekt.math.generateUUID
import java.awt.image.BufferedImage

class Texture(
    var image: BufferedImage? = null,
    mapping: Int? = null,
    wrapS: Int? = null,
    wrapT: Int? = null,
    magFilter: Int? = null,
    minFilter: Int? = null,
    format: Int? = null,
    type: Int? = null,
    anisotropy: Int? = null,
    encoding: Int? = null
) : EventDispatcher() {

    var name = ""
    val id = textureId++
    val uuid = generateUUID()

    val mipmaps = mutableListOf<BufferedImage>()

    var mapping = mapping ?: UVMapping

    var wrapS = wrapS ?: ClampToEdgeWrapping
    var wrapT = wrapT ?: ClampToEdgeWrapping

    var magFilter = magFilter ?: LinearFilter
    var minFilter = minFilter ?: LinearMipMapLinearFilter

    var anisotropy = anisotropy ?: 1

    var format = format ?: RGBAFormat
    var type = type ?: UnsignedByteType

    val offset = Vector2i(0, 0)
    val repeat = Vector2i(1, 1)
    val center = Vector2i(0, 0)
    var rotation = 0

    var matrixAutoUpdate = true
    val matrix = Matrix3()

    var generateMipmaps = true
    var premultiplyAlpha = false
    var flipY = true
    var unpackAlignment = 4

    var encoding = encoding ?: LinearEncoding

    var version = 0

    fun needsUpdate(flag: Boolean) {
        if (flag) {
            version++
        }
    }

    fun clone(): Texture {
        return Texture().copy(this)
    }

    fun copy(source: Texture): Texture {

        this.name = source.name

        this.image = source.image

        this.mipmaps.apply {
            clear()
            addAll(source.mipmaps)
        }

        this.mapping = source.mapping

        this.wrapS = source.wrapS
        this.wrapT = source.wrapT

        this.magFilter = source.magFilter
        this.minFilter = source.minFilter

        this.anisotropy = source.anisotropy

        this.format = source.format
        this.type = source.type

        this.offset.copy(source.offset)
        this.repeat.copy(source.repeat)
        this.center.copy(source.center)
        this.rotation = source.rotation

        this.generateMipmaps = source.generateMipmaps
        this.premultiplyAlpha = source.premultiplyAlpha
        this.flipY = source.flipY
        this.unpackAlignment = source.unpackAlignment
        this.encoding = source.encoding

        return this
    }

    fun dispose() {
        dispatchEvent("dispose")
    }

    companion object {

        var textureId = 0

    }

}