package info.laht.threekt.loaders

import com.google.gson.Gson
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

        println(Gson().fromJson(data, GLTF::class.java).cameras[0].type)

    }

}

private class GLTF {

    lateinit var asset: Asset
    val scene: Int? = null

    val scenes: List<Scene> = emptyList()
    val nodes: List<Node> = emptyList()
    val cameras: List<Camera> = emptyList()

    data class Asset(
            val version: String,
            val generator: String,
            val copyright: String? = null
    )

    class Scene {

        var name: String? = null
        lateinit var nodes: IntArray

    }

    class Node {

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

    class Camera {

        lateinit var type: String

    }

    data class Buffer(
            val byteLength: Int,
            val uri: String
    )

    data class BufferView(
            val buffer: Int,
            val byteLength: Int,
            val byteOffset: Int
    )

}
