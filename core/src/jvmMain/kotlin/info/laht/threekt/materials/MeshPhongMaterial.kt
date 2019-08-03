package info.laht.threekt.materials

import info.laht.threekt.NormalMapType
import info.laht.threekt.TextureCombineOperation
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector2
import info.laht.threekt.textures.Texture

open class MeshPhongMaterial : Material(), MaterialWithWireframe, MaterialWithColor, MaterialWithEmissive,
    MaterialWithMorphTarget, MaterialWithMorphNormals, MaterialWithSkinning, MaterialWithReflectivity,
    MaterialWithSpecular {

    override var color = Color(0xffffff) // diffuse
    override var specular = Color(0x111111)
    override var shininess = 30f

    public override var map: Texture? = null

    public override var lightMap: Texture? = null
    public override var lightMapIntensity = 1f

    public override var aoMap: Texture? = null
    public override var aoMapIntensity = 1f

    override var emissive = Color(0x000000)
    override var emissiveIntensity = 1f
    public override var emissiveMap: Texture? = null

    public override var bumpMap: Texture? = null
    public override var bumpScale = 1f

    public override var normalMap: Texture? = null
    public override var normalMapType = NormalMapType.TangentSpace
    var normalScale = Vector2(1, 1)

    public override var displacementMap: Texture? = null
    public override var displacementScale = 1f
    public override var displacementBias = 0f

    public override var specularMap: Texture? = null

    public override var alphaMap: Texture? = null

    public override var envMap: Texture? = null
    override var combine = TextureCombineOperation.Multiply
    override var reflectivity = 1f
    override var refractionRatio = 0.98f

    override var wireframe = false
    override var wireframeLinewidth = 1f

    override var skinning = false
    override var morphTargets = false
    override var morphNormals = false

    override fun clone(): MeshPhongMaterial {
        return MeshPhongMaterial().copy(this)
    }

    fun copy(source: MeshPhongMaterial): MeshPhongMaterial {

        super.copy(source)

        this.color.copy(source.color)
        this.specular.copy(source.specular)
        this.shininess = source.shininess

        this.map = source.map

        this.lightMap = source.lightMap
        this.lightMapIntensity = source.lightMapIntensity

        this.aoMap = source.aoMap
        this.aoMapIntensity = source.aoMapIntensity

        this.emissive.copy(source.emissive)
        this.emissiveMap = source.emissiveMap
        this.emissiveIntensity = source.emissiveIntensity

        this.bumpMap = source.bumpMap
        this.bumpScale = source.bumpScale

        this.normalMap = source.normalMap
        this.normalMapType = source.normalMapType
        this.normalScale.copy(source.normalScale)

        this.displacementMap = source.displacementMap
        this.displacementScale = source.displacementScale
        this.displacementBias = source.displacementBias

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
