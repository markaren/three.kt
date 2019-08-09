package info.laht.threekt.loaders

import com.google.gson.Gson
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Quaternion
import info.laht.threekt.math.Vector3
import java.io.File

class GLTFLoader {

    private var path: String? = null
    private var resourcePath: String? = null


    fun load(path: String) {

        val file = File(path)

        parse(file.readText())

    }

    fun setPath(path: String): GLTFLoader {
        this.path = path
        return this
    }

    fun setResourcePath(path: String): GLTFLoader {
        this.resourcePath = path
        return this
    }

    fun parse(data: String) {

        println(Gson().fromJson(data, GLTF::class.java).cameras[0].getCamera())

    }

}

private class GLTF {

    lateinit var asset: GLFTAsset
    val scene: Int? = null

    val scenes: List<GLFTScene> = emptyList()
    val nodes: List<GLFTNode> = emptyList()
    val cameras: List<GLFTCamera> = emptyList()

    data class GLFTAsset(
            val version: String,
            val generator: String,
            val copyright: String? = null
    )

    class GLFTScene {

        var name: String? = null
        lateinit var nodes: IntArray

    }

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
