package info.laht.threekt.materials

import info.laht.threekt.NormalMapType
import info.laht.threekt.math.Vector2
import info.laht.threekt.textures.Texture

class MeshNormalMaterial : Material(), MaterialWithWireframe, MaterialWithMorphNormals, MaterialWithMorphTargets,
    MaterialWithSkinning {

    public override var bumpMap: Texture? = null
    public override var bumpScale = 1f

    public override var normalMap: Texture? = null
    public override var normalMapType = NormalMapType.TangentSpace
    val normalScale = Vector2(1, 1)

    public override var displacementMap: Texture? = null
    public override var displacementScale = 1f
    public override var displacementBias = 0f

    override var wireframe = false
    override var wireframeLinewidth = 1f

    override var skinning = false
    override var morphTargets = false
    override var morphNormals = false

    init {

        var fog = false
        var lights = false

    }

    override fun clone(): MeshNormalMaterial {
        return MeshNormalMaterial().copy(this)
    }

    fun copy(source: MeshNormalMaterial): MeshNormalMaterial {

        super.copy(source)

        this.bumpMap = source.bumpMap
        this.bumpScale = source.bumpScale

        this.normalMap = source.normalMap
        this.normalMapType = source.normalMapType
        this.normalScale.copy(source.normalScale)

        this.displacementMap = source.displacementMap
        this.displacementScale = source.displacementScale
        this.displacementBias = source.displacementBias

        this.wireframe = source.wireframe
        this.wireframeLinewidth = source.wireframeLinewidth

        this.skinning = source.skinning
        this.morphTargets = source.morphTargets
        this.morphNormals = source.morphNormals

        return this

    }

}
