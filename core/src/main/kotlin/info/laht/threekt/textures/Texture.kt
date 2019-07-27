package info.laht.threekt.textures

import info.laht.threekt.*
import info.laht.threekt.core.EventDispatcher
import info.laht.threekt.core.EventDispatcherImpl
import info.laht.threekt.math.Matrix3
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.generateUUID
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor


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
) : EventDispatcher by EventDispatcherImpl() {

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

    val offset = Vector2(0, 0)
    val repeat = Vector2(1, 1)
    val center = Vector2(0, 0)
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
        )
    }

    fun transformUv(uv: Vector2): Vector2 {

        if (this.mapping != UVMapping) return uv

        uv.applyMatrix3(this.matrix)

        if (uv.x < 0 || uv.x > 1) {

            when (this.wrapS) {

                RepeatWrapping -> uv.x = uv.x - floor(uv.x)
                ClampToEdgeWrapping -> uv.x = if (uv.x < 0) 0f else 1f
                MirroredRepeatWrapping -> {
                    if (abs(floor(uv.x).toInt() % 2) == 1) {
                        uv.x = ceil(uv.x) - uv.x
                    } else {
                        uv.x = uv.x - floor(uv.x)
                    }
                }

            }

        }

        if (uv.y < 0 || uv.y > 1) {

            when (this.wrapT) {

                RepeatWrapping -> uv.y = uv.y - floor(uv.y)
                ClampToEdgeWrapping -> uv.y = if (uv.y < 0) 0f else 1f
                MirroredRepeatWrapping -> {
                    if (abs(floor(uv.y).toInt() % 2) == 1) {
                        uv.y = ceil(uv.y) - uv.y
                    } else {
                        uv.y = uv.y - floor(uv.y)
                    }
                }

            }

        }

        return uv
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

}
