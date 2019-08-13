package info.laht.threekt.materials

import info.laht.threekt.TextureEncoding
import info.laht.threekt.textures.Texture

class MeshDepthMaterial : Material(), MaterialWithMorphTarget, MaterialWithSkinning, MaterialWithWireframe {

    var depthPacking = TextureEncoding.BasicDepthPacking

    override var skinning = false
    override var morphTargets = false

    public override var map: Texture? = null

    public override var alphaMap: Texture? = null

    public override var displacementMap: Texture? = null
    public override var displacementScale = 1f
    public override var displacementBias = 0f

    override var wireframe = false
    override var wireframeLinewidth = 1f

    init {

        fog = false
        lights = false

    }

    override fun clone(): MeshDepthMaterial {
        return MeshDepthMaterial().copy(this)
    }

    fun copy(source: MeshDepthMaterial): MeshDepthMaterial {

        super.copy(source)

        this.depthPacking = source.depthPacking

        this.skinning = source.skinning
        this.morphTargets = source.morphTargets

        this.map = source.map

        this.alphaMap = source.alphaMap

        this.displacementMap = source.displacementMap
        this.displacementScale = source.displacementScale
        this.displacementBias = source.displacementBias

        this.wireframe = source.wireframe
        this.wireframeLinewidth = source.wireframeLinewidth;depthPacking = source.depthPacking

        this.skinning = source.skinning
        this.morphTargets = source.morphTargets

        this.map = source.map

        this.alphaMap = source.alphaMap

        this.displacementMap = source.displacementMap
        this.displacementScale = source.displacementScale
        this.displacementBias = source.displacementBias

        this.wireframe = source.wireframe
        this.wireframeLinewidth = source.wireframeLinewidth

        return this

    }

}
