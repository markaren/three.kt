package info.laht.threekt.loaders

import info.laht.threekt.TextureFormat
import info.laht.threekt.TextureType
import info.laht.threekt.textures.Texture
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL

object TextureLoader {

    private val LOG: Logger = LoggerFactory.getLogger(TextureLoader::class.java)

    @JvmOverloads
    fun load(path: String, flipY: Boolean = true): Texture {

        LOG.debug("Loading texture $path")

        val isJpg = path.endsWith(".jpg", true) || path.endsWith(".jpeg", true)

        val texture = Texture(
            image = ImageLoader.load(path, flipY),
            format = if (isJpg) TextureFormat.RGB else TextureFormat.RGBA,
            type = TextureType.UnsignedByte
        )
        texture.needsUpdate = true

        return texture

    }

    fun load(url: URL, flipY: Boolean = true): Texture {
        val split = url.toExternalForm().split(".")
        val type = split[split.lastIndex]
        val tmp = createTempFile(suffix = ".$type")
        tmp.writeBytes(url.openStream().use { it.readBytes() })
        return load(tmp.absolutePath, flipY).also {
            tmp.delete()
        }
    }

    fun load(file: File, flipY: Boolean = true): Texture {
        return load(file.absolutePath, flipY)
    }

}
