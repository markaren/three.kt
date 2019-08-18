package info.laht.threekt.loaders.gltf

import info.laht.threekt.loaders.ImageLoader
import info.laht.threekt.textures.Image
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class GLTFTexture(
    val sampler: Int,
    val source: Int
)

@Serializable
internal data class GLTFSampler(
    val magFilter: Int? = null,
    val minFilter: Int? = null,
    val wrapS: Int? = null,
    val wrapT: Int? = null
)

@Serializable
internal data class GLTFImage(
    private val uri: String? = null,
    private val mimeType: Int? = null,
    private val bufferView: Int? = null,
    val name: String? = null,
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null
) {

    fun getImage(baseDir: String): Image {

        if (mimeType != null && bufferView != null) {
            TODO()
        }

        if (uri == null) {
            throw IllegalStateException("uri must not be null!")
        }

        val imageLocation = "$baseDir/$uri"
        return ImageLoader.load(imageLocation)

    }

}
