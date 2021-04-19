package info.laht.threekt.geometries

import info.laht.threekt.add
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.IntBufferAttribute
import info.laht.threekt.math.Curve3
import info.laht.threekt.math.TWO_PI
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import kotlin.math.cos
import kotlin.math.sin

class TubeBufferGeometry @JvmOverloads constructor(
        val path: Curve3,
        val tubularSegments: Int = 64,
        val radius: Float = 1f,
        val radialSegments: Int = 8,
        val closed: Boolean = false
) : BufferGeometry() {

    val frames = path.computeFrenetFrames(tubularSegments, closed)

    init {

        val vertex = Vector3()
        val normal = Vector3()
        val uv = Vector2()
        val P = Vector3()

        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()
        val indices = mutableListOf<Int>()

        fun generateSegment(i: Int) {

            // we use getPointAt to sample evenly distributed points from the given path

            path.getPointAt(i.toFloat() / tubularSegments, P)

            // retrieve corresponding normal and binormal

            val N = frames.normals[i]
            val B = frames.binormals[i]

            // generate normals and vertices for the current segment

            for (j in 0..radialSegments) {

                val v = j.toFloat() / radialSegments * TWO_PI

                val sin = sin(v)
                val cos = -cos(v)

                // normal

                normal.x = (cos * N.x + sin * B.x)
                normal.y = (cos * N.y + sin * B.y)
                normal.z = (cos * N.z + sin * B.z)
                normal.normalize()

                normals.add(normal.x, normal.y, normal.z)

                // vertex

                vertex.x = P.x + radius * normal.x
                vertex.y = P.y + radius * normal.y
                vertex.z = P.z + radius * normal.z

                vertices.add(vertex.x, vertex.y, vertex.z)

            }

        }

        fun generateIndices() {

            for (j in 1..tubularSegments) {

                for (i in 1..radialSegments) {

                    val a = (radialSegments + 1) * (j - 1) + (i - 1)
                    val b = (radialSegments + 1) * j + (i - 1)
                    val c = (radialSegments + 1) * j + i
                    val d = (radialSegments + 1) * (j - 1) + i

                    // faces

                    indices.add(a, b, d)
                    indices.add(b, c, d)

                }

            }

        }

        fun generateUVs() {

            for (i in 0 until tubularSegments) {

                for (j in 0..radialSegments) {

                    uv.x = i.toFloat() / tubularSegments
                    uv.y = j.toFloat() / radialSegments

                    uvs.add(uv.x, uv.y)

                }

            }

        }

        fun generateBufferData() {

            for (i in 0 until tubularSegments) {

                generateSegment(i)

            }

            // if the geometry is not closed, generate the last row of vertices and normals
            // at the regular position on the given path
            //
            // if the geometry is closed, duplicate the first row of vertices and normals (uvs will differ)

            generateSegment(if (!closed) tubularSegments else 0)

            // uvs are generated in a separate function.
            // this makes it easy compute correct values for closed geometries

            generateUVs()

            // finally create faces

            generateIndices()

        }

        generateBufferData()

        setIndex(IntBufferAttribute(indices.toIntArray(), 1))
        addAttribute("position", FloatBufferAttribute(vertices.toFloatArray(), 3))
        addAttribute("normal", FloatBufferAttribute(normals.toFloatArray(), 3))
        addAttribute("uv", FloatBufferAttribute(uvs.toFloatArray(), 3))

    }

}
