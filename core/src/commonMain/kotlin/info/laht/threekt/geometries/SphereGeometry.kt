package info.laht.threekt.geometries

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.math.Sphere
import info.laht.threekt.math.TWO_PI
import info.laht.threekt.math.Vector3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SphereBufferGeometry(
        radius: Number? = null,
        widthSegments: Int? = null,
        heightSegments: Int? = null,
        phiStart: Number? = null,
        phiLength: Number? = null,
        thetaStart: Number? = null,
        thetaLength: Number? = null
) : BufferGeometry() {

    val radius = radius?.toFloat() ?: 0.5f
    val widthSegments = widthSegments ?: 32
    val heightSegments = heightSegments ?: 32
    val phiStart = phiStart?.toFloat() ?: 0f
    val phiLength = phiLength?.toFloat() ?: TWO_PI
    val thetaStart = thetaStart?.toFloat() ?: 0f
    val thetaLength = thetaLength?.toFloat() ?: PI.toFloat()

    constructor(radius: Number) : this(radius, null, null, null, null, null, null)
    constructor(radius: Number, widthSegments: Int, heightSegments: Int) : this(radius, widthSegments, heightSegments, null, null, null, null)

    init {

        val helper = SphereBufferGeometryHelper()

        setIndex(helper.indices.toIntArray())
        addAttribute("position", helper.positions)
        addAttribute("normal", helper.normals)
        addAttribute("uvs", helper.uvs)

        boundingSphere = Sphere(Vector3(), this.radius)

    }

    private inner class SphereBufferGeometryHelper {

        val indices: MutableList<Int>
        val positions: FloatBufferAttribute
        val normals: FloatBufferAttribute
        val uvs: FloatBufferAttribute

        init {
            val thetaEnd = thetaStart + thetaLength

            val vertexCount = (widthSegments + 1) * (heightSegments + 1)

            positions = FloatBufferAttribute(FloatArray(vertexCount * 3), 3)
            normals = FloatBufferAttribute(FloatArray(vertexCount * 3), 3)
            uvs = FloatBufferAttribute(FloatArray(vertexCount * 2), 2)

            var index = 0
            val normal = Vector3()

            val vertices = mutableListOf<List<Int>>()

            for (y in 0..heightSegments) {

                val verticesRow = mutableListOf<Int>()
                val v = y.toFloat() / heightSegments

                for (x in 0..widthSegments) {

                    val u = x.toFloat() / widthSegments

                    val px =
                            -radius * cos(phiStart + u * phiLength) * sin(thetaStart + v * thetaLength)
                    val py = radius * cos(thetaStart + v * thetaLength)
                    val pz =
                            radius * sin(phiStart + u * phiLength) * sin(thetaStart + v * thetaLength)

                    normal.set(px, py, pz).normalize()

                    positions.setXYZ(index, px, py, pz)
                    normals.setXYZ(index, normal.x, normal.y, normal.z)
                    uvs.setXY(index, u, 1 - v)

                    verticesRow.add(index)

                    index++

                }

                vertices.add(verticesRow)

            }

            indices = mutableListOf()

            for (y in 0 until heightSegments) {

                for (x in 0 until widthSegments) {

                    val v1 = vertices[y][x + 1]
                    val v2 = vertices[y][x]
                    val v3 = vertices[y + 1][x]
                    val v4 = vertices[y + 1][x + 1]

                    if (y != 0 || thetaStart > 0) {
                        indices.add(v1)
                        indices.add(v2)
                        indices.add(v4)
                    }
                    if (y != heightSegments - 1 || thetaEnd < PI) {
                        indices.add(v2)
                        indices.add(v3)
                        indices.add(v4)
                    }

                }

            }
        }

    }

}
