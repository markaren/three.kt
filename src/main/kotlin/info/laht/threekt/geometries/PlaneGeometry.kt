package info.laht.threekt.geometries

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.IntBufferAttribute

typealias PlaneGeometry = PlaneBufferGeometry

class PlaneBufferGeometry(
    val width: Float = 1f,
    val height: Float = 1f,
    val widthSegments: Int = 1,
    val heightSegments: Int = 1
) : BufferGeometry() {

    init {

        val helper = PlaneBufferGeometryHelper()

        this.setIndex(IntBufferAttribute(helper.indices, 1))
        this.addAttribute("position", FloatBufferAttribute(helper.vertices, 3))
        this.addAttribute("normal", FloatBufferAttribute(helper.normals, 3))
        this.addAttribute("uv", FloatBufferAttribute(helper.uvs, 2))

    }

    private inner class PlaneBufferGeometryHelper {

        internal var indices: IntArray
        internal var vertices: FloatArray
        internal var normals: FloatArray
        internal var uvs: FloatArray

        init {

            val width_half = width / 2
            val height_half = height / 2

            val gridX = widthSegments
            val gridY = heightSegments

            val gridX1 = gridX + 1
            val gridY1 = gridY + 1

            val segment_width = width / gridX
            val segment_height = height / gridY

            vertices = FloatArray(gridX1 * gridY1 * 3)
            normals = FloatArray(gridX1 * gridY1 * 3)
            uvs = FloatArray(gridX1 * gridY1 * 2)

            var offset = 0
            var offset2 = 0

            for (iy in 0 until gridY1) {

                val y = iy * segment_height - height_half

                for (ix in 0 until gridX1) {

                    val x = ix * segment_width - width_half

                    vertices[offset + 0] = x
                    vertices[offset + 1] = -y

                    normals[offset + 2] = 1f

                    uvs[offset2 + 0] = (ix / gridX).toFloat()
                    uvs[offset2 + 1] = (1 - iy / gridY).toFloat()

                    offset += 3
                    offset2 += 2

                }

            }

            offset = 0

            indices = IntArray(gridX * gridY * 6)

            for (iy in 0 until gridY) {

                for (ix in 0 until gridX) {

                    val a = ix + gridX1 * iy
                    val b = ix + gridX1 * (iy + 1)
                    val c = ix + 1 + gridX1 * (iy + 1)
                    val d = ix + 1 + gridX1 * iy

                    indices[offset + 0] = a
                    indices[offset + 1] = b
                    indices[offset + 2] = d

                    indices[offset + 3] = b
                    indices[offset + 4] = c
                    indices[offset + 5] = d

                    offset += 6

                }

            }

        }

    }

}
