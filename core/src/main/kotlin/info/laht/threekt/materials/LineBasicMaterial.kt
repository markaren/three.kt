package info.laht.threekt.materials

import info.laht.threekt.math.Color

open class LineBasicMaterial: Material(), MaterialWithColor {

    override val color = Color( 0xffffff )

    var linewidth = 1f
    var linecap = "round"
    var linejoin = "round"
    
    init {

        lights = false

    }

    override fun clone(): LineBasicMaterial {
        return LineBasicMaterial().copy(this)
    }

    fun copy( source: LineBasicMaterial ): LineBasicMaterial {

        super.copy(source)

        this.color.copy( source.color )

        this.linewidth = source.linewidth
        this.linecap = source.linecap
        this.linejoin = source.linejoin

        return this
    }

}