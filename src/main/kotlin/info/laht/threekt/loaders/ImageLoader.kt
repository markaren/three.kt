package info.laht.threekt.loaders

import info.laht.threekt.textures.Image
import org.lwjgl.BufferUtils
import java.io.File
import javax.imageio.ImageIO


object ImageLoader {

    private val cache = mutableMapOf<String, Image>()

    fun load(file: File): Image {

        return cache.computeIfAbsent(file.absolutePath) {
            ImageIO.read(file).let {

                val res = it.width * it.height
                val pixels = IntArray(res)
                it.getRGB(0, 0, it.width, it.height, pixels, 0, it.width)

                val buffer = BufferUtils.createByteBuffer(res * 4)
                for (y in 0 until it.height) {
                    for (x in 0 until it.width) {
                        val pixel = pixels[y * it.width + x]
                        buffer.put((pixel shr 16 and 0xFF).toByte()) // Red component
                        buffer.put((pixel shr 8 and 0xFF).toByte()) // Green component
                        buffer.put((pixel and 0xFF).toByte()) // Blue component
                        buffer.put((pixel shr 24 and 0xFF).toByte()) // Alpha component. Only for RGBA
                    }
                }

                buffer.flip()
                Image(it.width, it.height, buffer)
            }

        }
    }

    fun clearCache() {
        cache.clear()
    }

}
