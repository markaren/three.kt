package info.laht.threekt.materials

import info.laht.threekt.NormalMapType
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector2
import info.laht.threekt.textures.Texture

class MeshMatcapMaterial: Material(), MaterialWithColor, MaterialWithSkinning, MaterialWithMorphNormals, MaterialWithMorphTarget {

    override val color = Color( 0xffffff ) // diffuse

    public override var matcap: Texture? = null

    public override var map: Texture? = null

    public override var bumpMap: Texture? = null
    public override var bumpScale = 1f

    public override var normalMap: Texture? = null
    public override var normalMapType = NormalMapType.TangentSpace
    var normalScale = Vector2( 1, 1 )

    public override var displacementMap: Texture? = null
    public override var displacementScale = 1f
    public override var displacementBias = 0f

    public override var alphaMap: Texture? = null

    override var skinning = false
    override var morphTargets = false
    override var morphNormals = false

    init {

        defines["MATCAP"] = ""

        lights = false

    }

    fun copy( source: MeshMatcapMaterial ): MeshMatcapMaterial {

        super.copy(source)

        this.defines.apply {
            clear()
            put("MATCAP", "")
        }

        this.color.copy( source.color );

        this.matcap = source.matcap;

        this.map = source.map;

        this.bumpMap = source.bumpMap;
        this.bumpScale = source.bumpScale;

        this.normalMap = source.normalMap;
        this.normalMapType = source.normalMapType;
        this.normalScale.copy( source.normalScale );

        this.displacementMap = source.displacementMap;
        this.displacementScale = source.displacementScale;
        this.displacementBias = source.displacementBias;

        this.alphaMap = source.alphaMap;

        this.skinning = source.skinning;
        this.morphTargets = source.morphTargets;
        this.morphNormals = source.morphNormals;

        return this;

    }

}