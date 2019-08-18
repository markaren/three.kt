package info.laht.threekt.loaders.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class GLTFMesh(
    val primitives: List<GLTFPrimitive>,
    val weights: List<Float>? = null,
    val name: String? = null,
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null
)
