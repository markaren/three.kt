package info.laht.threekt.materials

import info.laht.threekt.math.Color
import info.laht.threekt.textures.Texture

class PointsMaterial : Material(), MaterialWithMorphTarget, MaterialWithSizeAttenuation {

    val color = Color(0xffffff)

    override var map: Texture? = null

    var size = 1
    override var sizeAttenuation = true

    override var morphTargets = false

    init {
        lights = false
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