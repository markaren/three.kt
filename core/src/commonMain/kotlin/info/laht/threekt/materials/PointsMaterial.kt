package info.laht.threekt.materials

import info.laht.threekt.math.Color
import info.laht.threekt.textures.Texture

class PointsMaterial : Material(), MaterialWithMorphTargets, MaterialWithSizeAttenuation, MaterialWithColor {

    override val color = Color(0xffffff)

    var size = 1f
    override var sizeAttenuation = true

    override var map: Texture? = null

    override var morphTargets = false

    init {
        lights = false
    }

    override fun clone(): PointsMaterial {
        return PointsMaterial().copy(this)
    }

    fun copy(source: PointsMaterial): PointsMaterial {

        super.copy(source)

        this.color.copy(source.color)

        this.map = source.map

        this.size = source.size
        this.sizeAttenuation = source.sizeAttenuation

        this.morphTargets = source.morphTargets

        return this
    }

}
