package info.laht.threekt.materials

class MeshPhysicalMaterial : MeshStandardMaterial() {

    var reflectivity = 0.5f // maps to F0 = 0.04

    var clearCoat = 0.0f
    var clearCoatRoughness = 0.0f

    init {
        defines.apply {
            clear()
            put("PHYSICAL", "")
        }
    }

    override fun clone(): MeshPhysicalMaterial {
        return MeshPhysicalMaterial().copy(this)
    }

    fun copy(source: MeshPhysicalMaterial): MeshPhysicalMaterial {

        super.copy(source)

        this.defines.apply {
            clear()
            put("PHYSICAL", "")
        }

        this.reflectivity = source.reflectivity

        this.clearCoat = source.clearCoat
        this.clearCoatRoughness = source.clearCoatRoughness

        return this
    }

}
