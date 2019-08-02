package info.laht.threekt.loaders

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.math.Vector3
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.regex.Pattern

class STLLoader {

    fun load(path: String): BufferGeometry {
        return parseBinary(File(path).readBytes())
    }

    private fun parseBinary(data: ByteArray): BufferGeometry {

        val reader = ByteBuffer.wrap(data)
        reader.order(ByteOrder.LITTLE_ENDIAN)
        val faces = reader.getInt(80)

        var colors: MutableList<Float>? = null
        var defaultR = 0f
        var defaultG = 0f
        var defaultB = 0f
        var alpha = 1f
        var r = 0f
        var g = 0f
        var b = 0f

        for (index in 0 until 80 - 10) {

            if (reader.getInt(index) == 0x434F4C4F
                && reader.get(index + 5).toInt() == 0x52
                && reader.get(index + 5).toInt() == 0x3D
            ) {

                colors = mutableListOf()
                defaultR = (reader.get(index + 6)).toFloat() / 255
                defaultG = (reader.get(index + 7)).toFloat() / 255
                defaultB = (reader.get(index + 8)).toFloat() / 255
                alpha = (reader.get(index + 9)).toFloat() / 255

            }

        }

        val dataOffset = 84
        val faceLength = 12 * 4 + 2

        val geometry = BufferGeometry()

        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()

        for (face in 0 until faces) {

            val start = dataOffset + face * faceLength
            val normalX = reader.getFloat(start)
            val normalY = reader.getFloat(start + 4)
            val normalZ = reader.getFloat(start + 8)

            if (colors != null) {
                val packedColor = reader.getChar(start + 48).toInt()

                if (packedColor and 0x8000 == 0) {

                    // facet has its own unique color
                    r = ((packedColor and 0x1F) / 31).toFloat()
                    g = ((packedColor shr 5 and 0x1F) / 31).toFloat()
                    b = ((packedColor shr 10 and 0x1F) / 31).toFloat()

                } else {

                    r = defaultR
                    g = defaultG
                    b = defaultB

                }
            }

            for (i in 1..3) {

                val vertexstart = start + i * 12

                vertices.add(reader.getFloat(vertexstart))
                vertices.add(reader.getFloat(vertexstart + 4))
                vertices.add(reader.getFloat(vertexstart + 8))

                normals.add(normalX)
                normals.add(normalY)
                normals.add(normalZ)

                colors?.apply {
                    add(r)
                    add(g)
                    add(b)
                }

            }

        }

        geometry.addAttribute("position", FloatBufferAttribute(vertices.toFloatArray(), 3))
        geometry.addAttribute("normal", FloatBufferAttribute(normals.toFloatArray(), 3))

        colors?.also {
            geometry.addAttribute("color", FloatBufferAttribute(it.toFloatArray(), 3))
        }

        return geometry

    }

    private fun parseAscii(data: String): BufferGeometry {

        val patternFace = "facet([\\s\\S]*?)endfacet"
        val patternNormal =
            "normal[\\s]+([\\-+]?[0-9]+\\.?[0-9]*([eE][\\-+]?[0-9]+)?)+[\\s]+([\\-+]?[0-9]*\\.?[0-9]+([eE][\\-+]?[0-9]+)?)+[\\s]+([\\-+]?[0-9]*\\.?[0-9]+([eE][\\-+]?[0-9]+)?)+"
        val patternVertex =
            "vertex[\\s]+([\\-+]?[0-9]+\\.?[0-9]*([eE][\\-+]?[0-9]+)?)+[\\s]+([\\-+]?[0-9]*\\.?[0-9]+([eE][\\-+]?[0-9]+)?)+[\\s]+([\\-+]?[0-9]*\\.?[0-9]+([eE][\\-+]?[0-9]+)?)+"

        val geometry = BufferGeometry()

        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val normal = Vector3()

        val facePattern = Pattern.compile(patternFace)
        val faceMatcher = facePattern.matcher(data)
        while (faceMatcher.find()) {

            var group = faceMatcher.group()
            val normalPattern = Pattern.compile(patternNormal)
            val normalMatcher = normalPattern.matcher(group)
            while (normalMatcher.find()) {
                val normalGroup = normalMatcher.group()
                val split = normalGroup.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                normal.set(
                    split[1].toFloat(),
                    split[2].toFloat(),
                    split[3].toFloat()
                )
            }

            group = faceMatcher.group()
            val vertexPattern = Pattern.compile(patternVertex)
            val vertexMatcher = vertexPattern.matcher(group)
            while (vertexMatcher.find()) {
                val vertexGroup = vertexMatcher.group()
                val split = vertexGroup.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }

                vertices.add(split[1].toFloat())
                vertices.add(split[2].toFloat())
                vertices.add(split[3].toFloat())

                normals.add(normal.x)
                normals.add(normal.y)
                normals.add(normal.z)

            }
        }

        geometry.addAttribute("position", FloatBufferAttribute(vertices.toFloatArray(), 3))
        geometry.addAttribute("normal", FloatBufferAttribute(normals.toFloatArray(), 3))

        return geometry

    }

}