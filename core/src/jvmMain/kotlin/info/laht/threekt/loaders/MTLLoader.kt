package info.laht.threekt.loaders

import info.laht.threekt.Side
import info.laht.threekt.TextureWrapping
import info.laht.threekt.materials.Material

class MTLLoader {

    var materialOptions: MaterialOptions? = null

    fun parse(text: String): MaterialCreator {

        val lines = text.split("\n")
        val info = mutableMapOf<String, Any>()
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

                info.apply {
                    clear()
                    put("name", value)
                }
                materialsInfo[value] = info

            } else {

                if (key == "ka" || key == "kd" || key == "ks" || key == "ke") {

                    val ss = value.split(delimiter_pattern, 3)
                    info[key] = listOf(ss[0].toFloat(), ss[1].toFloat(), ss[2].toFloat())

                } else {

                    info[key] = value

                }

            }

        }

        val materialCreator = MaterialCreator(materialOptions)
        materialCreator.setMaterials(materialsInfo)
        return materialCreator
    }

    data class MaterialOptions(
        val side: Side = Side.Front,
        val wrap: TextureWrapping = TextureWrapping.Repeat,
        val normalizeRGB: Boolean = false,
        val ignoreZeroRGBs: Boolean = false,
        val invertTrProperty: Boolean = false
    )

    class MaterialCreator internal constructor(
        private val options: MaterialOptions? = null
    ) {

        val materials = mutableMapOf<String, Material>()
        val materialsArray = mutableListOf<Map<String, Material>>()
        lateinit var materialsInfo: Map<String, Map<String, Any>>

        fun setMaterials(materialsInfo: Map<String, Map<String, Any>>) {
            this.materialsInfo = convert(materialsInfo)
        }

        fun convert(materialsInfo: Map<String, Map<String, Any>>): Map<String, Map<String, Any>> {

            if (options == null) return materialsInfo

            val converted = mutableMapOf<String, Any>()

            for (mn in materialsInfo) {

                val mat = materialsInfo[mn]

            }

        }

    }

}
