package info.laht.threekt.materials

class LineDashedMaterial: LineBasicMaterial() {

    var scale = 1f;
    var dashSize = 3f;
    var gapSize = 1f;

    override fun clone(): LineDashedMaterial {
        return LineDashedMaterial().copy(this)
    }

    fun copy( source: LineDashedMaterial ): LineDashedMaterial {

        super.copy(source)

        this.scale = source.scale;
        this.dashSize = source.dashSize;
        this.gapSize = source.gapSize;

        return this
    }
    
}