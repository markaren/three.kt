package info.laht.threekt.geometries

import info.laht.threekt.core.BufferGeometry
import java.util.ArrayList
import info.laht.threekt.core.DoubleBufferAttribute
import info.laht.threekt.math.Vector3
import kotlin.math.cos
import kotlin.math.sin
import info.laht.threekt.core.BufferAttribute
import info.laht.threekt.core.IntBufferAttribute
import info.laht.threekt.math.Sphere


class SphereBufferGeometry(
    val radius: Double,
    val widthSegments: Int,
    val heightSegments: Int,
    val phiStart: Double,
    val phiLength: Double,
    val thetaStart: Double,
    val thetaLength: Double
): BufferGeometry() {

    init {

        val helper = SphereBufferGeometryHelper()

        setIndex(IntBufferAttribute(helper.indices.toIntArray(), 1))
        addAttribute("position", helper.positions)
        addAttribute("normal", helper.normals)
        addAttribute("uvs", helper.uvs)

        this.boundingSphere = Sphere(Vector3(), radius)

    }

    private inner class SphereBufferGeometryHelper {

        internal var indices: MutableList<Int>
        internal var positions: DoubleBufferAttribute
        internal var normals: DoubleBufferAttribute
        internal var uvs: DoubleBufferAttribute

        init {
            val thetaEnd = thetaStart + thetaLength

            val vertexCount = (widthSegments + 1) * (heightSegments + 1)

            positions = DoubleBufferAttribute(DoubleArray(vertexCount * 3), 3)
            normals = DoubleBufferAttribute(DoubleArray(vertexCount * 3), 3)
            uvs = DoubleBufferAttribute(DoubleArray(vertexCount * 2), 2)

            var index = 0
            val normal = Vector3()

            val vertices = ArrayList<List<Int>>()

            for (y in 0..heightSegments) {

                val verticesRow = ArrayList<Int>()
                val v = y.toDouble() / heightSegments

                for (x in 0..widthSegments) {

                    val u = x.toDouble() / widthSegments

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

            indices = ArrayList()

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
                    if (y != heightSegments - 1 || thetaEnd < Math.PI) {
                        indices.add(v2)
                        indices.add(v3)
                        indices.add(v4)
                    }

                }

            }
        }

    }
    
}