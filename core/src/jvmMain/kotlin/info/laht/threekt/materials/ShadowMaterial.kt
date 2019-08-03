package info.laht.threekt.materials

import info.laht.threekt.math.Color

class ShadowMaterial : Material(), MaterialWithColor {

    override val color = Color(0xffffff)

    init {
        transparent = true
    }

    override fun clone(): ShadowMaterial {
        return ShadowMaterial().copy(this)
    }

    fun copy(source: ShadowMaterial): ShadowMaterial {

        super.copy(source)

        this.color.copy(source.color)

        return this

    }

}
