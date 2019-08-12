package info.laht.threekt.loaders

import info.laht.threekt.Colors
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.Object3D
import info.laht.threekt.length
import info.laht.threekt.materials.*
import info.laht.threekt.objects.Group
import info.laht.threekt.objects.LineSegments
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points
import info.laht.threekt.push
import info.laht.threekt.splice
import java.io.File

class OBJLoader(
        val tryLoadMtl: Boolean = true
) {

    var materials: MTLLoader.MaterialCreator? = null

    fun load(path: String): Group {

        val file = File(path)

        if (tryLoadMtl) {
            val mtlFile = File(file.parent, "${file.nameWithoutExtension}.mtl")
            if (mtlFile.exists()) {
                materials = MTLLoader().load(mtlFile.absolutePath).preload()
            }
        }

        return parse(file.readText())
    }

    fun parse(text: String): Group {

        @Suppress("NAME_SHADOWING")
        var text = text
        val state = ParserState()

        if (text.indexOf("\r\n") != -1) {

            // This is faster than String.split with regex that splits on both
            text = text.replace("\r\n".toRegex(), "\n")

        }

        if (text.indexOf("\\\n") != -1) {

            // join lines separated by a line continuation character (\)
            text = text.replace("\\\n".toRegex(), "")

        }

        val lines = text.split("\n")
        var line: String
        var lineFirstChar: Char
        var result: List<String>

        for (i in 0 until lines.size) {

            line = lines[i].trimStart()

            val lineLength = line.length

            if (lineLength == 0) continue

            lineFirstChar = line[0]

            if (lineFirstChar == '#') continue

            if (lineFirstChar == 'v') {

                val data = line.split("\\s+".toRegex())

                when (data[0]) {

                    "v" -> {
                        state.vertices.push(
                                data[1].toFloat(),
                                data[2].toFloat(),
                                data[3].toFloat()
                        )
                        if (data.length == 8) {
                            state.colors.push(
                                    data[4].toFloat(),
                                    data[5].toFloat(),
                                    data[6].toFloat()
                            )
                        }
                    }

                    "vn" -> {
                        state.normals.push(
                                data[1].toFloat(),
                                data[2].toFloat(),
                                data[3].toFloat()
                        )
                    }

                    "vt" -> {
                        state.uvs.push(
                                data[1].toFloat(),
                                data[2].toFloat()
                        )
                    }

                }

            } else if (lineFirstChar == 'f') {

                val lineData = line.substring(1).trim()
                val vertexData = lineData.split("\\s+".toRegex())
                val faceVertices = mutableListOf<List<String>>()

                for (j in 0 until vertexData.size) {

                    val vertex = vertexData[j]

                    if (vertex.isNotEmpty()) {

                        val vertexParts = vertex.split("/")
                        faceVertices.push(vertexParts)

                    }

                }

                val v1 = faceVertices[0]

                for (j in 1 until faceVertices.size - 1) {

                    val v2 = faceVertices[j]
                    val v3 = faceVertices[j + 1]

                    state.addFace(
                            v1[0], v2[0], v3[0],
                            v1[1], v2[1], v3[1],
                            v1[2], v2[2], v3[2]
                    )

                }

            } else if (lineFirstChar == 'l') {

                val lineParts = line.substring(1).trim().split(" ")
                var lineVertices = mutableListOf<String>()
                val lineUVs = mutableListOf<String>()

                if (line.indexOf("/") == -1) {

                    lineVertices = lineParts.toMutableList()

                } else {

                    for (li in 0 until lineParts.size) {

                        val parts = lineParts[li].split("/")

                        if (parts[0] != "") lineVertices.push(parts[0])
                        if (parts[1] != "") lineUVs.push(parts[1])

                    }

                }
                state.addLineGeometry(lineVertices, lineUVs)

            } else if (lineFirstChar == 'p') {

                val lineData = line.substring(1).trim()
                val pointData = lineData.split(" ")

                state.addPointGeometry(pointData)

            } else if (OBJECT_PATTERN.toRegex().findAll(line).mapNotNull { if (it.value.isNotEmpty()) it.value else null }.toList().let {
                        result = it
                        result.isNotEmpty()
                    }) {

                // o object_name
                // or
                // g group_name

                val name = (" " + result[0].substring(1).trim()).substring(1)

                state.startObject(name)


            } else if (MATERIAL_USE_PATTERN.toRegex().findAll(line).mapNotNull { if (it.value.isNotEmpty()) it.value else null }.toList().let {
                        result = it
                        result.isNotEmpty()
                    }) {

                // material

                state.`object`!!.startMaterial(line.substring(7).trim(), state.materialLibraries)

            } else if (MATERIAL_LIBRARY_PATTERN.toRegex().findAll(line).mapNotNull { if (it.value.isNotEmpty()) it.value else null }.toList().let {
                        result = it
                        result.isNotEmpty()
                    }) {

                // mtl file

                state.materialLibraries.push(line.substring(7).trim())

            } else if (lineFirstChar == 's') {

                result = line.split(" ")

                // smooth shading

                // @todo Handle files that have varying smooth values for a set of faces inside one geometry,
                // but does not define a usemtl for each face set.
                // This should be detected and a dummy material created (later MultiMaterial and geometry groups).
                // This requires some care to not create extra material on each smooth value for "normal" obj files.
                // where explicit usemtl defines geometry groups.
                // Example asset: examples/models/obj/cerberus/Cerberus.obj

                /*
                 * http://paulbourke.net/dataformats/obj/
                 * or
                 * http://www.cs.utah.edu/~boulos/cs3505/obj_spec.pdf
                 *
                 * From chapter "Grouping" Syntax explanation "s group_number":
                 * "group_number is the smoothing group number. To turn off smoothing groups, use a value of 0 or off.
                 * Polygonal elements use group numbers to put elements in different smoothing groups. For free-form
                 * surfaces, smoothing groups are either turned on or off; there is no difference between values greater
                 * than 0."
                 */
                if (result.length > 1) {

                    val value = result[1].trim().toLowerCase()
                    state.`object`!!.smooth = (value != "0" && value != "off")

                } else {

                    // ZBrush can produce "s" lines #11707
                    state.`object`!!.smooth = true

                }
                val material = state.`object`!!.currentMaterial
                if (material != null) {
                    material.smooth = state.`object`!!.smooth
                }

            } else {

                // Handle null terminated files without exception
                if (line == "\\0") continue

                throw Error("OBJLoader: Unexpected line: '$line'")

            }


        }

        state.finalize()

        val container = Group()

        for (i in 0 until state.objects.size) {

            val `object` = state.`object`!!
            val geometry = `object`.geometry
            val materials = `object`.materials
            val isLine = (geometry.type == "Line")
            val isPoints = (geometry.type == "Points")
            var hasVertexColors = false

            // Skip o/g line declarations that did not follow with any faces
            if (geometry.vertices.isEmpty()) continue

            val buffergeometry = BufferGeometry()

            buffergeometry.addAttribute("position", FloatBufferAttribute(geometry.vertices.toFloatArray(), 3))

            if (geometry.normals.length > 0) {

                buffergeometry.addAttribute("normal", FloatBufferAttribute(geometry.normals.toFloatArray(), 3))

            } else {

                buffergeometry.computeVertexNormals()

            }

            if (geometry.colors.length > 0) {

                hasVertexColors = true
                buffergeometry.addAttribute("color", FloatBufferAttribute(geometry.colors.toFloatArray(), 3))

            }

            if (geometry.uvs.length > 0) {

                buffergeometry.addAttribute("uv", FloatBufferAttribute(geometry.uvs.toFloatArray(), 2))

            }

            // Create materials

            val createdMaterials = mutableListOf<Material>()

            for (mi in 0 until materials.size) {

                val sourceMaterial = materials[mi]
                var material: Material? = null

                this.materials?.also { materialCreator ->

                    material = materialCreator.create(sourceMaterial.name!!)

                    // mtl etc. loaders probably can't create line materials correctly, copy properties to a line material.
                    if (isLine && material != null && material !is LineBasicMaterial) {

                        val materialLine = LineBasicMaterial()

                        materialLine.copy(material!!)

                        materialLine.color.copy((material as MaterialWithColor).color)


                        materialLine.lights = false
                        material = materialLine

                    } else if (isPoints && material != null && material !is PointsMaterial) {

                        val materialPoints = PointsMaterial().apply {
                            size = 10f
                            sizeAttenuation = false
                        }
                        materialPoints.copy(material!!)


                        materialPoints.color.copy((material as MaterialWithColor).color)

                        materialPoints.map = material!!.map
                        materialPoints.lights = false
                        material = materialPoints

                    }

                }

                if (material == null) {

                    when {
                        isLine -> material = LineBasicMaterial()
                        isPoints -> material = PointsMaterial().apply {
                            size = 1f
                            sizeAttenuation = false
                        }
                        else -> material = MeshPhongMaterial()
                    }

                    material!!.name = sourceMaterial.name!!

                }

                material!!.flatShading = !sourceMaterial.smooth
                material!!.vertexColors = if (hasVertexColors) Colors.Vertex else Colors.None

                createdMaterials.push(material!!)

            }

            // Create mesh

            var mesh: Object3D

            if (createdMaterials.length > 1) {

                for (mi in 0 until materials.length) {

                    val sourceMaterial = materials[mi]
                    buffergeometry.addGroup(sourceMaterial.groupStart, sourceMaterial.groupCount, mi)

                }

                when {
                    isLine -> mesh = LineSegments(buffergeometry, createdMaterials[0] as LineBasicMaterial) //TODO support multiple materials
                    isPoints -> mesh = Points(buffergeometry, createdMaterials[0])  //TODO support multiple materials
                    else -> mesh = Mesh(buffergeometry, createdMaterials)
                }

            } else {

                when {
                    isLine -> mesh = LineSegments(buffergeometry, createdMaterials[0] as LineBasicMaterial)
                    isPoints -> mesh = Points(buffergeometry, createdMaterials[0])
                    else -> mesh = Mesh(buffergeometry, createdMaterials[0])
                }

            }

            mesh.name = `object`.name

            container.add(mesh)

        }

        return container

    }


    private companion object {

        // o object_name | g group_name
        val OBJECT_PATTERN = "^[og]\\s*(.+)?"

        // mtllib file_reference
        val MATERIAL_LIBRARY_PATTERN = "^mtllib "

        // usemtl material_name
        var MATERIAL_USE_PATTERN = "^usemtl "

    }

    private class ParserState {

        var `object`: OBJObject? = null
        val objects = mutableListOf<OBJObject>()

        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val colors = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()

        val materialLibraries = mutableListOf<String>()

        init {
            startObject("", false)
        }

        fun startObject(name: String, fromDeclaration: Boolean = false) {

            // If the current object (initial from reset) is not from a g/o declaration in the parsed
            // file. We need to use it for the first parsed g/o to keep things in sync.

            this.`object`?.also { `object` ->
                if (!`object`.fromDeclaration) {
                    `object`.name = name
                    `object`.fromDeclaration = fromDeclaration != false
                    return
                }
            }

            val previousMaterial = this.`object`?.currentMaterial

            this.`object`?._finalize(true)


            this.`object` = OBJObject(
                    name = name,
                    fromDeclaration = fromDeclaration,
                    smooth = true
            ).apply {

                if (previousMaterial?.name != null) {

                    val declared = previousMaterial.copy(index = 0, inherited = true)
                    materials.add(declared)

                }

                objects.add(this)

            }


        }

        fun finalize() {

            this.`object`?._finalize(true)

        }

        fun parseVertexIndex(value: String, len: Int): Int {
            val index = value.toInt(10)
            return (if (index > 0) index - 1 else index + len / 3) * 3
        }

        fun parseNormalIndex(value: String, len: Int): Int {
            val index = value.toInt(10)
            return (if (index > 0) index - 1 else index + len / 3) * 3
        }

        fun parseUVIndex(value: String, len: Int): Int {
            val index = value.toInt(10)
            return (if (index > 0) index - 1 else index + len / 2) * 2
        }

        fun addVertex(a: Int, b: Int, c: Int) {

            val src = this.vertices
            val dst = this.`object`!!.geometry.vertices

            dst.push(src[a + 0], src[a + 1], src[a + 2])
            dst.push(src[b + 0], src[b + 1], src[b + 2])
            dst.push(src[c + 0], src[c + 1], src[c + 2])

        }

        fun addVertexPoint(a: Int) {

            val src = this.vertices
            val dst = this.`object`!!.geometry.vertices

            dst.push(src[a + 0], src[a + 1], src[a + 2])

        }

        fun addVertexLine(a: Int) {

            val src = this.vertices
            val dst = this.`object`!!.geometry.vertices

            dst.push(src[a + 0], src[a + 1], src[a + 2])

        }

        fun addNormal(a: Int, b: Int, c: Int) {

            val src = this.normals
            val dst = this.`object`!!.geometry.normals

            dst.push(src[a + 0], src[a + 1], src[a + 2])
            dst.push(src[b + 0], src[b + 1], src[b + 2])
            dst.push(src[c + 0], src[c + 1], src[c + 2])


        }

        fun addColor(a: Int, b: Int, c: Int) {

            val src = this.colors
            val dst = this.`object`!!.geometry.colors

            dst.push(src[a + 0], src[a + 1], src[a + 2])
            dst.push(src[b + 0], src[b + 1], src[b + 2])
            dst.push(src[c + 0], src[c + 1], src[c + 2])

        }

        fun addUV(a: Int, b: Int, c: Int) {

            val src = this.uvs
            val dst = this.`object`!!.geometry.uvs

            dst.push(src[a + 0], src[a + 1])
            dst.push(src[b + 0], src[b + 1])
            dst.push(src[c + 0], src[c + 1])

        }

        fun addUVLine(a: Int) {

            val src = this.uvs
            val dst = this.`object`!!.geometry.uvs

            dst.push(src[a + 0], src[a + 1])

        }

        fun addFace(a: String, b: String, c: String, ua: String, ub: String, uc: String, na: String, nb: String, nc: String) {

            val vLen = this.vertices.size

            var ia = this.parseVertexIndex(a, vLen)
            var ib = this.parseVertexIndex(b, vLen)
            var ic = this.parseVertexIndex(c, vLen)

            this.addVertex(ia, ib, ic)

            if (ua != "") {

                val uvLen = this.uvs.size
                ia = this.parseUVIndex(ua, uvLen)
                ib = this.parseUVIndex(ub, uvLen)
                ic = this.parseUVIndex(uc, uvLen)
                this.addUV(ia, ib, ic)

            }

            if (na != "") {

                // Normals are many times the same. If so, skip function call and parseInt.
                val nLen = this.normals.length
                ia = this.parseNormalIndex(na, nLen)

                ib = if (na == nb) ia else this.parseNormalIndex(nb, nLen)
                ic = if (na == nc) ia else this.parseNormalIndex(nc, nLen)

                this.addNormal(ia, ib, ic)

            }

            if (this.colors.length > 0) {

                this.addColor(ia, ib, ic)

            }

        }

        fun addPointGeometry(vertices: List<String>) {

            this.`object`!!.geometry.type = "Points"

            val vLen = this.vertices.length

            vertices.forEach { v ->
                this.addVertexPoint(this.parseVertexIndex(v, vLen))
            }

        }

        fun addLineGeometry(vertices: List<String>, uvs: List<String>) {

            this.`object`!!.geometry.type = "Line"

            val vLen = this.vertices.length
            val uvLen = this.uvs.length

            vertices.forEach { v ->
                this.addVertexLine(this.parseVertexIndex(v, vLen))
            }

            uvs.forEach { v ->
                this.addUVLine(this.parseUVIndex(v, uvLen))
            }

        }


    }

    private class OBJObject(
            var name: String,
            var fromDeclaration: Boolean,

            var smooth: Boolean = false
    ) {

        val geometry = OBJGeometry()
        val materials = mutableListOf<OBJMaterial>()

        val currentMaterial: OBJMaterial?
            get() = materials.getOrNull(materials.size - 1)


        fun startMaterial(name: String, libraries: List<String>): OBJMaterial {

            val previous = _finalize(false)

            previous?.also { prev ->
                if (prev.inherited || prev.groupCount <= 0) {
                    materials.splice(prev.index!!, 1)
                }
            }

            val material = OBJMaterial(
                    index = materials.size,
                    name = name,
                    mtlib = if (libraries.isNotEmpty()) libraries.last() else "",
                    smooth = previous?.smooth ?: smooth,
                    groupStart = previous?.groupEnd ?: 0,
                    groupEnd = -1,
                    groupCount = -1,
                    inherited = false

            )

            materials.add(material)

            return material

        }


        fun _finalize(end: Boolean): OBJMaterial? {
            val lastMultiMaterial = this.currentMaterial
            if (lastMultiMaterial != null && lastMultiMaterial.groupEnd == -1) {

                lastMultiMaterial.groupEnd = this.geometry.vertices.size / 3
                lastMultiMaterial.groupCount = lastMultiMaterial.groupEnd - lastMultiMaterial.groupStart
                lastMultiMaterial.inherited = false

            }

            // Ignore objects tail materials if no face declarations followed them before a new o/g started.
            if (end && this.materials.size > 1) {

                for (mi in materials.size - 1 downTo 0) {

                    if (this.materials[mi].groupCount <= 0) {

                        this.materials.splice(mi, 1)

                    }

                }

            }

            // Guarantee at least one empty material, this makes the creation later more straight forward.
            if (end && this.materials.isEmpty()) {

                this.materials.add(OBJMaterial(
                        name = "",
                        smooth = this.smooth
                ))

            }

            return lastMultiMaterial
        }

    }

    private class OBJGeometry {

        var type: String = ""
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val colors = mutableListOf<Float>()
        val uvs = mutableListOf<Float>()

    }

    private data class OBJMaterial(
            var index: Int? = null,
            var name: String? = null,
            var mtlib: String = "",
            var smooth: Boolean = false,
            var groupStart: Int = 0,
            var groupEnd: Int = -1,
            var groupCount: Int = -1,
            var inherited: Boolean = false
    )

}
