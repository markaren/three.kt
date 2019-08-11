package info.laht.threekt.renderers.opengl

import info.laht.threekt.*
import info.laht.threekt.materials.Material
import info.laht.threekt.math.Vector4
import kotlinx.io.core.IoBuffer
import org.lwjgl.opengl.*
import java.nio.ByteBuffer
import kotlin.math.roundToInt

internal class GLState {

    val colorBuffer = GLColorBuffer().apply {
        setClear(0f, 0f, 0f, 1f)
    }

    val depthBuffer = GLDepthBuffer().apply {
        setClear(1.0)
    }

    val stencilBuffer = GLStencilBuffer().apply {
        setClear(0)
    }

    val maxVertexAttributes = GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS)
    val newAttributes = IntArray(maxVertexAttributes)
    val enabledAttributes = IntArray(maxVertexAttributes)
    val attributeDivisors = IntArray(maxVertexAttributes)

    private val enabledCapabilities = mutableMapOf<Int, Boolean>()

    var currentProgram: Int? = null

    var currentBlendingEnabled: Boolean? = null
    var currentBlending: Blending? = null
    var currentBlendEquation: BlendingEquation? = null
    var currentBlendSrc: BlendingFactor? = null
    var currentBlendDst: BlendingFactor? = null
    var currentBlendEquationAlpha: BlendingEquation? = null
    var currentBlendSrcAlpha: BlendingFactor? = null
    var currentBlendDstAlpha: BlendingFactor? = null
    var currentPremultipledAlpha: Boolean? = null

    var currentFlipSided: Boolean? = null
    var currentCullFace: CullFaceMode? = null

    var currentLineWidth: Float? = null

    var currentPolygonOffsetFactor: Float? = null
    var currentPolygonOffsetUnits: Float? = null

    val maxTextures = GL11.glGetInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS)

    var lineWidthAvailable = false

    var currentTextureSlot: Int? = null
    var currentBoundTextures = mutableMapOf<Int?, BoundTexture>()

    var currentScissor = Vector4()
    var currentViewport = Vector4()

    private val emptyTextures = mapOf(
        GL11.GL_TEXTURE_2D to createTexture(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_2D, 1),
        GL13.GL_TEXTURE_CUBE_MAP to createTexture(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_CUBE_MAP, 6)
    )

    init {
        enable(GL11.GL_DEPTH_TEST)
        depthBuffer.setFunc(DepthMode.LessEqualDepth)

        enable(GL11.GL_CULL_FACE)
        setBlending(Blending.None)
    }

    fun initAttributes() {
        for (i in 0 until newAttributes.size) {
            newAttributes[i] = 0
        }
    }

    fun enableAttribute(attribute: Int) {

        enableAttributeAndDivisor(attribute, 0)

    }

    fun enableAttributeAndDivisor(attribute: Int, meshPerAttribute: Int) {

        newAttributes[attribute] = 1

        if (enabledAttributes[attribute] == 0) {

            GL20.glEnableVertexAttribArray(attribute)
            enabledAttributes[attribute] = 1

        }

        if (attributeDivisors[attribute] != meshPerAttribute) {

            GL33.glVertexAttribDivisor(attribute, meshPerAttribute)
            attributeDivisors[attribute] = meshPerAttribute

        }

    }

    fun disableUnusedAttributes() {

        for (i in 0 until enabledAttributes.size) {

            if (enabledAttributes[i] != newAttributes[i]) {

                GL20.glDisableVertexAttribArray(i)
                enabledAttributes[i] = 0

            }

        }

    }


    private fun createTexture(type: Int, target: Int, count: Int): Int {

        val data = IntArray(4) // 4 is required to match default unpack alignment of 4.
        val texture = GL11.glGenTextures()

        GL11.glBindTexture(type, texture)
        GL11.glTexParameteri(type, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(type, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)

        for (i in 0 until count) {
            GL11.glTexImage2D(target + 1, 0, GL11.GL_RGBA, 1, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data)
        }

        return texture

    }

    fun enable(id: Int) {
        if (enabledCapabilities[id] != true) {
            GL11.glEnable(id)
            enabledCapabilities[id] = true
        }
    }

    fun disable(id: Int) {
        if (enabledCapabilities[id] != false) {
            GL11.glDisable(id)
            enabledCapabilities[id] = false
        }
    }

    fun useProgram(program: Int): Boolean {

        if (currentProgram != program) {

            GL20.glUseProgram(program)

            currentProgram = program

            return true

        }

        return false

    }

    @Suppress("NAME_SHADOWING")
    fun setBlending(
        blending: Blending,
        blendEquation: BlendingEquation? = null,
        blendSrc: BlendingFactor? = null,
        blendDst: BlendingFactor? = null,
        blendEquationAlpha: BlendingEquation? = null,
        blendSrcAlpha: BlendingFactor? = null,
        blendDstAlpha: BlendingFactor? = null,
        premultipliedAlpha: Boolean? = null
    ) {

        if (blending == Blending.None) {

            if (currentBlendingEnabled == true) {
                disable(GL11.GL_BLEND)
                currentBlendingEnabled = false
            }

            return

        }

        if (currentBlendingEnabled == null || currentBlendingEnabled == false) {

            enable(GL11.GL_BLEND)
            currentBlendingEnabled = true

        }

        if (blending != Blending.Custom) {

            if (blending != currentBlending || premultipliedAlpha != currentPremultipledAlpha) {

                if (currentBlendEquation != BlendingEquation.Add || currentBlendEquationAlpha != BlendingEquation.Add) {

                    GL14.glBlendEquation(GL14.GL_FUNC_ADD)

                    currentBlendEquation = BlendingEquation.Add
                    currentBlendEquationAlpha = BlendingEquation.Add

                }

                if (premultipliedAlpha == true) {

                    when (blending) {
                        Blending.Normal -> GL14.glBlendFuncSeparate(
                            GL11.GL_ONE,
                            GL11.GL_ONE_MINUS_SRC_ALPHA,
                            GL11.GL_ONE,
                            GL11.GL_ONE_MINUS_SRC_ALPHA
                        )
                        Blending.Additive -> GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE)
                        Blending.Subtractive -> GL14.glBlendFuncSeparate(
                            GL11.GL_ZERO,
                            GL11.GL_ZERO,
                            GL11.GL_ONE_MINUS_SRC_COLOR,
                            GL11.GL_ONE_MINUS_SRC_ALPHA
                        )
                        Blending.Multiply -> GL14.glBlendFuncSeparate(
                            GL11.GL_ZERO,
                            GL11.GL_SRC_COLOR,
                            GL11.GL_ZERO,
                            GL11.GL_SRC_ALPHA
                        )
                        else -> println("GLState: Invalid blending: $blending")
                    }

                } else {

                    when (blending) {
                        Blending.Normal -> GL14.glBlendFuncSeparate(
                            GL11.GL_SRC_ALPHA,
                            GL11.GL_ONE_MINUS_SRC_ALPHA,
                            GL11.GL_ONE,
                            GL11.GL_ONE_MINUS_SRC_ALPHA
                        )
                        Blending.Additive -> GL14.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
                        Blending.Subtractive -> GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR)
                        Blending.Multiply -> GL14.glBlendFunc(GL11.GL_ZERO, GL11.GL_SRC_COLOR)
                        else -> System.err.println("GLState: Invalid blending: $blending")
                    }

                }

                currentBlendSrc = null
                currentBlendDst = null
                currentBlendSrcAlpha = null
                currentBlendDstAlpha = null

                currentBlending = blending
                currentPremultipledAlpha = premultipliedAlpha

            }

            return

        }

        // custom blending

        val blendEquationAlpha = blendEquationAlpha ?: blendEquation
        val blendSrcAlpha = blendSrcAlpha ?: blendSrc
        val blendDstAlpha = blendDstAlpha ?: blendDst

        if (blendEquation != currentBlendEquation || blendEquationAlpha != currentBlendEquationAlpha) {

            GL20.glBlendEquationSeparate(
                GLUtils.convert(blendEquation?.value),
                GLUtils.convert(blendEquationAlpha?.value)
            )

            currentBlendEquation = blendEquation
            currentBlendEquationAlpha = blendEquationAlpha

        }

        if (blendSrc != currentBlendSrc || blendDst != currentBlendDst || blendSrcAlpha != currentBlendSrcAlpha || blendDstAlpha != currentBlendDstAlpha) {

            GL14.glBlendFuncSeparate(
                GLUtils.convert(blendSrc?.value),
                GLUtils.convert(blendDst?.value),
                GLUtils.convert(blendSrcAlpha?.value),
                GLUtils.convert(blendDstAlpha?.value)
            )

            currentBlendSrc = blendSrc
            currentBlendDst = blendDst
            currentBlendSrcAlpha = blendSrcAlpha
            currentBlendDstAlpha = blendDstAlpha

        }

        currentBlending = blending
        currentPremultipledAlpha = null
    }

    fun setMaterial(material: Material, frontFaceCW: Boolean) {

        if (material.side == Side.Double) {
            disable(GL11.GL_CULL_FACE)
        } else {
            enable(GL11.GL_CULL_FACE)
        }

        var flipSided = material.side == Side.Back
        if (frontFaceCW) {
            flipSided = !flipSided
        }

        setFlipSided(flipSided)

        if (material.blending == Blending.Normal && !material.transparent) {
            setBlending(Blending.None)
        } else {
            setBlending(
                material.blending,
                material.blendEquation,
                material.blendSrc,
                material.blendDst,
                material.blendEquationAlpha,
                material.blendSrcAlpha,
                material.blendDstAlpha,
                material.premultipliedAlpha
            )
        }

        depthBuffer.setFunc(material.depthFunc)
        depthBuffer.setTest(material.depthTest)
        depthBuffer.setMask(material.depthWrite)
        colorBuffer.setMask(material.colorWrite)

        setPolygonOffset(material.polygonOffset, material.polygonOffsetFactor, material.polygonOffsetUnits)

    }


    private fun setFlipSided(flipSided: Boolean) {

        if (currentFlipSided != flipSided) {

            if (flipSided) {
                GL11.glFrontFace(GL11.GL_CW)
            } else {
                GL11.glFrontFace(GL11.GL_CCW)
            }

            currentFlipSided = flipSided

        }

    }

    fun setCullFace(cullFace: CullFaceMode) {

        if (cullFace != CullFaceMode.None) {

            enable(GL11.GL_CULL_FACE)

            if (cullFace != currentCullFace) {

                when (cullFace) {
                    CullFaceMode.Back -> GL11.glCullFace(GL11.GL_BACK)
                    CullFaceMode.Front -> GL11.glCullFace(GL11.GL_FRONT)
                    else -> GL11.glCullFace(GL11.GL_FRONT_AND_BACK)
                }

            }

        } else {

            disable(GL11.GL_CULL_FACE)

        }

        currentCullFace = cullFace

    }

    fun setLineWidth(width: Float) {

        if (width != currentLineWidth) {

            if (lineWidthAvailable) GL11.glLineWidth(width)

            currentLineWidth = width

        }

    }

    fun setPolygonOffset(polygonOffset: Boolean, factor: Float? = null, units: Float? = null) {

        if (polygonOffset) {

            enable(GL11.GL_POLYGON_OFFSET_FILL)

            if (currentPolygonOffsetFactor != factor || currentPolygonOffsetUnits != units) {

                GL11.glPolygonOffset(factor!!, units!!)

                currentPolygonOffsetFactor = factor
                currentPolygonOffsetUnits = units

            }

        } else {
            disable(GL11.GL_POLYGON_OFFSET_FILL)
        }
    }

    fun setScissorTest(scissorTest: Boolean?) {
        if (scissorTest == true) {
            enable(GL11.GL_SCISSOR_TEST)
        } else {
            disable(GL11.GL_SCISSOR_TEST)
        }
    }

    fun activeTexture(glSlot: Int? = null) {

        @Suppress("NAME_SHADOWING")
        val glSlot = glSlot ?: GL13.GL_TEXTURE0 + maxTextures - 1

        if (currentTextureSlot != glSlot) {

            GL13.glActiveTexture(glSlot)
            currentTextureSlot = glSlot

        }

    }

    fun bindTexture(glType: Int, glTexture: Int?) {

        if (currentTextureSlot == null) {

            activeTexture()

        }

        var boundTexture = currentBoundTextures[currentTextureSlot]

        if (boundTexture == null) {

            boundTexture = BoundTexture(type = null, texture = null)
            currentBoundTextures[currentTextureSlot] = boundTexture

        }

        if (boundTexture.type != glType || boundTexture.texture != glTexture) {

            GL11.glBindTexture(glType, glTexture ?: emptyTextures[glType]!!)

            boundTexture.type = glType
            boundTexture.texture = glTexture

        }

    }

    fun texImage2D(
        target: Int,
        level: Int,
        internalFormat: Int,
        width: Int,
        height: Int,
        format: Int,
        type: Int,
        data: IoBuffer?
    ) {

        if (data == null) {
            //TODO should we even call this function here
            GL11.glTexImage2D(target, level, internalFormat, width, height, 0, format, type, null as ByteBuffer?)
        } else {
            data.readDirect { buffer ->
                GL11.glTexImage2D(target, level, internalFormat, width, height, 0, format, type, buffer)
            }
//            data.resetForRead() // TODO do we need to call this?
        }

    }

    fun scissor(scissor: Vector4) {

        if (currentScissor != scissor) {

            GL11.glScissor(
                scissor.x.roundToInt(),
                scissor.y.roundToInt(),
                scissor.z.roundToInt(),
                scissor.w.roundToInt()
            )
            currentScissor.copy(scissor)

        }

    }

    fun viewport(viewport: Vector4) {

        if (currentViewport != viewport) {

            GL11.glViewport(
                viewport.x.roundToInt(),
                viewport.y.roundToInt(),
                viewport.z.roundToInt(),
                viewport.w.roundToInt()
            )
            currentViewport.copy(viewport)

        }

    }

    fun reset() {

        enabledAttributes.forEachIndexed { i, v ->
            if (v == 1) {
                GL20.glDisableVertexAttribArray(i)
                enabledAttributes[i] = 0
            }
        }

        enabledCapabilities.clear()

        currentTextureSlot = null
        currentBoundTextures.clear()

        currentProgram = null

        currentBlending = null

        currentFlipSided = null
        currentCullFace = null

        colorBuffer.reset()
        depthBuffer.reset()
        stencilBuffer.reset()

    }

    inner class GLColorBuffer {

        internal var locked = false

        private var color = Vector4()
        private var currentColorMask: Boolean? = null
        private var currentColorClear = Vector4(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())

        fun setMask(colorMask: Boolean) {
            if (currentColorMask != colorMask && !locked) {

                GL11.glColorMask(colorMask, colorMask, colorMask, colorMask)
                currentColorMask = colorMask

            }
        }

        @Suppress("NAME_SHADOWING")
        fun setClear(r: Float, g: Float, b: Float, a: Float, premultipliedAlpha: Boolean? = null) {

            var r = r
            var g = g
            var b = b

            if (premultipliedAlpha == true) {
                r *= a; g *= a; b *= a
            }

            color.set(r, g, b, a)

            if (currentColorClear != color) {
                GL11.glClearColor(r, g, b, a)
                currentColorClear.copy(color)
            }
        }

        fun reset() {
            locked = false

            currentColorMask = null
            currentColorClear.set(-1f, 0f, 0f, 0f) // set to invalid state
        }

    }

    inner class GLDepthBuffer {

        internal var locked = false

        private var currentDepthMask: Boolean? = null
        private var currentDepthFunc: DepthMode? = null
        private var currentDepthClear: Double? = null

        fun setTest(depthTest: Boolean) {
            if (depthTest) {
                enable(GL11.GL_DEPTH_TEST)
            } else {
                disable(GL11.GL_DEPTH_TEST)
            }
        }

        fun setMask(depthMask: Boolean) {
            if (currentDepthMask != depthMask && !locked) {
                GL11.glDepthMask(depthMask)
                currentDepthMask = depthMask
            }
        }

        fun setFunc(depthFunc: DepthMode) {
            if (currentDepthFunc != depthFunc) {

                when (depthFunc) {
                    DepthMode.NeverDepth -> GL11.glDepthFunc(GL11.GL_NEVER)
                    DepthMode.AlwaysDepth -> GL11.glDepthFunc(GL11.GL_ALWAYS)
                    DepthMode.LessDepth -> GL11.glDepthFunc(GL11.GL_LESS)
                    DepthMode.LessEqualDepth -> GL11.glDepthFunc(GL11.GL_LEQUAL)
                    DepthMode.EqualDepth -> GL11.glDepthFunc(GL11.GL_EQUAL)
                    DepthMode.GreaterEqualDepth -> GL11.glDepthFunc(GL11.GL_GEQUAL)
                    DepthMode.GreaterDepth -> GL11.glDepthFunc(GL11.GL_GREATER)
                    DepthMode.NotEqualDepth -> GL11.glDepthFunc(GL11.GL_NOTEQUAL)
                }

                currentDepthFunc = depthFunc

            }
        }

        fun setClear(depth: Double) {
            if (currentDepthClear != depth) {

                GL11.glClearDepth(depth)
                currentDepthClear = depth

            }
        }

        fun reset() {
            locked = false

            currentDepthMask = null
            currentDepthFunc = null
            currentDepthClear = null
        }

    }

    inner class GLStencilBuffer {

        internal var locked = false

        var currentStencilMask: Int? = null
        var currentStencilFunc: Int? = null
        var currentStencilRef: Int? = null
        var currentStencilFuncMask: Int? = null
        var currentStencilFail: Int? = null
        var currentStencilZFail: Int? = null
        var currentStencilZPass: Int? = null
        var currentStencilClear: Int? = null

        fun setTest(stencilTest: Boolean) {
            if (stencilTest) {
                enable(GL11.GL_STENCIL_TEST)
            } else {
                disable(GL11.GL_STENCIL_TEST)
            }
        }

        fun setMask(stencilMask: Int) {
            if (currentStencilMask != stencilMask && !locked) {
                GL11.glStencilMask(stencilMask)
                currentStencilMask = stencilMask
            }
        }

        fun setFunc(stencilFunc: Int, stencilRef: Int, stencilMask: Int) {
            if (currentStencilFunc != stencilFunc ||
                currentStencilRef != stencilRef ||
                currentStencilFuncMask != stencilMask
            ) {

                GL11.glStencilFunc(stencilFunc, stencilRef, stencilMask)

                currentStencilFunc = stencilFunc
                currentStencilRef = stencilRef
                currentStencilFuncMask = stencilMask

            }
        }

        fun setOp(stencilFail: Int, stencilZFail: Int, stencilZPass: Int) {
            if (currentStencilFail != stencilFail ||
                currentStencilZFail != stencilZFail ||
                currentStencilZPass != stencilZPass
            ) {

                GL11.glStencilOp(stencilFail, stencilZFail, stencilZPass)

                currentStencilFail = stencilFail
                currentStencilZFail = stencilZFail
                currentStencilZPass = stencilZPass

            }
        }

        fun setClear(stencil: Int) {
            if (currentStencilClear != stencil) {

                GL11.glClearStencil(stencil)
                currentStencilClear = stencil

            }
        }

        fun reset() {
            locked = false

            currentStencilMask = null
            currentStencilFunc = null
            currentStencilRef = null
            currentStencilFuncMask = null
            currentStencilFail = null
            currentStencilZFail = null
            currentStencilZPass = null
            currentStencilClear = null
        }

    }

    data class BoundTexture(
        var type: Int?,
        var texture: Int?
    )

}
