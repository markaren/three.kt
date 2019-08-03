package info.laht.threekt.loaders

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.IntBufferAttribute
import java.io.File
import java.util.regex.Pattern

private val VERTEX_PATTERN = "v( +[\\d|\\.|\\+|\\-|e|E]+)( +[\\d|\\.|\\+|\\-|e|E]+)( +[\\d|\\.|\\+|\\-|e|E]+)"
private val NORMAL_PATTERN = "vn( +[\\d|\\.|\\+|\\-|e|E]+)( +[\\d|\\.|\\+|\\-|e|E]+)( +[\\d|\\.|\\+|\\-|e|E]+)"

// f vertex vertex vertex ...
private val FACE_PATTERN1 = "f( +-?\\d+)( +-?\\d+)( +-?\\d+)( +-?\\d+)?"

// f vertex/uv vertex/uv vertex/uv ...
private val FACE_PATTERN2 =
    "f( +(-?\\d+)\\/(-?\\d+))( +(-?\\d+)\\/(-?\\d+))( +(-?\\d+)\\/(-?\\d+))( +(-?\\d+)\\/(-?\\d+))?"

// f vertex/uv/normal vertex/uv/normal vertex/uv/normal ...
private val FACE_PATTERN3 =
    "f( +(-?\\d+)\\/(-?\\d+)\\/(-?\\d+))( +(-?\\d+)\\/(-?\\d+)\\/(-?\\d+))( +(-?\\d+)\\/(-?\\d+)\\/(-?\\d+))( +(-?\\d+)\\/(-?\\d+)\\/(-?\\d+))?"

// f vertex//normal vertex//normal vertex//normal ...
private val FACE_PATTERN4 =
    "f( +(-?\\d+)\\/\\/(-?\\d+))( +(-?\\d+)\\/\\/(-?\\d+))( +(-?\\d+)\\/\\/(-?\\d+))( +(-?\\d+)\\/\\/(-?\\d+))?"

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

        run {
            val compile = Pattern.compile(VERTEX_PATTERN)
            val matcher = compile.matcher(text)

            while (matcher.find()) {
                val result = matcher.group().replace("  ", " ").split(" ")
                vertices.add(result[1].toFloat())
                vertices.add(result[2].toFloat())
                vertices.add(result[3].toFloat())
            }
        }

        run {
            val compile = Pattern.compile(NORMAL_PATTERN)
            val matcher = compile.matcher(text)

            while (matcher.find()) {
                val result = matcher.group().replace("  ", " ").split(" ")
                normals.add(result[1].toFloat())
                normals.add(result[2].toFloat())
                normals.add(result[3].toFloat())
            }
        }

        run {
            val compile = Pattern.compile(FACE_PATTERN1)
            val matcher = compile.matcher(text)

            while (matcher.find()) {
                val result = matcher.group().split(" ")
                indices.add(result[1].toInt() - 1)
                indices.add(result[2].toInt() - 1)
                indices.add(result[3].toInt() - 1)
            }

        }

        run {
            val compile = Pattern.compile(FACE_PATTERN2)
            val matcher = compile.matcher(text)

            while (matcher.find()) {
                val result = matcher.group().split(" ")
                indices.add(result[1].split("/".toRegex()).dropLastWhile({ it.isEmpty() })[0].toInt() - 1)
                indices.add(result[2].split("/".toRegex()).dropLastWhile({ it.isEmpty() })[0].toInt() - 1)
                indices.add(result[3].split("/".toRegex()).dropLastWhile({ it.isEmpty() })[0].toInt() - 1)
            }

        }

        run {
            val compile = Pattern.compile(FACE_PATTERN3)
            val matcher = compile.matcher(text)

            while (matcher.find()) {
                val result = matcher.group().split(" ")
                indices.add(result[1].split("/".toRegex()).dropLastWhile({ it.isEmpty() })[0].toInt() - 1)
                indices.add(result[2].split("/".toRegex()).dropLastWhile({ it.isEmpty() })[0].toInt() - 1)
                indices.add(result[3].split("/".toRegex()).dropLastWhile({ it.isEmpty() })[0].toInt() - 1)
            }

        }

        run {
            val compile = Pattern.compile(FACE_PATTERN4)
            val matcher = compile.matcher(text)

            while (matcher.find()) {
                val result = matcher.group().split(" ")
                indices.add(result[1].split("//".toRegex()).dropLastWhile({ it.isEmpty() })[0].toInt() - 1)
                indices.add(result[2].split("//".toRegex()).dropLastWhile({ it.isEmpty() })[0].toInt() - 1)
                indices.add(result[3].split("//".toRegex()).dropLastWhile({ it.isEmpty() })[0].toInt() - 1)
            }

        }

        return BufferGeometry().apply {
            setIndex(IntBufferAttribute(indices.toIntArray(), 1))
            addAttribute("position", FloatBufferAttribute(vertices.toFloatArray(), 3))
            addAttribute("normal", FloatBufferAttribute(normals.toFloatArray(), 3))
        }

    }

}
