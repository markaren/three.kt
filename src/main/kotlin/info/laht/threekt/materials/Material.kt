package info.laht.threekt.materials

import info.laht.threekt.*
import info.laht.threekt.core.EventDispatcher
import info.laht.threekt.math.Plane
import info.laht.threekt.math.generateUUID
import java.util.concurrent.atomic.AtomicInteger

open class Material(
    parameters: MaterialParameters? = null
) : EventDispatcher() {

    internal val program: Int? = null

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
    var blendDst = OneMinusSrcAlphaFactor

    /**
     * The tranparency of the .blendDst. Default is null.
     */
    var blendDstAlpha: Int? = null

    /**
     * Blending equation to use when applying blending. It's one of the constants defined in Three.js. Default is {@link AddEquation}.
     */
    var blendEquation = AddEquation

    /**
     * The transparency of the .blendEquation. Default is null.
     */
    var blendEquationAlpha: Int? = null

    /**
     * Which blending to use when displaying objects with this material. Default is {@link NormalBlending}.
     */
    var blending = NormalBlending

    /**
     * Blending source. It's one of the blending mode constants defined in Three.js. Default is {@link SrcAlphaFactor}.
     */
    var blendSrc = SrcAlphaFactor

    /**
     * The tranparency of the .blendSrc. Default is null.
     */
    var blendSrcAlpha: Int? = null

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
     * Whether to render the material's color. This can be used in conjunction with a mesh's .renderOrder property to create invisible objects that occlude other objects. Default is true.
     */
    var colorWrite = true

    /**
     * Which depth function to use. Default is {@link LessEqualDepth}. See the depth mode constants for all possible values.
     */
    var depthFunc = LessEqualDepth

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
    open var fog = true

    /**
     * Whether the material is affected by lights. Default is true.
     */
    open var lights = true

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
    var side = FrontSide

    var shadowSide: Int? = null

    /**
     * Defines whether this material is transparent. This has an effect on rendering as transparent objects need special treatment and are rendered after non-transparent objects.
     * When set to true, the extent to which the material is transparent is controlled by setting it's .opacity property.
     * Default is false.
     */
    var transparent = false

    /**
     * Defines whether vertex coloring is used. Default is THREE.NoColors. Other options are THREE.VertexColors and THREE.FaceColors.
     */
    var vertexColors = NoColors

    /**
     * Defines whether precomputed vertex tangents are used. Default is false.
     */
    var vertexTangents = false

    /**
     * Defines whether this material is visible. Default is true.
     */
    var visible = true

    var defines: MutableMap<String, String>? = null

    init {

        parameters?.also { source ->
            source.name?.also { this.name = it }

            source.fog?.also { this.fog = it }
            source.lights?.also { this.lights = it }

            source.blending?.also { this.blending = it }
            source.side?.also { this.side = it }
            source.shadowSide?.also { this.shadowSide = it }
            source.flatShading?.also { this.flatShading = it }
            source.vertexColors?.also { this.vertexColors = it }

            source.opacity?.also { this.opacity = it }
            source.transparent?.also { this.transparent = it }

            source.blendSrc?.also { this.blendSrc = it }
            source.blendDst?.also { this.blendDst = it }
            source.blendEquation?.also { this.blendEquation = it }
            source.blendSrcAlpha?.also { this.blendSrcAlpha = it }
            source.blendDstAlpha?.also { this.blendDstAlpha = it }
            source.blendEquationAlpha?.also { this.blendEquationAlpha = it }

            source.depthFunc?.also { this.depthFunc = it }
            source.depthTest?.also { this.depthTest = it }
            source.depthWrite?.also { this.depthWrite = it }

            source.colorWrite?.also { this.colorWrite = it }

            source.polygonOffset?.also { this.polygonOffset = it }
            source.polygonOffsetFactor?.also { this.polygonOffsetFactor = it }
            source.polygonOffsetUnits?.also { this.polygonOffsetUnits = it }

            source.dithering?.also { this.dithering = it }

            source.alphaTest?.also { this.alphaTest = it }
            source.premultipliedAlpha?.also { this.premultipliedAlpha = it }

            source.visible?.also { this.visible = it }

            source.clippingPlanes?.also { it -> this.clippingPlanes = it.map { p -> p.clone() } }
            source.clipShadows?.also { this.clipShadows = it }
            source.clipIntersection?.also { this.clipIntersection = it }

        }

    }

    /**
     * Return a new material with the same parameters as this material.
     */
    fun clone(): Material {
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
        dispatchEvent("dispose")
    }

    private companion object {
        var materialId = 0
    }

}

open class MaterialParameters {
    var alphaTest: Float? = null
    var blendDst: Int? = null
    var blendDstAlpha: Int? = null
    var blendEquation: Int? = null
    var blendEquationAlpha: Int? = null
    var blending: Int? = null
    var blendSrc: Int? = null
    var blendSrcAlpha: Int? = null
    var clipIntersection: Boolean? = null
    var clippingPlanes: List<Plane>? = null
    var clipShadows: Boolean? = null
    var colorWrite: Boolean? = null
    var depthFunc: Int? = null
    var depthTest: Boolean? = null
    var depthWrite: Boolean? = null
    var fog: Boolean? = null
    var lights: Boolean? = null
    var name: String? = null
    var opacity: Float? = null
    var polygonOffset: Boolean? = null
    var polygonOffsetFactor: Float? = null
    var polygonOffsetUnits: Float? = null
    var premultipliedAlpha: Boolean? = null
    var dithering: Boolean? = null
    var flatShading: Boolean? = null
    var side: Int? = null
    var shadowSide: Int? = null
    var transparent: Boolean? = null
    var vertexColors: Int? = null
    var visible: Boolean? = null
}
