package info.laht.threekt.materials

import info.laht.threekt.math.Vector3
import info.laht.threekt.textures.Texture

class MeshDistanceMaterial : Material(), MaterialWithMorphTarget, MaterialWithSkinning {

    val referencePosition = Vector3()
    var nearDistance = 1f
    var farDistance = 1000f

    override var skinning = false
    override var morphTargets = false

    public override var map: Texture? = null

    public override var alphaMap: Texture? = null

    public override var displacementMap: Texture? = null
    public override var displacementScale = 1f
    public override var displacementBias = 0f

    init {

        fog = false
        lights = false

    }

    override fun clone(): MeshDistanceMaterial {
        return MeshDistanceMaterial().copy(this)
    }

    fun copy(source: MeshDistanceMaterial): MeshDistanceMaterial {

        super.copy(source)

        this.referencePosition.copy(source.referencePosition)
        this.nearDistance = source.nearDistance
        this.farDistance = source.farDistance

        this.skinning = source.skinning
        this.morphTargets = source.morphTargets

        this.map = source.map

        this.alphaMap = source.alphaMap

        this.displacementMap = source.displacementMap
        this.displacementScale = source.displacementScale
        this.displacementBias = source.displacementBias

        return this
    }

}
