package info.laht.threekt.exporters

import info.laht.threekt.core.Object3D
import info.laht.threekt.math.Color
import info.laht.threekt.math.Matrix3
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.LineSegments
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points

class ObjExporter {

    private val vertex = Vector3()
    private val color = Color()
    private val normal = Vector3()
    private val uv = Vector2()

    private var indexVertex = 0
    private var indexVertexUvs = 0
    private var indexNormals = 0

    fun parse(obj: Object3D): String {
        val output = StringBuilder()
        obj.traverse {
            when (it) {
                is Mesh -> {
                    parseMesh(output, it)
                }
                is Line -> {
                    parseLine(output, it)
                }
                is Points -> {
                    parsePoints(output, it)
                }
            }
        }
        return output.toString()
    }

    private fun parseMesh(output: StringBuilder, mesh: Mesh) {
        val geometry = mesh.geometry
        val normalMatrixWorld = Matrix3()

        val vertices = geometry.attributes.position
        val normals = geometry.attributes.normal
        val uvs = geometry.attributes.uv
        val indices = geometry.index

        output.append("o ${mesh.name} \n")

        if (mesh.materials.isNotEmpty() && mesh.material.name.isNotEmpty()) {
            output.append("usemtl ${mesh.material.name}\n")
        }

        // vertices
        if (vertices != null) {
            for (i in 0 until vertices.count) {
                vertex.x = vertices.getX(i)
                vertex.y = vertices.getY(i)
                vertex.z = vertices.getZ(i) // transform the vertex to world space

                vertex.applyMatrix4(mesh.matrixWorld) // transform the vertex to export format

                output.append("v ${vertex.x} ${vertex.y} ${vertex.z}\n")
            }
        }

        // uvs
        if (uvs != null) {
            for (i in 0 until uvs.count) {
                uv.x = uvs.getX(i)
                uv.y = uvs.getY(i)

                output.append("vt ${uv.x} ${uv.y}\n")
            }
        }

        // normals
        if (normals != null) {
            normalMatrixWorld.getNormalMatrix(mesh.matrixWorld)

            for (i in 0 until normals.count) {
                normal.x = normals.getX(i)
                normal.y = normals.getY(i)
                normal.z = normals.getZ(i) // transform the normal to world space

                normal.applyMatrix4(mesh.matrixWorld).normalize() // transform the normal to export format

                output.append("vn ${normal.x} ${normal.y} ${normal.z}\n")
            }
        }

        // face indices
        if (indices == null) {
            checkNotNull(vertices) { "Vertices must not be null here." }

            for (i in 0 until vertices.count step 3) {
                output.append("f ")

                // transform the face to export format
                for (j in 0 until 3) {
                    val index = i + j + 1
                    output.append(indexVertex + index)
                    if (normals != null || uvs != null) {
                        output.append("/")
                        if (uvs != null) {
                            output.append(indexVertexUvs + index)
                        }
                        if (normals != null) {
                            output.append("/${indexNormals + index}")
                        }
                    }
                    if (j != 2) {
                        output.append(" ")
                    }
                }

                output.append("\n")
            }
        } else {
            for (i in 0 until indices.count step 3) {
                output.append("f ")

                // transform the face to export format
                for (j in 0 until 3) {
                    val index = indices.getX(i + j) + 1
                    output.append(indexVertex + index)
                    if (normals != null || uvs != null) {
                        output.append("/")
                        if (uvs != null) {
                            output.append(indexVertexUvs + index)
                        }
                        if (normals != null) {
                            output.append("/${indexNormals + index}")
                        }
                    }
                    if (j != 2) {
                        output.append(" ")
                    }
                }

                output.append("\n")
            }
        }

        indexVertex += vertices?.count ?: 0
        indexVertexUvs += uvs?.count ?: 0
        indexNormals += normals?.count ?: 0
    }

    private fun parseLine(output: StringBuilder, line: Line) {
        val geometry = line.geometry

        val vertices = geometry.attributes.position

        output.append("o ${line.name} \n")

        if (vertices != null) {
            for (i in 0 until vertices.count) {
                vertex.x = vertices.getX(i)
                vertex.y = vertices.getY(i)
                vertex.z = vertices.getZ(i) // transform the vertex to world space

                output.append("v ${vertex.x} ${vertex.y} ${vertex.z} \n")
            }

            if (line is LineSegments) {
                for(i in 1 until vertices.count step 2) {
                    val next = i + 1
                    output.append("l ${indexVertex + i} ${indexVertex + next}\n")
                }
            }
        }

        indexVertex += vertices?.count ?: 0
    }

    private fun parsePoints(output: StringBuilder, points: Points) {
        val geometry = points.geometry

        val vertices = geometry.attributes.position
        val colors = geometry.attributes.color

        output.append("o ${points.name}\n")

        if (vertices != null) {
            val vertexCount = vertices.count

            for(i in 0 until vertexCount) {
                vertex.x = vertices.getX(i)
                vertex.y = vertices.getY(i)
                vertex.z = vertices.getZ(i) // transform the vertex to world space

                vertex.applyMatrix4(points.matrixWorld)

                output.append("v ${vertex.x} ${vertex.y} ${vertex.z}")

                if (colors != null) {
                    color.set(colors.getX(i), colors.getY(i), colors.getZ(i))

                    output.append(" ${color.r} ${color.g} ${color.b}")
                }

                output.append("\n")
            }

            output.append("p ")

            for(i in 1..vertexCount) {
                output.append(indexVertex + i)
                if (i != vertexCount) {
                    output.append(" ")
                }
            }

            output.append("\n")

            indexVertex += vertexCount
        }
    }

}
