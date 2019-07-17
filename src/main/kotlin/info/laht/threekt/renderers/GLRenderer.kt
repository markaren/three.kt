package info.laht.threekt.renderers

import info.laht.threekt.LinearToneMapping
import info.laht.threekt.Canvas
import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.BufferAttributes
import info.laht.threekt.core.Event
import info.laht.threekt.core.EventLister
import info.laht.threekt.core.Object3D
import info.laht.threekt.materials.Material
import info.laht.threekt.math.*
import info.laht.threekt.renderers.opengl.*
import info.laht.threekt.scenes.Scene
import info.laht.threekt.textures.Texture
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL45



class GLRenderer(
    private val window: Canvas,
    parameters: Parameters = Parameters()
) {

    private val capabilities = GLCapabilities()
    private val state = GLState()
    private val info: GLInfo = GLInfo()
    private val properties: GLProperties = GLProperties()
    private val textures = GLTextures(state, properties, capabilities, info)
    private val attributes = GLAttributes()
    private val geometries = GLGeometries(attributes, info)
    private val objects = GLObjects(geometries, info)
    private val programCache = GLPrograms(this, capabilities)
    private val renderLists = GLRenderLists()
    private val renderStates = GLRenderStates()

    private val background = GLBackground(this, state, parameters.preMultipliedAlpha)

    private val bufferRenderer = GLBufferRenderer(info, capabilities)
    private val indexedBufferRenderer = GLIndexedBufferRenderer(info, capabilities)

    var currentRenderList: GLRenderList? = null
    var currentRenderState: GLState? = null

    private val onMaterialDispose = OnMaterialDispose()

    // clearing
    var autoClear = true
    var autoClearColor = true
    var autoClearDepth = true
    var autoClearStencil = true

    // scene graph
    var sortObjects = true

    // user-defined clipping
    var clippingPlanes = mutableListOf<Plane>()
    var localClippingEnabled = false

    // physically based shading
    var gammaFactor = 2.toFloat()
    var gammaInput = false
    var gammaOutput = false

    // physical lights
    var physicallyCorrectLights = false

    // tone mapping
    var toneMapping = LinearToneMapping
    var toneMappingExposure = 1.toFloat()
    var toneMappingWhitePoint = 1.toFloat()

    // morphs
    var maxMorphTargets = 8
    var maxMorphNormals = 4

    private var frameBuffer: Int? = null

    private var currentActiveCubeFace = 0
    private var currentActiveMipmapLevel = 0
    private var currentRenderTarget: GLRenderTarget? = null
    private var currentFramebuffer: Int? = null
    private var currentMaterialId = -1

    private var currentViewport = Vector4i()
    private var currentScissor = Vector4i()
    private var currentScissorTest: Boolean? = null

    private var pixelRatio = 1

    private val viewport = Vector4i(0, 0, window.width, window.height)
    private val scissor = Vector4i(0, 0, window.width, window.height)
    private var scissorTest = false

    private val frustrum = Frustrum()

    private val clipping = GLClipping()
    private var clippingEnabled = false

    private val projScreenMatrix = Matrix4()
    private val vector3 = Vector3()

    fun setScissorTest(boolean: Boolean) {
        state.setScissorTest(boolean)
    }

    fun setClearColor(color: Color, alpha: Float = 1f) {
        background.setClearColor(color, alpha)
    }

    fun setClearAlpha(clearAlpha: Float) {
        background.clearAlpha = clearAlpha
    }

    fun clear(color: Boolean = true, depth: Boolean = true, stencil: Boolean = true) {
        var bits = 0
        if (color) bits = bits or GL_COLOR_BUFFER_BIT
        if (depth) bits = bits or GL_DEPTH_BUFFER_BIT
        if (stencil) bits = bits or GL_STENCIL_BUFFER_BIT
        glClear(bits)
    }

    fun clearColor() = clear(true, false, false)
    fun clearDepth() = clear(false, true, false)
    fun clearStencil() = clear(false, false, true)

    fun dispose() {
        renderLists.dispose()
        renderStates.dispose()
        properties.dispose()
        objects.dispose()
    }

    private fun deallocateMaterial( material: Material ) {
        releaseMaterialProgramReference(material)
        properties.remove(material)
    }

    private fun releaseMaterialProgramReference( material: Material ) {
        TODO()
    }

//    private fun renderObjectImmediate (`object`: ImmidiateRenderObject, program: Int) {
//
//        `object`.render {
//            renderBufferImmediate(`object`, program)
//        }
//
//    }

    private fun renderBufferImmediate(`object`: Object3D, program: GLProgram) {
        state.initAttributes()

        val buffers = properties[`object`] as BufferAttributes

        buffers.position?.also {
            GL45.glCreateBuffers()
        }
        buffers.normal?.also {
            GL45.glCreateBuffers()
        }
        buffers.uv?.also {
            GL45.glCreateBuffers()
        }
        buffers.color?.also {
            GL45.glCreateBuffers()
        }

//        val programAttributes = program.

    }

    fun render(scene: Scene, camera: Camera) {

//        val background = scene.background
//        val forceClear = false
//
//        if (background == null) {
//            state.colorBuffer.setClear(clearColor.r, clearColor.g, clearColor.b, clearAlpha)
//        }
//
//        if (autoClear || forceClear) {
//            clear(autoClearColor, autoClearDepth, autoClearStencil)
//        }

        GLFW.glfwPollEvents()
        GLFW.glfwSwapBuffers(window.pointer)

    }

    fun markUniformsLightsNeedsUpdate( uniforms: GLUniforms ) {
        TODO()
    }

    fun setFrameBuffer( value: Int ) {
        if (frameBuffer != value) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, value)
        }
        frameBuffer = value
    }

    fun getActiveCubeFace() = currentActiveCubeFace

    fun getActiveMipMapLevel() = currentActiveMipmapLevel

    fun getRenderTarget() = currentRenderTarget

    fun setRenderTarget( renderTarget: GLRenderTarget?, activeCubeFace: Int, activeMipMapLevel: Int ) {

        currentRenderTarget = renderTarget
        currentActiveCubeFace = activeCubeFace
        currentActiveMipmapLevel = activeMipMapLevel

        var isCube = false

        if (renderTarget != null ){

            when (renderTarget) {
                is GLRenderTargetCube -> {
                    TODO()
                    isCube = true
                }
                is GLMultisampleRenderTarget -> {
                    TODO()
                }
                else -> {
                    TODO()
                }
            }

            currentViewport.copy( renderTarget.viewport )
            currentScissor.copy( renderTarget.scissor )
            currentScissorTest = renderTarget.scissorTest

        } else {
            currentViewport.copy( viewport ).multiplyScalar( pixelRatio )
            currentScissor.copy( scissor ).multiplyScalar( pixelRatio )
            currentScissorTest = scissorTest
        }

        if (currentFramebuffer != frameBuffer) {

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer ?: 0)
            currentFramebuffer = frameBuffer

        }

        state.viewport( currentViewport )
        state.scissor( currentScissor )
        currentScissorTest?.also { state.setScissorTest(it) }

        if (isCube && renderTarget != null) {

            val textureProperties = properties[renderTarget.texture] as Texture
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + (activeCubeFace ?: 0), textureProperties["__webglTexture"]!!, activeMipMapLevel ?: 0)

        }

    }

    private inner class OnMaterialDispose: EventLister {
        override fun onEvent(evt: Event) {
            val material = evt.target as Material
            material.removeEventListener("dispose", this)
        }
    }

    data class Parameters(
        val alpha: Boolean = false,
        val depth: Boolean = true,
        val stencil: Boolean = true,
        val antialias: Boolean = false,
        val preMultipliedAlpha: Boolean = true,
        val preserveDrawingBuffer: Boolean = true

    )

}
