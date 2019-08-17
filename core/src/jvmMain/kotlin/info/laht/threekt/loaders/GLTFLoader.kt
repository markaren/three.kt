package info.laht.threekt.loaders

import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Quaternion
import info.laht.threekt.math.Vector3
import info.laht.threekt.textures.Image
import java.io.File
import java.nio.ByteBuffer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

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


        glft.meshes.forEach { mesh ->

            mesh.primitives.forEach { primitive ->

                val geometry = BufferGeometry()

                primitive.indices?.also { index ->
                    val view = glft.accessors[index].bufferView!!
                    val buffer = glft.buffers[view]
                    val data = ByteBuffer.wrap(File(File(path), buffer.uri!!).readBytes()).asIntBuffer()

                }

                primitive.attributes.forEach { t, u ->


                    when (t) {
                        GLTF.GLFTAttribute.NORMAL -> {
                            val accessor = glft.accessors[u]
//                            geometry.addAttribute("normal")
                        }
                        GLTF.GLFTAttribute.POSITION -> {

                        }
                    }

                }

            }

        }

    }

    private val bufferCache = mutableMapOf<String, ByteArray>()

    private fun loadBufferView(gltf: GLTF, index: Int, path: String): ByteArray {

        val bufferViewDef = gltf.bufferViews[index]
        val buffer = gltf.buffers[bufferViewDef.buffer]

        val dataLocation = File(File(path), buffer.uri!!)

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

@Serializable
private data class GLTF(

    val asset: GLFTAsset,
    val scene: Int? = null,
    val scenes: List<GLFTScene> = emptyList(),
    val nodes: List<GLFTNode> = emptyList(),
    val cameras: List<GLFTCamera> = emptyList(),
    val meshes: List<GLFTMesh> = emptyList(),
    val materials: List<GLFTMaterial> = emptyList(),
    val accessors: List<GLFTAccessor> = emptyList(),
    val textures: List<GLFTTexture> = emptyList(),
    val images: List<GLFTImage> = emptyList(),
    val samplers: List<GLFTSampler> = emptyList(),
    val bufferViews: List<GLFTBufferView> = emptyList(),
    val buffers: List<GLFTBuffer> = emptyList(),

    val extensionsUsed: List<String> = emptyList(),
    val extensionsRequired: List<String> = emptyList(),
    val extensions: JsonObject? = null,
    val extras: JsonElement? = null

) {

    @Serializable
    data class GLFTAsset(
        val version: String,
        val generator: String? = null,
        val copyright: String? = null,
        val minVersion: String? = null,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    @Serializable
    data class GLFTScene(
        var name: String? = null,
        val nodes: List<Int> = emptyList(),
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    @Serializable
    data class GLFTNode(
        val name: String? = null,
        val camera: Int? = null,
        val children: List<Int> = emptyList(),
        private val matrix: List<Float>? = null,
        private val rotation: List<Float>? = null,
        private val scale: List<Float>? = null,
        private val translation: List<Float>? = null
    ) {

        fun getMatrix(): Matrix4 {

            return if (matrix != null) {
                Matrix4(matrix.toFloatArray())
            } else {
                Matrix4().compose(position = getTranslation(), scale = getScale(), quaternion = getRotation())
            }

        }

        private fun getRotation(): Quaternion {
            return rotation?.let { Quaternion(it[0], it[1], it[2], it[3]) } ?: Quaternion()
        }

        private fun getScale(): Vector3 {
            return scale?.let { Vector3(it[0], it[1], it[2]) } ?: Vector3(1f, 1f, 1f)
        }

        private fun getTranslation(): Vector3 {
            return translation?.let { Vector3(it[0], it[1], it[2]) } ?: Vector3()
        }

    }

    @Serializable
    data class GLFTCamera(

        val perspective: Perspective? = null,
        val orthographic: Orthographic? = null,

        val type: String,
        val name: String? = null,

        val extensions: JsonObject? = null,
        val extras: JsonElement? = null

    ) {

        fun getCamera(): Camera {

            return when {
                perspective != null -> PerspectiveCamera(
                    perspective.yfov, perspective.aspectRatio, perspective.znear, perspective.zfar
                        ?: PerspectiveCamera.DEFAULT_FAR
                )
                orthographic != null -> OrthographicCamera(
                    orthographic.xmag / -2,
                    orthographic.xmag / 2,
                    orthographic.ymag / -2,
                    orthographic.ymag / 2,
                    orthographic.znear,
                    orthographic.zfar
                )
                else -> throw IllegalStateException("Neither perspective or orthographic..")
            }.also { camera ->
                name?.also { name -> camera.name = name }
            }

        }

        @Serializable
        data class Perspective(
            val yfov: Float,
            val aspectRatio: Float,
            val znear: Float,
            val zfar: Float? = null
        )

        @Serializable
        data class Orthographic(
            val xmag: Float,
            val ymag: Float,
            val znear: Float,
            val zfar: Float
        )

    }

    @Serializable
    data class GLFTMesh(
        val primitives: List<GLFTPrimitive>,
        val weights: List<Float>? = null,
        val name: String? = null,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    @Serializable
    data class GLFTPrimitive(
        val attributes: Map<GLFTAttribute, Int>,
        val indices: Int? = null,
        val material: Int? = null,
        val mode: Int = 4,
        val targets: Map<String, Int>? = null,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    @Serializable
    data class GLFTAccessor(
        val bufferView: Int? = null,
        val byteOffset: Int = 0,
        val componentType: Int,
        val normalized: Boolean = false,
        val count: Int,
        val type: String,
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

    @Serializable
    data class GLFTTexture(
        val sampler: Int,
        val source: Int
    )

    @Serializable
    data class GLFTSampler(
        val magFilter: Int? = null,
        val minFilter: Int? = null,
        val wrapS: Int? = null,
        val wrapT: Int? = null
    )

    @Serializable
    data class GLFTImage(
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

    @Serializable
    data class GLFTMaterial(

        val name: String? = null,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null,
        val pbrMetallicRoughness: PBRMetallicRoughness? = null,
        val normalTexture: NormalTextureInfo? = null,
        val occlusionTexture: OcclusionTextureInfo? = null,
        val emissiveTexture: NormalTextureInfo? = null,
        val emissiveFactor: List<Float> = listOf(0f, 0f, 0f),
        val alphaMode: GLFTAlphaMode = GLFTAlphaMode.OPAQUE,
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

    @Serializable
    data class GLFTBuffer(
        val uri: String? = null,
        val byteLength: Int,
        val name: String? = null,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    @Serializable
    data class GLFTBufferView(
        val buffer: Int,
        val byteOffset: Int = 0,
        val byteLength: Int,
        val byteStride: Int? = null,
        val target: Int,
        val name: String? = null,
        val extensions: JsonObject? = null,
        val extras: JsonElement? = null
    )

    enum class GLFTAttribute {

        NORMAL,
        POSITION,
        TANGENT,
        TEXCOORD_0,
        TEXCOORD_1,
        COLOR_0,
        JOINTS_0,
        WEIGHTS_0

    }

    enum class GLFTAlphaMode {

        OPAQUE,
        MASK,
        BLEND

    }

}
