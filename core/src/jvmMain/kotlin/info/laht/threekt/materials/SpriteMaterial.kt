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

    override fun clone(): SpriteMaterial {
        return SpriteMaterial().copy(this)
    }

    fun copy( source: SpriteMaterial ): SpriteMaterial {

        super.copy(source)

        this.color.copy( source.color );
        this.map = source.map;

        this.rotation = source.rotation;

        this.sizeAttenuation = source.sizeAttenuation;

        return this;

    }

}
