package info.laht.threekt.loaders

import info.laht.threekt.textures.Texture
import java.io.File
import java.io.InputStream
import java.net.URL

fun TextureLoader.load(url: URL, flipY: Boolean = true): Texture {
    return load(url.openStream(), flipY)
}

fun TextureLoader.load(file: File, flipY: Boolean = true): Texture {
    return load(file.absolutePath, flipY)
}

fun TextureLoader.load(`in`: InputStream, flipY: Boolean = true): Texture {
    val tmp = createTempFile()
    tmp.writeBytes(`in`.use { it.readBytes() })
    return load(tmp.absolutePath, flipY).also {
        tmp.delete()
    }
}
