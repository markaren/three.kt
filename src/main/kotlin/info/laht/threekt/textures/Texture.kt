package info.laht.threekt.textures

import info.laht.threekt.*
import info.laht.threekt.core.EventDispatcher
import info.laht.threekt.math.Matrix3
import info.laht.threekt.math.Vector2i
import info.laht.threekt.math.generateUUID
import java.awt.image.BufferedImage
import java.nio.ByteBuffer


open class Texture(
    var image: Image? = null,
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

    internal var version = 0

    val mipmaps = mutableListOf<Image>()

    var mapping = mapping ?: UVMapping

    var wrapS = wrapS ?: ClampToEdgeWrapping
    var wrapT = wrapT ?: ClampToEdgeWrapping

    var magFilter = magFilter ?: LinearFilter
    var minFilter = minFilter ?: LinearMipMapLinearFilter

    var anisotropy = anisotropy ?: 1

    open var format = format ?: RGBAFormat
    var type = type ?: UnsignedByteType

    val offset = Vector2i(0, 0)
    val repeat = Vector2i(1, 1)
    val center = Vector2i(0, 0)
    var rotation = 0f

    var matrixAutoUpdate = true
    val matrix = Matrix3()

    var generateMipmaps = true

    var unpackAlignment = 4

    var encoding = encoding ?: LinearEncoding

    internal val onUpdate: ((Texture) -> Unit)? = null

    var needsUpdate: Boolean = false
        set(value) {
            if (value) {
                version++
            }
            field = value
        }

    fun updateMatrix() {
        this.matrix.setUvTransform(
            this.offset.x,
            this.offset.y,
            this.repeat.x,
            this.repeat.y,
            this.rotation,
            this.center.x,
            this.center.y
        );
    }

    fun transformUv(uv: Vector2i): Vector2i {
        TODO()
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
//        this.premultiplyAlpha = source.premultiplyAlpha
//        this.flipY = source.flipY
        this.unpackAlignment = source.unpackAlignment
        this.encoding = source.encoding

        return this
    }

    fun dispose() {
        dispatchEvent("dispose", this)
    }

    private companion object {

        var textureId = 0

    }

}

class Image(
    width: Int,
    height: Int,
    val data: ByteBuffer? = null
) {

    var width = width
        internal set

    var height = height
        internal set

    internal var complete: Boolean? = null

}
