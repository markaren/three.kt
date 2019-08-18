package info.laht.threekt.loaders.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class GLTFAccessor(
    val bufferView: Int? = null,
    val byteOffset: Int = 0,
    val componentType: Int,
    val normalized: Boolean = false,
    val count: Int,
    val type: GLTFType,
    val max: List<Float>? = null,
    val min: List<Float>? = null,
    val sparse: Sparse? = null,
    val name: String? = null,
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null
) {

    @Serializable
    data class Sparse(
        val count: Int,
        val indices: Indices,
        val values: Values,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    @Serializable
    data class Indices(
        val bufferView: Int,
        val byteOffset: Int = 0,
        val componentType: Int,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    @Serializable
    data class Values(
        val bufferView: Int,
        val byteOffset: Int = 0,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

}
