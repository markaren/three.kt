package info.laht.threekt.loaders.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class GLTFMesh(
    val primitives: List<GLTFPrimitive>,
    val weights: List<Float>? = null,
    val name: String? = null,
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null
) {

    @Serializable
    internal data class GLTFPrimitive(
        val attributes: Map<GLTFAttribute, Int>,
        val indices: Int? = null,
        val material: Int? = null,
        val mode: Int = 4,
        val targets: Map<String, Int>? = null,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

}
