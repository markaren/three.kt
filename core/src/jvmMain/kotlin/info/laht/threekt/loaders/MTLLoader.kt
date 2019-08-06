package info.laht.threekt.loaders

import info.laht.threekt.Side
import info.laht.threekt.TextureWrapping
import info.laht.threekt.materials.Material
import info.laht.threekt.math.Vector2

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

    data class MaterialOptions(
        val name: String,
        val side: Side = Side.Front,
        val wrap: TextureWrapping = TextureWrapping.Repeat,
        val normalizeRGB: Boolean = false,
        val ignoreZeroRGBs: Boolean = false,
        val invertTrProperty: Boolean = false
    )

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

            var index = 0

            for (mn in materialsInfo.keys) {
                materialsArray.add(create(mn))
                nameLookup[mn] = index++
            }

        }

        fun create(materialName: String): Material {

            if (materialName !in materials) {
                createMaterial(materialName)
            }

            return materials[materialName]!!

        }

        private fun createMaterial(materialName: String) {

            val mat = materialsInfo[materialName]!!
            val params = MaterialOptions(
                name = materialName,
                side = this.side
            )


        }

        fun getTextureParams(value: String): TexParams {

            TODO()

        }

    }

    class TexParams(
        val scale: Vector2,
        val offset: Vector2,
        val url: String
    )

    class MaterialInfo(
        val ks: FloatArray? = null,
        val kd: FloatArray? = null,
        val ke: FloatArray? = null,
        val map_kd: String? = null,
        val map_ks: String? = null,
        val map_ke: String? = null,
        val norm: String? = null,
        val bump: String? = null,
        val map_d: String? = null,
        val ns: Float? = null,
        val d: Float? = null,
        val tr: Float? = null
    )

}
