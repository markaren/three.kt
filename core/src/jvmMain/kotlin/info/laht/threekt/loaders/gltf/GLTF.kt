package info.laht.threekt.loaders.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class GLTF(

    val asset: GLTFAsset,
    val scene: Int? = null,
    val scenes: List<GLTFScene> = emptyList(),
    val nodes: List<GLTFNode> = emptyList(),
    val cameras: List<GLTFCamera> = emptyList(),
    val meshes: List<GLTFMesh> = emptyList(),
    val materials: List<GLTFMaterial> = emptyList(),
    val accessors: List<GLTFAccessor> = emptyList(),
    val textures: List<GLTFTexture> = emptyList(),
    val images: List<GLTFImage> = emptyList(),
    val samplers: List<GLTFSampler> = emptyList(),
    val bufferViews: List<GLTFBufferView> = emptyList(),
    val buffers: List<GLTFBuffer> = emptyList(),

    val extensionsUsed: List<String> = emptyList(),
    val extensionsRequired: List<String> = emptyList(),
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null

)
