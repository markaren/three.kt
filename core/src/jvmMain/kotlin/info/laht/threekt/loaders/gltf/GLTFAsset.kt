package info.laht.threekt.loaders.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class GLTFAsset(
    val version: String,
    val generator: String? = null,
    val copyright: String? = null,
    val minVersion: String? = null,
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null
)
