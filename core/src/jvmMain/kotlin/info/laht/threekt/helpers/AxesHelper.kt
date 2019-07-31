package info.laht.threekt.helpers

import info.laht.threekt.Colors
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.objects.LineSegments

class AxesHelper(
    size: Number = 1f
) : LineSegments() {

    init {

        val vertices = floatArrayOf(
            0f, 0f, 0f,	size.toFloat(), 0f, 0f,
            0f, 0f, 0f,	0f, size.toFloat(), 0f,
            0f, 0f, 0f,	0f, 0f, size.toFloat()
        )

        val colors = floatArrayOf(
            1f, 0f, 0f,	1f, 0.6f, 0f,
            0f, 1f, 0f,	0.6f, 1f, 0f,
            0f, 0f, 1f,	0f, 0.6f, 1f
        )

        geometry.addAttribute("position", FloatBufferAttribute(vertices, 3))
        geometry.addAttribute("color", FloatBufferAttribute(colors, 3))

        material.vertexColors = Colors.Vertex

    }

}
