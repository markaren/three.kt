package info.laht.threekt.materials

import info.laht.threekt.textures.Texture

class MeshToonMaterial : MeshPhongMaterial() {

    public override var gradientMap: Texture? = null

    init {

        defines["TOON"] = ""

    }

    override fun clone(): MeshToonMaterial {
        return MeshToonMaterial().copy(this)
    }

    fun copy(source: MeshToonMaterial): MeshToonMaterial {

        super.copy(source)

        this.gradientMap = source.gradientMap

        return this

    }

}
