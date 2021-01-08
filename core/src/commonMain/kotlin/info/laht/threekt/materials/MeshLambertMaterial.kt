package info.laht.threekt.materials

import info.laht.threekt.TextureCombineOperation
import info.laht.threekt.math.Color
import info.laht.threekt.textures.Texture

class MeshLambertMaterial : Material(), MaterialWithColor, MaterialWithSkinning, MaterialWithMorphTargets,
        MaterialWithMorphNormals, MaterialWithEmissive, MaterialWithWireframe, MaterialWithReflectivity {

    override val color = Color(0xffffff)

    public override var map: Texture? = null

    public override var lightMap: Texture? = null
    public override var lightMapIntensity = 1f

    public override var aoMap: Texture? = null
    public override var aoMapIntensity = 1f

    override val emissive = Color(0x000000)
    override var emissiveIntensity = 1f
    override var emissiveMap: Texture? = null

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
    override var morphNormals = false

    override fun clone(): MeshLambertMaterial {
        return MeshLambertMaterial().copy(this)
    }

    fun copy(source: MeshLambertMaterial): MeshLambertMaterial {

        super.copy(source)

        this.color.copy(source.color)

        this.map = source.map

        this.lightMap = source.lightMap
        this.lightMapIntensity = source.lightMapIntensity

        this.aoMap = source.aoMap
        this.aoMapIntensity = source.aoMapIntensity

        this.emissive.copy(source.emissive)
        this.emissiveMap = source.emissiveMap
        this.emissiveIntensity = source.emissiveIntensity

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
        this.morphNormals = source.morphNormals

        return this

    }

}
