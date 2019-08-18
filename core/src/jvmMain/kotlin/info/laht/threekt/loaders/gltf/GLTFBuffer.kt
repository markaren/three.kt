package info.laht.threekt.loaders.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class GLTFBuffer(
    val uri: String? = null,
    val byteLength: Int,
    val name: String? = null,
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null
)
