package info.laht.threekt.loaders

import info.laht.threekt.loaders.gltf.GLTF
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File

class GLTFLoader {

    private var path: String? = null
    private var resourcePath: String? = null

    fun setPath(path: String): GLTFLoader {
        this.path = path
        return this
    }

    fun setResourcePath(path: String): GLTFLoader {
        this.resourcePath = path
        return this
    }

    fun load(url: String) {
        val path = this.path ?: LoaderUtils.extractUrlBase(url)
        parse(File(url).readText(), path)
    }

    fun parse(data: String, path: String) {
        val json = Json(JsonConfiguration.Stable.copy(encodeDefaults = false, strictMode = false))

        val glft = json.parse(GLTF.serializer(), data)

    }


    private val bufferCache = mutableMapOf<String, ByteArray>()

    private fun loadBufferView(gltf: GLTF, index: Int, path: String): ByteArray {

        val bufferViewDef = gltf.bufferViews[index]
        val buffer = gltf.buffers[bufferViewDef.buffer]

        val dataLocation = File("$path/${buffer.uri!!}")

        val data = bufferCache[dataLocation.absolutePath] ?: run {
            dataLocation.readBytes().also {
                bufferCache[dataLocation.absolutePath] = it
            }
        }

        val byteLength = bufferViewDef.byteLength
        val byteOffset = bufferViewDef.byteOffset
        return data.sliceArray(byteOffset until byteOffset + byteLength)

    }

}
