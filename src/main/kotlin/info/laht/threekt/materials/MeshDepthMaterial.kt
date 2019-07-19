package info.laht.threekt.materials

import info.laht.threekt.BasicDepthPacking
import info.laht.threekt.textures.Texture

class MeshDepthMaterial: Material(), MaterialWithMorphTarget, MaterialWithSkinning {

    var depthPacking = BasicDepthPacking

    override var skinning = false
    override var morphTargets = false

    public override var map: Texture? = null

    public override var alphaMap: Texture? = null

    public override var displacementMap: Texture? = null
    public override var displacementScale = 1f
    public override var displacementBias = 0f

    var wireframe = false
    var wireframeLinewidth = 1f

    init {

        fog = false
        lights = false

    }
    
}
