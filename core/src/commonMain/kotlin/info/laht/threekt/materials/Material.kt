package info.laht.threekt.materials

import info.laht.threekt.*
import info.laht.threekt.core.EventDispatcher
import info.laht.threekt.core.EventDispatcherImpl
import info.laht.threekt.core.Shader
import info.laht.threekt.core.Uniform
import info.laht.threekt.math.Plane
import info.laht.threekt.math.generateUUID
import info.laht.threekt.renderers.Program
import info.laht.threekt.renderers.Renderer
import info.laht.threekt.textures.Texture
import info.laht.threekt.core.Cloneable as Cloneable1 // needed to avoid intelliJ from "optmizing" this import

open class Material : Cloneable1, EventDispatcher by EventDispatcherImpl() {

    /**
     * Material name. Default is an empty string.
     */
    var name = ""

    /**
     * Unique number of this material instance.
     */
    val id = materialId++

    /**
     * UUID of this material instance. This gets automatically assigned, so this shouldn't be edited.
     */
    val uuid = generateUUID()

    /**
     * Sets the alpha value to be used when running an alpha test. Default is 0.
     */
    var alphaTest = 0f

    /**
     * Blending destination. It's one of the blending mode constants defined in Three.js. Default is {@link OneMinusSrcAlphaFactor}.
     */
    var blendDst = BlendingFactor.OneMinusSrcAlpha

    /**
     * The tranparency of the .blendDst. Default is null.
     */
    var blendDstAlpha: BlendingFactor? = null

    /**
     * Blending equation to use when applying blending. It's one of the constants defined in Three.js. Default is {@link AddEquation}.
     */
    var blendEquation = BlendingEquation.Add

    /**
     * The transparency of the .blendEquation. Default is null.
     */
    var blendEquationAlpha: BlendingEquation? = null

    /**
     * Which blending to use when displaying objects with this material. Default is {@link NormalBlending}.
     */
    var blending = Blending.Normal

    /**
     * Blending source. It's one of the blending mode constants defined in Three.js. Default is {@link SrcAlphaFactor}.
     */
    var blendSrc = BlendingFactor.SrcAlpha

    /**
     * The tranparency of the .blendSrc. Default is null.
     */
    var blendSrcAlpha: BlendingFactor? = null

    /**
     * User-defined clipping planes specified as THREE.Plane objects in world space. These planes apply to the objects this material is attached to. Points in space whose signed distance to the plane is negative are clipped (not rendered). See the WebGL / clipping /intersection example. Default is null.
     */
    var clippingPlanes: List<Plane>? = null

    /**
     * Changes the behavior of clipping planes so that only their intersection is clipped, rather than their union. Default is false.
     */
    var clipIntersection = false

    /**
     * Defines whether to clip shadows according to the clipping planes specified on this material. Default is false.
     */
    var clipShadows = false

    /**
     * Whether to render the material's color. This can be used in conjunction with a mesh's .renderOrder property to createShader invisible objects that occlude other objects. Default is true.
     */
    var colorWrite = true

    /**
     * Which depth function to use. Default is {@link LessEqualDepth}. See the depth mode constants for all possible values.
     */
    var depthFunc = DepthMode.LessEqualDepth

    /**
     * Whether to have depth test enabled when rendering this material. Default is true.
     */
    var depthTest = true

    /**
     * Whether rendering this material has any effect on the depth buffer. Default is true.
     * When drawing 2D overlays it can be useful to disable the depth writing in order to layer several things together without creating z-index artifacts.
     */
    var depthWrite = true

    /**
     * Whether the material is affected by fog. Default is true.
     */
    var fog = true

    /**
     * Whether the material is affected by lights. Default is true.
     */
    var lights = true

    /**
     * Specifies that the material needs to be updated, WebGL wise. Set it to true if you made changes that need to be reflected in WebGL.
     * This property is automatically set to true when instancing a new material.
     */
    var needsUpdate = true

    /**
     * Opacity. Default is 1.
     */
    var opacity = 1f


    /**
     * Whether to use polygon offset. Default is false. This corresponds to the POLYGON_OFFSET_FILL WebGL feature.
     */
    var polygonOffset = false

    /**
     * Sets the polygon offset factor. Default is 0.
     */
    var polygonOffsetFactor = 0f

    /**
     * Sets the polygon offset units. Default is 0.
     */
    var polygonOffsetUnits = 0f

    /**
     * Whether to premultiply the alpha (transparency) value. See WebGL / Materials / Transparency for an example of the difference. Default is false.
     */
    var premultipliedAlpha = false

    /**
     * Whether to apply dithering to the color to remove the appearance of banding. Default is false.
     */
    var dithering = false

    /**
     * Define whether the material is rendered with flat shading. Default is false.
     */
    var flatShading = false

    /**
     * Defines which of the face sides will be rendered - front, back or both.
     * Default is THREE.FrontSide. Other options are THREE.BackSide and THREE.DoubleSide.
     */
    var side = Side.Front

    var shadowSide: Side? = null

