package info.laht.threekt.geometries

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import info.laht.threekt.push
import kotlin.math.*

@Suppress("NAME_SHADOWING")
open class PolyhedronBufferGeometry(
        private val vertices: FloatArray,
        private val indices: IntArray,
        radius: Float? = null,
        detail: Int? = null
) : BufferGeometry() {

    private val vertexBuffer = mutableListOf<Float>()
    private val uvBuffer = mutableListOf<Float>()

    init {

        val radius = radius ?: 1f
        val detail = detail ?: 0

        subdivide(detail)

        applyRadius(radius)

        generateUVs()

        addAttribute("position", FloatBufferAttribute(vertexBuffer.toFloatArray(), 3))
        addAttribute("normal", FloatBufferAttribute(vertexBuffer.toFloatArray(), 3))
        addAttribute("uv", FloatBufferAttribute(uvBuffer.toFloatArray(), 2))

        if (detail == 0) {
            computeVertexNormals()
        } else {
            normalizeNormals()
        }

    }

    private fun subdivide(detail: Int) {

        val a = Vector3()
        val b = Vector3()
        val c = Vector3()

        // iterate over all faces and apply a subdivison with the given detail value

        var i = 0
        while (i < indices.size) {

            // get the vertices of the face

            getVertexByIndex(indices[i + 0], a)
            getVertexByIndex(indices[i + 1], b)
            getVertexByIndex(indices[i + 2], c)

            // perform subdivision

            subdivideFace(a, b, c, detail)

            i += 3

        }

    }


    private fun subdivideFace(a: Vector3, b: Vector3, c: Vector3, detail: Int) {

        val cols = 2f.pow(detail).toInt()

        // we use this multidimensional array as a data structure for creating the subdivision

        val v = mutableListOf<MutableList<Vector3>>()

        // construct all of the vertices for this subdivision

        for (i in 0..cols) {

            v.add(mutableListOf())

            val aj = a.clone().lerp(c, i.toFloat() / cols)
            val bj = b.clone().lerp(c, i.toFloat() / cols)

            val rows = cols - i

            for (j in 0..rows) {

                if (j == 0 && i == cols) {

                    v[i].push(aj)

                } else {

                    v[i].push(aj.clone().lerp(bj, j.toFloat() / rows))

                }

            }

        }

        // construct all of the faces

        for (i in 0 until cols) {

            for (j in 0 until 2 * (cols - i) - 1) {

                val k = floor(j.toFloat() / 2).toInt()

                if (j % 2 == 0) {

                    pushVertex(v[i][k + 1])
                    pushVertex(v[i + 1][k])
                    pushVertex(v[i][k])

                } else {

                    pushVertex(v[i][k + 1]);
                    pushVertex(v[i + 1][k + 1]);
                    pushVertex(v[i + 1][k]);

                }

            }

        }

    }

    private fun applyRadius(radius: Float) {

        val vertex = Vector3()

        // iterate over the entire buffer and apply the radius to each vertex

        var i = 0
        while (i < vertexBuffer.size) {

            vertex.x = vertexBuffer[i + 0]
            vertex.y = vertexBuffer[i + 1]
            vertex.z = vertexBuffer[i + 2]

            vertex.normalize().multiplyScalar(radius)

            vertexBuffer[i + 0] = vertex.x
            vertexBuffer[i + 1] = vertex.y
            vertexBuffer[i + 2] = vertex.z

            i += 3

        }

    }

    private fun generateUVs() {

        val vertex = Vector3()

        var i = 0
        while (i < vertexBuffer.size) {

            vertex.x = vertexBuffer[i + 0]
            vertex.y = vertexBuffer[i + 1]
            vertex.z = vertexBuffer[i + 2]

            val u = azimuth(vertex) / 2 / PI.toFloat() + 0.5f
            val v = inclination(vertex) / PI.toFloat() + 0.5f
            uvBuffer.push(u, 1 - v);

            i += 3

        }

        correctUVs();

        correctSeam();

    }

    private fun correctSeam() {

        // handle case when face straddles the seam, see #3269

        var i = 0
        while (i < uvBuffer.size) {

            // uv data of a single face

            val x0 = uvBuffer[i + 0]
            val x1 = uvBuffer[i + 2]
            val x2 = uvBuffer[i + 4]

            val max = max(x0, max(x1, x2))
            val min = min(x0, min(x1, x2))

            // 0.9 is somewhat arbitrary

            if (max > 0.9 && min < 0.1) {

                if (x0 < 0.2) uvBuffer[i + 0] = uvBuffer[i + 0] + 1
                if (x1 < 0.2) uvBuffer[i + 2] = uvBuffer[i + 2] + 1
                if (x2 < 0.2) uvBuffer[i + 4] = uvBuffer[i + 4] + 1

            }

            i += 6

        }

    }

    private fun pushVertex(vertex: Vector3) {
        vertexBuffer.push(vertex.x, vertex.y, vertex.z)
    }

    private fun getVertexByIndex(index: Int, vertex: Vector3) {

        val stride = index * 3

        vertex.x = vertices[stride + 0]
        vertex.y = vertices[stride + 1]
        vertex.z = vertices[stride + 2]

    }

    private fun correctUVs() {

        val a = Vector3()
        val b = Vector3()
        val c = Vector3();

        val centroid = Vector3()

        val uvA = Vector2()
        val uvB = Vector2()
        val uvC = Vector2()

        var i = 0
        var j = 0
        while (i < vertexBuffer.size) {

            a.set(vertexBuffer[i + 0], vertexBuffer[i + 1], vertexBuffer[i + 2])
            b.set(vertexBuffer[i + 3], vertexBuffer[i + 4], vertexBuffer[i + 5])
            c.set(vertexBuffer[i + 6], vertexBuffer[i + 7], vertexBuffer[i + 8])

            uvA.set(uvBuffer[j + 0], uvBuffer[j + 1])
            uvB.set(uvBuffer[j + 2], uvBuffer[j + 3])
            uvC.set(uvBuffer[j + 4], uvBuffer[j + 5])

            centroid.copy(a).add(b).add(c).divideScalar(3f)

            val azi = azimuth(centroid)

            correctUV(uvA, j + 0, a, azi)
            correctUV(uvB, j + 2, b, azi)
            correctUV(uvC, j + 4, c, azi)

            i += 9
            j += 6

        }

    }

    private fun correctUV(uv: Vector2, stride: Int, vector: Vector3, azimuth: Float) {

        if ((azimuth < 0) && (uv.x == 1f)) {

            uvBuffer[stride] = uv.x - 1

        }

        if ((vector.x == 0f) && (vector.z == 0f)) {

            uvBuffer[stride] = azimuth / 2f / PI.toFloat() + 0.5f

        }

    }

    private fun azimuth(vector: Vector3): Float {

        return atan2(vector.z, -vector.x)

    }


    // Angle above the XZ plane.

    private fun inclination(vector: Vector3): Float {

        return atan2(-vector.y, sqrt((vector.x * vector.x) + (vector.z * vector.z)));

    }

}
