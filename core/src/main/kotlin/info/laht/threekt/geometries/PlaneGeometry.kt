package info.laht.threekt.geometries

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.IntBufferAttribute

class PlaneBufferGeometry(
        width: Number? = null,
        height: Number? = null,
        widthSegments: Int? = null,
        heightSegments: Int? = null
) : BufferGeometry() {

    val width = width?.toFloat() ?: 1f
    val height = height?.toFloat() ?: 1f
    val widthSegments = widthSegments ?: 1
    val heightSegments = heightSegments ?: 1

    constructor() : this(null)
    constructor(size: Number) : this(size, size)
    constructor(width: Number, height: Number) : this(width, height, null, null)

    init {

        val helper = PlaneBufferGeometryHelper()

        this.setIndex(IntBufferAttribute(helper.indices, 1))
        this.addAttribute("position", FloatBufferAttribute(helper.vertices, 3))
        this.addAttribute("normal", FloatBufferAttribute(helper.normals, 3))
        this.addAttribute("uv", FloatBufferAttribute(helper.uvs, 2))

    }

    private inner class PlaneBufferGeometryHelper {

        val indices: IntArray
        val vertices: FloatArray
        val normals: FloatArray
        val uvs: FloatArray

        init {

            val widthHalf = width / 2
            val heightHalf = height / 2

            val gridX = widthSegments
            val gridY = heightSegments

            val gridX1 = gridX + 1
            val gridY1 = gridY + 1

            val segmentWidth = width / gridX
            val segmentHeight = height / gridY

            vertices = FloatArray(gridX1 * gridY1 * 3)
            normals = FloatArray(gridX1 * gridY1 * 3)
            uvs = FloatArray(gridX1 * gridY1 * 2)

            var offset = 0
            var offset2 = 0

            for (iy in 0 until gridY1) {

                val y = iy * segmentHeight - heightHalf

                for (ix in 0 until gridX1) {

                    val x = ix * segmentWidth - widthHalf

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
