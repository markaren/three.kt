package info.laht.threekt.materials

import info.laht.threekt.math.Color
import info.laht.threekt.textures.Texture

class SpriteMaterial : Material(), MaterialWithSizeAttenuation, MaterialWithColor {

    override val color = Color(0xffffff)

    override var map: Texture? = null

    var rotation = 0f

    override var sizeAttenuation = true

    init {

        lights = false
        transparent = true

    }

}
