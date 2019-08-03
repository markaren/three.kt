package info.laht.threekt.loaders

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.IntBufferAttribute
import java.io.File

private val VERTEX_PATTERN = "v( +[\\d|.|+|\\-|e|E]+)( +[\\d|.|+|\\-|e|E]+)( +[\\d|.|+|\\-|e|E]+)"
private val NORMAL_PATTERN = "vn( +[\\d|.|+|\\-|e|E]+)( +[\\d|.|+|\\-|e|E]+)( +[\\d|.|+|\\-|e|E]+)"

// f vertex vertex vertex ...
private val FACE_PATTERN1 = "f( +-?\\d+)( +-?\\d+)( +-?\\d+)( +-?\\d+)?"

// f vertex/uv vertex/uv vertex/uv ...
private val FACE_PATTERN2 =
    "f( +(-?\\d+)/(-?\\d+))( +(-?\\d+)/(-?\\d+))( +(-?\\d+)/(-?\\d+))( +(-?\\d+)/(-?\\d+))?"

// f vertex/uv/normal vertex/uv/normal vertex/uv/normal ...
private val FACE_PATTERN3 =
    "f( +(-?\\d+)/(-?\\d+)/(-?\\d+))( +(-?\\d+)/(-?\\d+)/(-?\\d+))( +(-?\\d+)/(-?\\d+)/(-?\\d+))( +(-?\\d+)/(-?\\d+)/(-?\\d+))?"

// f vertex//normal vertex//normal vertex//normal ...
private val FACE_PATTERN4 =
    "f( +(-?\\d+)//(-?\\d+))( +(-?\\d+)//(-?\\d+))( +(-?\\d+)//(-?\\d+))( +(-?\\d+)//(-?\\d+))?"

class OBJLoader {

    fun load(path: String): BufferGeometry {
        return File(path).readText().let {
            parse(it)
        }
    }

    fun parse(text: String): BufferGeometry {

        val indices = ArrayList<Int>()
        val vertices = ArrayList<Float>()
        val normals = ArrayList<Float>()

        VERTEX_PATTERN.toRegex().findAll(text).forEach {
            val result = it.groups[0]!!.value.replace("  ", " ").split(" ")
            vertices.add(result[1].toFloat())
            vertices.add(result[2].toFloat())
            vertices.add(result[3].toFloat())
        }

        NORMAL_PATTERN.toRegex().findAll(text).forEach {
            val result = it.groups[0]!!.value.replace("  ", " ").split(" ")
            normals.add(result[1].toFloat())
            normals.add(result[2].toFloat())
            normals.add(result[3].toFloat())
        }

        FACE_PATTERN1.toRegex().findAll(text).forEach {
            val result = it.groups[0]!!.value.split(" ")
            indices.add(result[1].toInt() - 1)
            indices.add(result[2].toInt() - 1)
            indices.add(result[3].toInt() - 1)
        }

        FACE_PATTERN2.toRegex().findAll(text).forEach {
            val result = it.groups[0]!!.value.split(" ")

            indices.add(result[1].split("/")[0].toInt() - 1)
            indices.add(result[2].split("/")[0].toInt() - 1)
            indices.add(result[3].split("/")[0].toInt() - 1)
        }

        FACE_PATTERN3.toRegex().findAll(text).forEach {
            val result = it.groups[0]!!.value.split(" ")
            indices.add(result[1].split("/")[0].toInt() - 1)
            indices.add(result[2].split("/")[0].toInt() - 1)
            indices.add(result[3].split("/")[0].toInt() - 1)
        }

        FACE_PATTERN4.toRegex().findAll(text).forEach {
            val result = it.groups[0]!!.value.split(" ")
            indices.add(result[1].split("//")[0].toInt() - 1)
            indices.add(result[2].split("//")[0].toInt() - 1)
            indices.add(result[3].split("//")[0].toInt() - 1)
        }

        return BufferGeometry().apply {
            setIndex(IntBufferAttribute(indices.toIntArray(), 1))
            addAttribute("position", FloatBufferAttribute(vertices.toFloatArray(), 3))
            addAttribute("normal", FloatBufferAttribute(normals.toFloatArray(), 3))
        }

    }

}
