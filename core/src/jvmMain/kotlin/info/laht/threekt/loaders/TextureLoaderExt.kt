package info.laht.threekt.loaders

import info.laht.threekt.textures.Texture
import java.io.File
import java.net.URL

fun TextureLoader.load(url: URL, flipY: Boolean = true): Texture {
    val split = url.toExternalForm().split(".")
    val type = split[split.lastIndex]
    val tmp = createTempFile(suffix = ".$type")
    tmp.writeBytes(url.openStream().use { it.readBytes() })
    return load(tmp.absolutePath, flipY).also {
        tmp.delete()
    }
}

fun TextureLoader.load(file: File, flipY: Boolean = true): Texture {
    return load(file.absolutePath, flipY)
}
