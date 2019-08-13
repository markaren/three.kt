package info.laht.threekt.materials

import info.laht.threekt.NormalMapType
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector2
import info.laht.threekt.textures.Texture

open class MeshStandardMaterial : Material(), MaterialWithColor, MaterialWithEmissive, MaterialWithWireframe,
    MaterialWithSkinning, MaterialWithMorphTarget, MaterialWithMorphNormals {

    override val color = Color(0xffffff) // diffuse
    var roughness = 0.5f
    var metalness = 0.5f

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
    val normalScale = Vector2(1, 1)

    public override var displacementMap: Texture? = null
    public override var displacementScale = 1f
    public override var displacementBias = 0f

    public override var roughnessMap: Texture? = null

    public override var metalnessMap: Texture? = null

    public override var alphaMap: Texture? = null

    public override var envMap: Texture? = null
    var envMapIntensity = 1f

    var refractionRatio = 0.98f

    override var wireframe = false
    override var wireframeLinewidth = 1f

    override var skinning = false
    override var morphTargets = false
    override var morphNormals = false

    init {
        defines.apply {
            put("STANDARD", "")
        }
    }

    override fun clone(): MeshStandardMaterial {
        return MeshStandardMaterial().copy(this)
    }

    fun copy(source: MeshStandardMaterial): MeshStandardMaterial {

        super.copy(source)

        this.defines.apply {
            clear()
            put("STANDARD", "")
        }

        this.color.copy(source.color)
        this.roughness = source.roughness
        this.metalness = source.metalness

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

        this.roughnessMap = source.roughnessMap

        this.metalnessMap = source.metalnessMap

        this.alphaMap = source.alphaMap

        this.envMap = source.envMap
        this.envMapIntensity = source.envMapIntensity

        this.refractionRatio = source.refractionRatio

        this.wireframe = source.wireframe
        this.wireframeLinewidth = source.wireframeLinewidth

        this.skinning = source.skinning
        this.morphTargets = source.morphTargets
        this.morphNormals = source.morphNormals

        return this

    }

}
