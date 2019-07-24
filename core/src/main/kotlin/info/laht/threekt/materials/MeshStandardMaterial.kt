package info.laht.threekt.materials

import info.laht.threekt.TangentSpaceNormalMap
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector2
import info.laht.threekt.textures.Texture

open class MeshStandardMaterial: Material(), MaterialWithColor, MaterialWithEmissive {

    override val color = Color( 0xffffff ); // diffuse
    var roughness = 0.5f;
    var metalness = 0.5f;

    public override var map: Texture? = null;

    public override var lightMap: Texture? = null;
    public override var lightMapIntensity = 1f;

    public override var aoMap: Texture? = null;
    public override var aoMapIntensity = 1f;

    override var emissive = Color( 0x000000 );
    override var emissiveIntensity = 1f;
    public override var emissiveMap: Texture? = null;

    public override var bumpMap: Texture? = null;
    public override var bumpScale = 1f;

    public override var normalMap: Texture? = null;
    public override var normalMapType = TangentSpaceNormalMap;
    var normalScale = Vector2( 1, 1 );

    public override var displacementMap: Texture? = null;
    public override var displacementScale = 1f;
    public override var displacementBias = 0f;

    public override var roughnessMap: Texture? = null;

    public override var metalnessMap: Texture? = null;

    public override var alphaMap: Texture? = null;

    public override var envMap: Texture? = null;
    var envMapIntensity = 1f;

    var refractionRatio = 0.98f;

    var wireframe = false;
    var wireframeLinewidth = 1f;
    var wireframeLinecap = "round";
    var wireframeLinejoin = "round";

    var skinning = false;
    var morphTargets = false;
    var morphNormals = false;

    init {
        defines["STANDARD"] = ""
    }
    
}