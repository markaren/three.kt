package info.laht.threekt.loaders

import info.laht.threekt.*
import info.laht.threekt.materials.Material
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Vector2
import info.laht.threekt.textures.Texture
import java.io.File

class MTLLoader {

    private var path: String? = null
    private var resourcePath: String? = null

    var materialOptions: MaterialOptions? = null

    fun setPath(path: String): MTLLoader {
        this.path = path
        return this
    }

    fun setResourcePath(path: String): MTLLoader {
        this.resourcePath = path
        return this
    }

    fun load(url: String): MaterialCreator {
        val path = this.path ?: LoaderUtils.extractUrlBase(url)
        return parse(File(url).readText(), path)
    }

    fun parse(text: String, path: String): MaterialCreator {

        val lines = text.split("\n")
        var info: MutableMap<String, Any>? = null
        val delimiterPattern = "\\s+".toRegex()
        val materialsInfo = mutableMapOf<String, MutableMap<String, Any>>()

        for (i in 0 until lines.size) {

            var line = lines[i]
            line = line.trim()

            if (line.isEmpty() || line[0] == '#') {

                // Blank line or comment ignore
                continue

            }

            val pos = line.indexOf(" ")

            var key = if (pos >= 0) line.substring(0, pos) else line
            key = key.toLowerCase()

            var value = if (pos >= 0) line.substring(pos + 1) else ""
            value = value.trim()

            if (key == "newmtl") {

                // New material

                info = mutableMapOf("name" to value)
                materialsInfo[value] = info

            } else {

                if (key == "ka" || key == "kd" || key == "ks" || key == "ke") {

                    val ss = value.split(delimiterPattern, 3)
                    info!![key] = floatArrayOf(ss[0].toFloat(), ss[1].toFloat(), ss[2].toFloat())

                } else {

                    info!![key] = value

                }

            }

        }

        val materialCreator = MaterialCreator(this.resourcePath ?: path, materialOptions)
        materialCreator.setMaterials(materialsInfo)
        return materialCreator
    }

