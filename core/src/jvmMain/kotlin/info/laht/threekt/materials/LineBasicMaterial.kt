package info.laht.threekt.materials

import info.laht.threekt.math.Color

open class LineBasicMaterial : Material(), MaterialWithColor, MaterialWithLineWidth {

    override val color = Color(0xffffff)

    override var linewidth = 1f

    init {

        lights = false

    }

    override fun clone(): LineBasicMaterial {
        return LineBasicMaterial().copy(this)
    }

    fun copy(source: LineBasicMaterial): LineBasicMaterial {

        super.copy(source)

        this.color.copy(source.color)

        this.linewidth = source.linewidth

        return this
    }

}
