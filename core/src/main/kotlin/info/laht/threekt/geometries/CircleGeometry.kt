package info.laht.threekt.geometries

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.math.TWO_PI
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import info.laht.threekt.push
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class CircleBufferGeometry @JvmOverloads constructor(
    radius: Number? = null,
    segments: Int? = null,
    thetaStart: Number? = null,
    thetaLength: Number? = null
) : BufferGeometry() {

    val radius: Float = radius?.toFloat() ?: 1f
    val segments: Int = if (segments != null) max(3, segments) else 16
    val thetaStart: Float = thetaStart?.toFloat() ?: 0f
    val thetaLength: Float = thetaLength?.toFloat() ?: TWO_PI

    init {

        val helper = CircleBufferGeometryHelper()

        setIndex(helper.indices.toIntArray())
        addAttribute("position", FloatBufferAttribute(helper.vertices.toFloatArray(), 3))
        addAttribute("normal", FloatBufferAttribute(helper.normals.toFloatArray(), 3))
        addAttribute("uv", FloatBufferAttribute(helper.uvs.toFloatArray(), 2))

    }

    private inner class CircleBufferGeometryHelper {

        val indices = mutableListOf<Int>()
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()

        init {

            val vertex = Vector3()
            val uv = Vector2()

            // center point

            vertices.push(0f, 0f, 0f)
            normals.push(0f, 0f, 1f)
            uvs.push(0.5f, 0.5f)

            var s = 0
            var i = 3
            while (s <= segments) {

                val segment = thetaStart + s / segments.toFloat() * thetaLength

                // vertex

                vertex.x = radius * cos(segment)
                vertex.y = radius * sin(segment)

                vertices.push(vertex.x, vertex.y, vertex.z)

                // normal

                normals.push(0f, 0f, 1f)

                // uvs

                uv.x = (vertices[i] / radius + 1) / 2f
                uv.y = (vertices[i + 1] / radius + 1) / 2f

                uvs.push(uv.x, uv.y)

                s++
                i += 3
            }

            // indices

            for (j in 1..segments) {

                indices.push(j, j + 1, 0)

            }

        }

    }

}