    class MaterialCreator internal constructor(
            baseUrl: String? = null,
            private val options: MaterialOptions? = null
    ) {

        private val baseUrl = baseUrl ?: ""

        private val side = options?.side ?: Side.Front
        private val wrap = options?.wrap ?: TextureWrapping.Repeat

        private val materials = mutableMapOf<String, Material>()
        private val materialsArray = mutableListOf<Material>()
        private lateinit var materialsInfo: Map<String, Map<String, Any>>
        private val nameLookup = mutableMapOf<String, Int>()

        fun setMaterials(materialsInfo: Map<String, Map<String, Any>>) {
            this.materialsInfo = convert(materialsInfo)
        }

        private fun convert(materialsInfo: Map<String, Map<String, Any>>): Map<String, Map<String, Any>> {

            if (options == null) {
                return materialsInfo
            }

            val converted = mutableMapOf<String, Map<String, Any>>()

            for (mn in materialsInfo.keys) {

                val mat = materialsInfo.getValue(mn)

                val covMat = mutableMapOf<String, Any>()

                converted[mn] = covMat

                loop@ for (prop in mat.keys) {

                    var save = true
                    val value = mat.getValue(prop)
                    val lprop = prop.toLowerCase()

                    when (lprop) {
                        "kd", "ka", "ks" -> {
                            // Diffuse color (color under white light) using RGB values

                            if (this.options != null && this.options.normalizeRGB) {

                                value as FloatArray

                                value[0] /= 255f
                                value[1] /= 255f
                                value[2] /= 255f

                            }

                            if (this.options != null && this.options.ignoreZeroRGBs) {

                                value as FloatArray

                                if (value[0] == 0f && value[1] == 0f && value[2] == 0f) {

                                    // ignore
                                    save = false

                                }

                            }
                        }
                        else -> break@loop
                    }

                    if (save) {

                        covMat[lprop] = value

                    }

                }

            }

            return converted

        }

        fun preload(): MaterialCreator {
            for (mn in materialsInfo.keys) {
                create(mn)
            }
            return this
        }

        fun getIndex(materialName: String): Int? {
            return nameLookup[materialName]
        }

        fun getAsArray() {

            for ((index, mn) in materialsInfo.keys.withIndex()) {
                materialsArray.add(create(mn)!!)
                nameLookup[mn] = index
            }

        }

        fun create(materialName: String): Material? {

            if (materialName !in materials) {
                createMaterial(materialName)
            }

            return materials[materialName]

        }

        private fun createMaterial(materialName: String) {

            if (materialName.isEmpty()) throw IllegalArgumentException("materialName is empty!")

            val mat = materialsInfo.getValue(materialName)
            val params = MeshPhongMaterial().apply {
                name = materialName
                side = this@MaterialCreator.side
            }

            fun setMapForType(mapType: String, value: String) {

                if (params.getMapForType(mapType) != null) return

                val texParams = getTextureParams(value, params)
                val map = loadTexture(baseUrl + texParams.url)

                map.repeat.copy(texParams.scale)
                map.offset.copy(texParams.offset)

                map.wrapS = wrap
                map.wrapT = wrap

                params.setMapForType(mapType, map)

            }

            for (prop in mat.keys) {

                val value = mat.getValue(prop)

                if (value == "") continue

                when (prop.toLowerCase()) {

                    "kd" -> params.color.fromArray(value as FloatArray)
                    "ks" -> params.specular.fromArray(value as FloatArray)
                    "ke" -> params.emissive.fromArray(value as FloatArray)

                    "map_kd" -> setMapForType("map", value as String)
                    "map_ks" -> setMapForType("specularMap", value as String)
                    "map_ke" -> setMapForType("emissiveMap", value as String)

                    "normal" -> setMapForType("normalMap", value as String)
                    "map_bump", "bump" -> setMapForType("bumpMap", value as String)
                    "map_d" -> setMapForType("alphaMap", value as String).also {
                        params.transparent = true
                    }
                    "ns" -> params.shininess = (value as String).toFloat()
                    "d" -> {

                        val n = (value as String).toFloat()

                        if (n < 1) {
                            params.opacity = n
                            params.transparent = true
                        }

                    }
                    "tr" -> {

                        var n = (value as String).toFloat()

                        if (this.options != null && this.options.invertTrProperty) {
                            n = 1 - n
                        }

                        if (n > 0) {

                            params.opacity = 1 - n
                            params.transparent = true

                        }

                    }

                }

                materials[materialName] = params

            }

        }

        private fun getTextureParams(value: String, matParams: MeshPhongMaterial): TexParams {

            val texParams = TexParams(
                    scale = Vector2(1, 1),
                    offset = Vector2(0, 0)
            )

            val items = value.split("\\s+".toRegex()).toMutableList()
            var pos = items.indexOf("-bm")

            if (pos >= 0) {

                matParams.bumpScale = items[pos + 1].toFloat()
                items.splice(pos, 2)

            }

            pos = items.indexOf("-o")

            if (pos >= 0) {
                texParams.offset.set(items[pos + 1].toFloat(), items[pos + 2].toFloat())
                items.splice(pos, 4)
            }

            texParams.url = items.joinToString(" ").trim()

            return texParams

        }

        fun loadTexture(url: String, mapping: TextureMapping? = null): Texture {

            val texture = TextureLoader.load(url)
            mapping?.also {
                texture.mapping = it
            }
            return texture

        }

    }

    private data class TexParams(
            var scale: Vector2,
            var offset: Vector2,
            var url: String = ""
    )

    data class MaterialOptions(
            val name: String,
            val side: Side = Side.Front,
            val wrap: TextureWrapping = TextureWrapping.Repeat,
            val normalizeRGB: Boolean = false,
            val ignoreZeroRGBs: Boolean = false,
            val invertTrProperty: Boolean = false
    )

}
