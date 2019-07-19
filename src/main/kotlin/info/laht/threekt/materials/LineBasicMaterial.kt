package info.laht.threekt.materials

import info.laht.threekt.math.Color

class LineBasicMaterial: Material() {

    val color = Color( 0xffffff )

    var linewidth = 1f
    var linecap = "round"
    var linejoin = "round"
    
    init {

        lights = false

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