    /**
     * Defines whether this material is transparent. This has an effect on rendering as transparent objects need special treatment and are rendered after non-transparent objects.
     * When set to true, the extent to which the material is transparent is controlled by setting it's .opacity property.
     * Default is false.
     */
    var transparent = false

    /**
     * Defines whether vertex coloring is used. Default is THREE.NoColors. Other options are THREE.VertexColors and THREE.FaceColors.
     */
    var vertexColors = Colors.None

    /**
     * Defines whether precomputed vertex tangents are used. Default is false.
     */
    var vertexTangents = false

    /**
     * Defines whether this material is visible. Default is true.
     */
    var visible = true

    val defines = mutableMapOf<String, Any>()

    internal open var map: Texture? = null

    internal open var matcap: Texture? = null

    internal open var alphaMap: Texture? = null

    internal open var lightMap: Texture? = null
    internal open var lightMapIntensity = 0f

    internal open var aoMap: Texture? = null
    internal open var aoMapIntensity = 0f

    internal open var bumpMap: Texture? = null
    internal open var bumpScale = 0f

    internal open var normalMap: Texture? = null
    internal open var normalMapType = NormalMapType.TangentSpace

    internal open var displacementMap: Texture? = null
    internal open var displacementScale = 0f
    internal open var displacementBias = 0f

    internal open var roughnessMap: Texture? = null

    internal open var metalnessMap: Texture? = null

    internal open var emissiveMap: Texture? = null

    internal open var specularMap: Texture? = null

    internal open var envMap: Texture? = null

    internal open var gradientMap: Texture? = null

    internal open var combine = TextureCombineOperation.Multiply

    internal var program: Program? = null

    internal var type = this::class.simpleName!!

    internal var index0AttributeName: String? = null

    internal open val uniforms: MutableMap<String, Uniform> = mutableMapOf()

    internal open var vertexShader: String = ""
    internal open var fragmentShader: String = ""

    var onBeforeCompile: ((Shader, Renderer) -> Unit)? = null

    internal fun getMapForType(type: String): Texture? {

        return when (type) {
            "map" -> map
            "matCap" -> matcap
            "alphaMap" -> alphaMap
            "lightMap" -> lightMap
            "aoMap" -> aoMap
            "bumpMap" -> bumpMap
            "normalMap" -> normalMap
            "displacementMap" -> displacementMap
            "roughnessMap" -> roughnessMap
            "metalnessMap" -> metalnessMap
            "emissiveMap" -> emissiveMap
            "specularMap" -> specularMap
            "envMap" -> envMap
            "gradientMap" -> gradientMap
            else -> throw IllegalArgumentException("No such map type: $type")
        }

    }

    internal fun setMapForType(type: String, value: Texture?) {

        when (type) {
            "map" -> map = value
            "matCap" -> matcap = value
            "alphaMap" -> alphaMap = value
            "lightMap" -> lightMap = value
            "aoMap" -> aoMap = value
            "bumpMap" -> bumpMap = value
            "normalMap" -> normalMap = value
            "displacementMap" -> displacementMap = value
            "roughnessMap" -> roughnessMap = value
            "metalnessMap" -> metalnessMap = value
            "emissiveMap" -> emissiveMap = value
            "specularMap" -> specularMap = value
            "envMap" -> envMap = value
            "gradientMap" -> gradientMap = value
            else -> throw IllegalArgumentException("No such map type: $type")
        }

    }

    /**
     * Return a new material with the same parameters as this material.
     */
    override fun clone(): Material {
        return Material().copy(this)
    }

    /**
     * Copy the parameters from the passed material into this material.
     * @param material
     */
    fun copy(source: Material): Material {
        this.name = source.name

        this.fog = source.fog
        this.lights = source.lights

        this.blending = source.blending
        this.side = source.side
        this.shadowSide = source.shadowSide
        this.flatShading = source.flatShading
        this.vertexColors = source.vertexColors

        this.opacity = source.opacity
        this.transparent = source.transparent

        this.blendSrc = source.blendSrc
        this.blendDst = source.blendDst
        this.blendEquation = source.blendEquation
        this.blendSrcAlpha = source.blendSrcAlpha
        this.blendDstAlpha = source.blendDstAlpha
        this.blendEquationAlpha = source.blendEquationAlpha

        this.depthFunc = source.depthFunc
        this.depthTest = source.depthTest
        this.depthWrite = source.depthWrite

        this.colorWrite = source.colorWrite

        this.polygonOffset = source.polygonOffset
        this.polygonOffsetFactor = source.polygonOffsetFactor
        this.polygonOffsetUnits = source.polygonOffsetUnits

        this.dithering = source.dithering

        this.alphaTest = source.alphaTest
        this.premultipliedAlpha = source.premultipliedAlpha

        this.visible = source.visible

        this.clipShadows = source.clipShadows
        this.clipIntersection = source.clipIntersection

        return this
    }

    /**
     * This disposes the material. Textures of a material don't get disposed. These needs to be disposed by {@link Texture}.
     */
    fun dispose() {
        dispatchEvent("dispose", this)
    }

    private companion object {
        var materialId = 0
    }

}
