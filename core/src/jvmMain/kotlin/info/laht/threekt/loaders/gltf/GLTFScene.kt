package info.laht.threekt.loaders.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class GLTFScene(
    var name: String? = null,
    val nodes: List<Int> = emptyList(),
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null
)
