package info.laht.threekt.materials

import info.laht.threekt.TextureCombineOperation
import info.laht.threekt.math.Color
import info.laht.threekt.textures.Texture

class MeshBasicMaterial : Material(), MaterialWithMorphTarget, MaterialWithSkinning, MaterialWithColor,
    MaterialWithWireframe, MaterialWithReflectivity {

    override val color = Color.fromHex(0xffffff)

    public override var map: Texture? = null

    public override var lightMap: Texture? = null
    public override var lightMapIntensity = 1f

    public override var aoMap: Texture? = null
    public override var aoMapIntensity = 1f

    public override var specularMap: Texture? = null
    public override var alphaMap: Texture? = null
    public override var envMap: Texture? = null

    public override var combine = TextureCombineOperation.Multiply

    override var reflectivity = 1f
    override var refractionRatio = 0.98f

    override var wireframe = false
    override var wireframeLinewidth = 1f

    override var skinning = false
    override var morphTargets = false

    init {

        lights = false

    }

    override fun clone(): MeshBasicMaterial {
        return MeshBasicMaterial().copy(this)
    }

    fun copy(source: MeshBasicMaterial): MeshBasicMaterial {
        super.copy(source)

        this.color.copy(source.color)

        this.map = source.map

        this.lightMap = source.lightMap
        this.lightMapIntensity = source.lightMapIntensity

        this.aoMap = source.aoMap
        this.aoMapIntensity = source.aoMapIntensity

        this.specularMap = source.specularMap

        this.alphaMap = source.alphaMap

        this.envMap = source.envMap
        this.combine = source.combine
        this.reflectivity = source.reflectivity
        this.refractionRatio = source.refractionRatio

        this.wireframe = source.wireframe
        this.wireframeLinewidth = source.wireframeLinewidth

        this.skinning = source.skinning
        this.morphTargets = source.morphTargets

        return this

    }

}
