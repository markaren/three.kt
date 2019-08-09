package info.laht.threekt.loaders

import com.google.gson.Gson
import info.laht.threekt.LoaderUtils
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Quaternion
import info.laht.threekt.math.Vector3
import info.laht.threekt.textures.Image
import java.io.File

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
        println(Gson().fromJson(data, GLTFData::class.java).images.getOrNull(0)?.getImage(path))
    }

}

private class GLTFData {

    lateinit var asset: GLFTAsset

    val scenes: List<GLFTScene> = emptyList()
    val nodes: List<GLFTNode> = emptyList()
    val cameras: List<GLFTCamera> = emptyList()
    val meshes: List<GLFTMesh> = emptyList()
    val accessors: List<GLFTAccessor> = emptyList()
    val textures: List<GLFTTexture> = emptyList()
    val images: List<GLFTImage> = emptyList()
    val samplers: List<GLFTSampler> = emptyList()

    data class GLFTAsset(
            val version: String,
            val generator: String,
            val copyright: String? = null
    )

    data class GLFTScene(
            var name: String? = null,
            val nodes: IntArray
    )

    class GLFTNode {

        var name: String? = null
        var camera: Int? = null
        var children: IntArray? = null

        private var matrix: FloatArray? = null
        private var rotation: FloatArray? = null
        private var scale: FloatArray? = null
        private var translation: FloatArray? = null

        fun getMatrix(): Matrix4? {
            return matrix?.let { Matrix4(it) }
        }

        fun getRotation(): Quaternion? {
            return rotation?.let { Quaternion(it[0], it[1], it[2], it[3]) }
        }

        fun getScale(): Vector3? {
            return scale?.let { Vector3(it[0], it[1], it[2]) }
        }

        fun getTranslation(): Vector3? {
            return translation?.let { Vector3(it[0], it[1], it[2]) }
        }

    }

    class GLFTCamera {

        val name: String? = null
        lateinit var type: String

        val perspective: Perspective? = null
        val orthographic: Orthographic? = null

        data class Perspective(
                val yfov: Float,
                val aspectRatio: Float,
                val znear: Float,
                val zfar: Float? = null
        )

        data class Orthographic(
                val xmag: Float,
                val ymag: Float,
                val znear: Float,
                val zfar: Float
        )

        fun getCamera(): Camera {

            return when {
                perspective != null -> PerspectiveCamera(perspective.yfov, perspective.aspectRatio, perspective.znear, perspective.zfar
                        ?: PerspectiveCamera.DEFAULT_FAR)
                orthographic != null -> OrthographicCamera()
                else -> throw IllegalStateException("Neither perspective or orthographic..")
            }

        }

    }

    data class GLFTMesh(
            val primitives: List<GLFTPrimitive>
    )

    data class GLFTPrimitive(
            val name: String? = null,
            val indices: Int? = null,
            val material: Int,
            val mode: Int,
            val attributes: Map<String, Int>
    )

    data class GLFTAccessor(
            val bufferView: Int,
            val byteOffset: Int,
            val componentType: Int,
            val count: Int,
            val max: List<Float>? = null,
            val min: List<Float>? = null,
            val type: String

    )

    data class GLFTTexture(
            val sampler: Int,
            val source: Int
    )

    data class GLFTSampler(
            val magFilter: Int? = null,
            val minFilter: Int? = null,
            val wrapS: Int? = null,
            val wrapT: Int? = null
    )

    class GLFTImage {

        private val uri: String? = null

        private val bufferView: Int? = null
        private val mimeType: Int? = null

        fun getImage(baseDir: String): Image {

            if (mimeType != null && bufferView != null) {
                TODO()
            }

            if (uri == null) {
                throw IllegalStateException("uri must not be null!")
            }

            val imageLocation = "$baseDir/$uri"
            return ImageLoader.load(File(imageLocation))

        }

    }

    data class GLFTBuffer(
            val byteLength: Int,
            val uri: String
    )

    data class GLFTBufferView(
            val buffer: Int,
            val byteLength: Int,
            val byteOffset: Int
    )

}
