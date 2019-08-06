package info.laht.threekt.loaders

import info.laht.threekt.Side
import info.laht.threekt.TextureMapping
import info.laht.threekt.TextureWrapping
import info.laht.threekt.materials.Material
import info.laht.threekt.math.Vector2
import info.laht.threekt.splice
import info.laht.threekt.textures.Texture

class MTLLoader {

    var materialOptions: MaterialOptions? = null

    fun parse(text: String): MaterialCreator {

        val lines = text.split("\n")
        var info: MutableMap<String, Any>? = null
        val delimiter_pattern = "\\s+".toRegex()
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

                    val ss = value.split(delimiter_pattern, 3)
                    info!![key] = listOf(ss[0].toFloat(), ss[1].toFloat(), ss[2].toFloat())

                } else {

                    info!![key] = value

                }

            }

        }

        val materialCreator = MaterialCreator(materialOptions)
        materialCreator.setMaterials(materialsInfo)
        return materialCreator
    }

    class MaterialCreator internal constructor(
            private val options: MaterialOptions? = null
    ) {

        val side = options?.side ?: Side.Front
        val wrap = options?.wrap ?: TextureWrapping.Repeat

        val materials = mutableMapOf<String, Material>()
        val materialsArray = mutableListOf<Material>()
        lateinit var materialsInfo: Map<String, Map<String, Any>>
        val nameLookup = mutableMapOf<String, Int>()

        fun setMaterials(materialsInfo: Map<String, Map<String, Any>>) {
            this.materialsInfo = convert(materialsInfo)
        }

        fun convert(materialsInfo: Map<String, Map<String, Any>>): Map<String, Map<String, Any>> {

            if (options == null) return materialsInfo

            val converted = mutableMapOf<String, Map<String, Any>>()

            for (mn in materialsInfo.keys) {

                val mat = materialsInfo.getValue(mn)

                val covMat = mutableMapOf<String, Any>()

                converted[mn] = covMat

                loop@ for (prop in mat.keys) {

                    var save = true
                    var value = mat.getValue(prop)
                    val lprop = prop.toLowerCase()

                    when (lprop) {
                        "kd", "ka", "ks" -> {
                            // Diffuse color (color under white light) using RGB values

                            if (this.options != null && this.options.normalizeRGB) {

                                value as FloatArray

                                value = listOf(value[0] / 255, value[1] / 255, value[2] / 255)

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

        fun preload() {
            for (mn in materialsInfo.keys) {
                create(mn)
            }
        }

        fun getIndex(materialName: String): Int? {
            return nameLookup[materialName]
        }

        fun getAsArray() {

            for ((index, mn) in materialsInfo.keys.withIndex()) {
                materialsArray.add(create(mn))
                nameLookup[mn] = index
            }

        }

        fun create(materialName: String): Material {

            if (materialName !in materials) {
                createMaterial(materialName)
            }

            return materials[materialName]!!

        }

        private fun createMaterial(materialName: String) {

            val mat = materialsInfo.getValue(materialName)
            val params = mutableMapOf<String, Any>(
                    "name"  to materialName,
                    "side"  to this.side
            )

            fun setMapForType(mapType: String, value: String) {

                if (params[mapType] != null) return

                val texParams = getTextureParams(value, params)

            }

        }


        fun getTextureParams(value: String, matParams: MutableMap<String, Any>): TexParams {

            val texParams = TexParams(
                    scale = Vector2(1, 1),
                    offset = Vector2(0, 0)
            )

            val items = value.split("\\s+".toRegex()).toMutableList()
            var pos = items.indexOf("-bm")

            if (pos >= 0) {

                matParams["bumpScale"] = items[pos + 1].toFloat()
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

    }

    fun loadTexture(url: String, mapping: TextureMapping? = null): Texture {
        TODO()
    }

    data class TexParams(
            var scale: Vector2,
            var offset: Vector2,
            var url: String = ""
    )

//    data class MaterialInfo(
//            var ks: FloatArray? = null,
//            var kd: FloatArray? = null,
//            var ke: FloatArray? = null,
//            var map_kd: String? = null,
//            var map_ks: String? = null,
//            var map_ke: String? = null,
//            var norm: String? = null,
//            var bump: String? = null,
//            var map_d: String? = null,
//            var ns: Float? = null,
//            var d: Float? = null,
//            var tr: Float? = null
//    ) {
//
//        operator fun get(key: String): Any? {
//            return when (key) {
//                "ks" -> ks
//                "kd" -> kd
//                "ke" -> ke
//                "map_kd" -> map_kd
//                "map_ks" -> map_ks
//                "map_ke" -> map_ke
//                "norm" -> norm
//                "bump" -> bump
//                "map_d" -> map_d
//                "ns" -> ns
//                "d" -> d
//                "tr" -> tr
//                else -> throw IllegalArgumentException("Illegal key: $key")
//            }
//        }
//
//        operator fun set(key: String, value: Any?) {
//            when (key) {
//                "ks" -> ks = value as FloatArray?
//                "kd" -> kd = value as FloatArray?
//                "ke" -> ke = value as FloatArray?
//                "map_kd" -> map_kd = value as String?
//                "map_ks" -> map_ks = value as String?
//                "map_ke" -> map_ke = value as String?
//                "norm" -> norm = value as String?
//                "bump" -> bump = value as String?
//                "map_d" -> map_d = value as String?
//                "ns" -> ns = value as Float?
//                "d" -> d = value as Float?
//                "tr" -> tr = value as Float?
//                else -> throw IllegalArgumentException("Illegal key: $key")
//            }
//        }
//
//    }
//
//
    data class MaterialOptions(
            val name: String,
            val side: Side = Side.Front,
            val wrap: TextureWrapping = TextureWrapping.Repeat,
            val normalizeRGB: Boolean = false,
            val ignoreZeroRGBs: Boolean = false,
            val invertTrProperty: Boolean = false
    ) {

//        operator fun get(key: String): Any {
//            return when (key) {
//                "name" -> name
//                "side" -> side
//                "wrap" -> wrap
//                "normalizeRGB" -> normalizeRGB
//                "ignoreZeroRGBs" -> ignoreZeroRGBs
//                "invertTrProperty" -> invertTrProperty
//                else -> throw IllegalArgumentException("Illegal key: $key")
//            }
//        }

    }


}
