package info.laht.threekt.loaders.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class GLTFMaterial(

    val name: String? = null,
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null,
    val pbrMetallicRoughness: PBRMetallicRoughness? = null,
    val normalTexture: NormalTextureInfo? = null,
    val occlusionTexture: OcclusionTextureInfo? = null,
    val emissiveTexture: NormalTextureInfo? = null,
    val emissiveFactor: List<Float> = listOf(0f, 0f, 0f),
    val alphaMode: GLTFAlphaMode = GLTFAlphaMode.OPAQUE,
    val alphaCutoff: Float = 0.5f,
    val doubleSided: Boolean = false

) {

    @Serializable
    data class PBRMetallicRoughness(
        val metallicFactor: Float = 1f,
        val roughnessFactor: Float = 1f,
        val baseColorFactor: List<Float> = listOf(1f, 1f, 1f, 1f),
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    @Serializable
    data class NormalTextureInfo(
        val index: Int,
        val texCoord: Int = 0,
        val scale: Float = 1f,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    @Serializable
    data class OcclusionTextureInfo(
        val index: Int,
        val texCoord: Int = 0,
        val strength: Float = 1f,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

}
