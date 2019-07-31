package info.laht.threekt.geometries

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.IntBufferAttribute
import info.laht.threekt.math.TWO_PI
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import kotlin.math.cos
import kotlin.math.sin


typealias CylinderGeometry = CylinderBufferGeometry

class CylinderBufferGeometry(
    val radiusTop: Float = 1f,
    val radiusBottom: Float = 1f,
    val height: Float = 1f,
    val radialSegments: Int = 32,
    val heightSegments: Int = 8,
    val openEnded: Boolean = false,
    val thetaStart: Float = 0f,
    val thetaLength: Float = TWO_PI
) : BufferGeometry() {

    constructor(radius: Float, height: Float) : this(radius, radius, height)

    init {

        val helper = CylinderBufferGeometryHelper()

        setIndex(helper.indices)
        addAttribute("position", helper.vertices)
        addAttribute("normal", helper.normals)
        addAttribute("uv", helper.uvs)

    }

    private inner class CylinderBufferGeometryHelper {

        internal var indices: IntBufferAttribute
        internal var vertices: FloatBufferAttribute
        internal var normals: FloatBufferAttribute
        internal var uvs: FloatBufferAttribute

        internal var index = 0
        internal var indexOffset = 0
        internal var indexArray: MutableList<List<Int>> = mutableListOf()
        internal var halfHeight = height / 2

        internal var groupStart = 0

        init {
            var nbCap = 0

            if (!openEnded) {
                if (radiusTop > 0) {
                    nbCap++
                }
                if (radiusBottom > 0) {
                    nbCap++
                }
            }

            val vertexCount = calculateVertexCount(nbCap)
            val indexCount = calculateIndexCount(nbCap)

            indices = IntBufferAttribute(IntArray(indexCount), 1)
            vertices = FloatBufferAttribute(FloatArray(vertexCount * 3), 3)
            normals = FloatBufferAttribute(FloatArray(vertexCount * 3), 3)
            uvs = FloatBufferAttribute(FloatArray(vertexCount * 2), 2)

            generateTorso()

            if (!openEnded) {
                if (radiusTop > 0) {
                    generateCap(true)
                }
                if (radiusBottom > 0) {
                    generateCap(false)
                }
            }

        }

        private fun calculateVertexCount(nbCap: Int): Int {
            var count = (radialSegments + 1) * (heightSegments + 1)
            if (!openEnded) {
                count += (radialSegments + 1) * nbCap + radialSegments * nbCap
            }
            return count
        }

        private fun calculateIndexCount(nbCap: Int): Int {
            var count = radialSegments * heightSegments * 2 * 3
            if (!openEnded) {
                count += radialSegments * nbCap * 3
            }
            return count
        }

        private fun generateTorso() {

            val normal = Vector3()
            val vertex = Vector3()

            var groupCount = 0

            // this will be used to calculate the normal
            val slope = (radiusBottom - radiusTop) / height

            // generate vertices, normals and uvs
            for (y in 0..heightSegments) {

                val indexRow = mutableListOf<Int>()

                val v = y.toFloat() / heightSegments

                // calculate the radius of the current row
                val radius = v * (radiusBottom - radiusTop) + radiusTop

                for (x in 0..radialSegments) {

                    val u = x.toFloat() / radialSegments

                    val theta = u * thetaLength + thetaStart

                    val sinTheta = sin(theta)
                    val cosTheta = cos(theta)

                    // vertex
                    vertex.x = radius * sinTheta
                    vertex.y = -v * height + halfHeight
                    vertex.z = radius * cosTheta
                    vertices.setXYZ(index, vertex.x, vertex.y, vertex.z)

                    // normal
                    normal.set(sinTheta, slope, cosTheta).normalize()
                    normals.setXYZ(index, normal.x, normal.y, normal.z)

                    // uv
                    uvs.setXY(index, u, 1 - v)

                    // save index of vertex in respective row
                    indexRow.add(index++)

                }

                // now save vertices of the row in our index array
                indexArray.add(indexRow)

            }

            // generate indices
            for (x in 0 until radialSegments) {

                for (y in 0 until heightSegments) {

                    // we use the index array to access the correct indices
                    val i1 = indexArray[y][x]
                    val i2 = indexArray[y + 1][x]
                    val i3 = indexArray[y + 1][x + 1]
                    val i4 = indexArray[y][x + 1]

                    // face one
                    indices.setX(indexOffset, i1)
                    indexOffset++
                    indices.setX(indexOffset, i2)
                    indexOffset++
                    indices.setX(indexOffset, i4)
                    indexOffset++

                    // face two
                    indices.setX(indexOffset, i2)
                    indexOffset++
                    indices.setX(indexOffset, i3)
                    indexOffset++
                    indices.setX(indexOffset, i4)
                    indexOffset++

                    // update counters
                    groupCount += 6

                }

            }

            // add a group to the geometry. this will ensure multi material support
            addGroup(groupStart, groupCount, 0)

            // calculate new start value for groups
            groupStart += groupCount
        }

        private fun generateCap(top: Boolean) {

            val uv = Vector2()
            val vertex = Vector3()

            var groupCount = 0

            val radius = if (top) radiusTop else radiusBottom
            val sign = if (top) 1 else -1

            // save the index of the first center vertex
            val centerIndexStart = index

            // first we generate the center vertex data of the cap.
            // because the geometry needs one set of uvs per face,
            // we must generate a center vertex per face/segment
            for (x in 1..radialSegments) {

                // vertex
                vertices.setXYZ(index, 0f, halfHeight * sign, 0f)

                // normal
                normals.setXYZ(index, 0f, sign.toFloat(), 0f)

                // uv
                uv.x = 0.5f
                uv.y = 0.5f

                uvs.setXY(index, uv.x, uv.y)

                // increase index
                index++

            }

            // save the index of the last center vertex
            val centerIndexEnd = index

            // now we generate the surrounding vertices, normals and uvs
            for (x in 0..radialSegments) {

                val u = x.toFloat() / radialSegments
                val theta = u * thetaLength + thetaStart

                val cosTheta = cos(theta)
                val sinTheta = sin(theta)

                // vertex
                vertex.x = radius * sinTheta
                vertex.y = halfHeight * sign
                vertex.z = radius * cosTheta
                vertices.setXYZ(index, vertex.x, vertex.y, vertex.z)

                // normal
                normals.setXYZ(index, 0f, sign.toFloat(), 0f)

                // uv
                uv.x = cosTheta * 0.5f + 0.5f
                uv.y = sinTheta * 0.5f * sign.toFloat() + 0.5f
                uvs.setXY(index, uv.x, uv.y)

                // increase index
                index++

            }

            // generate indices
            for (x in 0 until radialSegments) {

                val c = centerIndexStart + x
                val i = centerIndexEnd + x

                if (top) {

                    // face top
                    indices.setX(indexOffset, i)
                    indexOffset++
                    indices.setX(indexOffset, i + 1)
                    indexOffset++
                    indices.setX(indexOffset, c)
                    indexOffset++

                } else {

                    // face bottom
                    indices.setX(indexOffset, i + 1)
                    indexOffset++
                    indices.setX(indexOffset, i)
                    indexOffset++
                    indices.setX(indexOffset, c)
                    indexOffset++

                }

                // update counters
                groupCount += 3

            }

            // add a group to the geometry. this will ensure multi material support
            addGroup(groupStart, groupCount, if (top) 1 else 2)

            // calculate new start value for groups
            groupStart += groupCount
        }

    }


}