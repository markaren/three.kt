package info.laht.threekt.helpers

import info.laht.threekt.Colors
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.LineSegments

class GridHelper(
    size: Int = 10,
    divisions: Int = 10,
    color1: Color = Color(0x444444),
    color2: Color = Color(0x888888)
): LineSegments(create(size, divisions, color1, color2), LineBasicMaterial().apply { vertexColors = Colors.Vertex }) {

    private companion object {

        fun create(size: Int, divisions: Int, color1: Color, color2: Color): BufferGeometry {
            val center = divisions / 2
            val step = size / divisions
            val halfSize = size.toFloat() / 2

            val vertices = mutableListOf<Float>()
            val colors = FloatArray((divisions+1)*12)

            var i = 0
            var j = 0
            var k = -halfSize
            while (i <= divisions) {

                vertices.add(-halfSize)
                vertices.add(0f)
                vertices.add(k)
                vertices.add(halfSize)
                vertices.add(0f)
                vertices.add(k)

                vertices.add(k)
                vertices.add(0f)
                vertices.add(-halfSize)
                vertices.add(k)
                vertices.add(0f)
                vertices.add(halfSize)

                val color = if (i == center) color1 else color2
                color.toArray(colors, j)
                j += 3
                color.toArray(colors, j)
                j += 3
                color.toArray(colors, j)
                j += 3
                color.toArray(colors, j)
                j += 3

                i++
                k += step

            }

            val geometry = BufferGeometry()
            geometry.addAttribute("position", FloatBufferAttribute(vertices.toFloatArray(), 3))
            geometry.addAttribute("color", FloatBufferAttribute(colors, 3))

            return geometry
        }

    }

}