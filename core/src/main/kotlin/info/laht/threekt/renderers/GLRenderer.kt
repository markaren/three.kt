package info.laht.threekt.renderers

import info.laht.threekt.*
import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.*
import info.laht.threekt.extras.objects.ImmediateRenderObject
import info.laht.threekt.lights.Light
import info.laht.threekt.materials.*
import info.laht.threekt.math.*
import info.laht.threekt.objects.*
import info.laht.threekt.renderers.opengl.*
import info.laht.threekt.renderers.shaders.ShaderLib
import info.laht.threekt.renderers.shaders.cloneUniforms
import info.laht.threekt.scenes.Fog
import info.laht.threekt.scenes.FogExp2
import info.laht.threekt.scenes.Scene
import info.laht.threekt.scenes._Fog
import info.laht.threekt.textures.CubeTexture
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import kotlin.collections.set
import kotlin.math.max
import kotlin.math.min

class GLRenderer(
        size: WindowSize
) : Renderer {

    constructor(width: Int, height: Int) : this(WindowSize(width, height))

    var checkShaderErrors = false
    private val capabilities = GLCapabilities()
    internal val state = GLState()
    private val info: GLInfo = GLInfo()
    private val properties: GLProperties = GLProperties()
    private val textures = GLTextures(state, properties, capabilities, info)
    private val attributes = GLAttributes()
    private val geometries = GLGeometries(attributes, info)
    internal val objects = GLObjects(geometries, attributes, info)
    private val programCache = GLPrograms(this, capabilities)
    private val renderLists = GLRenderLists()
    private val renderStates = GLRenderStates()

    private val background = GLBackground(this, state, objects)

    private val bufferRenderer = GLBufferRenderer(info)
    private val indexedBufferRenderer = GLIndexedBufferRenderer(info)

    private var currentRenderList: GLRenderList? = null
    private var currentRenderState: GLRenderState? = null

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
    var gammaFactor = 2f
    var gammaInput = false
    var gammaOutput = false

    // physical lights
    var physicallyCorrectLights = false

    // tone mapping
    var toneMapping = ToneMapping.Linear
    var toneMappingExposure = 1f
    var toneMappingWhitePoint = 1f

    // morphs
    var maxMorphTargets = 8
    var maxMorphNormals = 4

    private var framebuffer: Int? = null

    private var currentActiveCubeFace: Int? = 0
    private var currentActiveMipmapLevel: Int? = 0
    private var currentRenderTarget: GLRenderTarget? = null
    private var currentFramebuffer: Int? = null
    private var currentMaterialId = -1

    private var currentGeometryProgram = GeometryProgram()

    private var currentCamera: Camera? = null

    private val currentViewport = Vector4()
    private val currentScissor = Vector4()
    private var currentScissorTest: Boolean? = null

    var width = size.width
        private set
    var height = size.height
        private set

    private var pixelRatio = 1

    private val viewport = Vector4(0, 0, width, height)
    private val scissor = Vector4(0, 0, width, height)
    private var scissorTest = false

    private val frustum = Frustum()

    private val clipping = GLClipping()
    private var clippingEnabled = false

    private val projScreenMatrix = Matrix4()
    private val vector3 = Vector3()

    val shadowMap = GLShadowMap(this, capabilities.maxTextureSize)

    init {

        state.scissor(currentScissor.copy(scissor).multiplyScalar(pixelRatio).floor())
        state.viewport(currentViewport.copy(viewport).multiplyScalar(pixelRatio).floor())

    }

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height

        setViewPort(0, 0, width, height)

    }

    fun setViewPort(x: Int, y: Int, width: Int, height: Int) {
        viewport.set(x, y, width, height)
        state.viewport(currentViewport.copy(viewport).multiplyScalar(pixelRatio).floor())
    }


    private fun getTargetPixelRatio(): Int {
        return if (currentRenderTarget == null) pixelRatio else 1
    }

    fun setScissor(x: Int, y: Int, width: Int, height: Int) {
        scissor.set(x, y, width, height)
        state.scissor(currentScissor.copy(scissor).multiplyScalar(pixelRatio).floor())
    }

    fun setScissorTest(boolean: Boolean) {
        state.setScissorTest(boolean).also {
            scissorTest = boolean
        }
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

    private fun deallocateMaterial(material: Material) {
        releaseMaterialProgramReference(material)
        properties.remove(material)
    }

    private fun releaseMaterialProgramReference(material: Material) {
        val programInfo = properties[material]["program"] as GLProgram?

        material.program = null

        if (programInfo != null) {

            programCache.releaseProgram(programInfo)

        }
    }

    internal fun renderBufferDirect(
            camera: Camera,
            fog: _Fog?,
            geometry: BufferGeometry,
            material: Material,
            `object`: Object3D,
            group: GeometryGroup?
    ) {
        val frontFaceCW = (`object` is Mesh && `object`.matrixWorld.determinant() < 0)

        state.setMaterial(material, frontFaceCW)

        val program = setProgram(camera, fog, material, `object`)

        var updateBuffers = false

        if (currentGeometryProgram.geometry != geometry.id ||
                currentGeometryProgram.program != program.id ||
                currentGeometryProgram.wireframe != (material is MaterialWithWireframe && material.wireframe)
        ) {

            currentGeometryProgram.geometry = geometry.id
            currentGeometryProgram.program = program.id
            currentGeometryProgram.wireframe = (material is MaterialWithWireframe && material.wireframe)
            updateBuffers = true

        }

        if (`object` is InstancedMesh) {
            updateBuffers = true
        }

        var index = geometry.index
        val position = geometry.attributes.position
        var rangeFactor = 1

        if (material is MaterialWithWireframe && material.wireframe) {

            index = geometries.getWireframeAttribute(geometry)
            rangeFactor = 2

        }

        var attribute: GLAttributes.Buffer? = null

        val renderer = if (index != null) {

            attribute = attributes.get(index)

            indexedBufferRenderer.also {
                it.setIndex(attribute)
            }

        } else {
            bufferRenderer
        }

        if (updateBuffers) {

            setupVertexAttributes(`object`, material, program, geometry)

            if (index != null) {

                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, attribute!!.buffer)

            }

        }

        var dataCount = Int.MAX_VALUE

        if (index != null) {

            dataCount = index.count

        } else if (position != null) {

            dataCount = position.count

        }

        val rangeStart = geometry.drawRange.start * rangeFactor
        val rangeCount = geometry.drawRange.count * rangeFactor

        val groupStart = if (group != null) group.start * rangeFactor else 0
        val groupCount = if (group != null) group.count * rangeFactor else Int.MAX_VALUE

        val drawStart = max(rangeStart, groupStart)
        val drawEnd = min(dataCount, min(rangeStart + rangeCount, groupStart + groupCount)) - 1

        val drawCount = max(0, drawEnd - drawStart + 1)

        if (drawCount == 0) return

        //

        if (`object` is Mesh) {

            if (material is MaterialWithWireframe && material.wireframe) {

                state.setLineWidth(material.wireframeLinewidth * getTargetPixelRatio())
                renderer.mode = GL_LINES

            } else {

                when (`object`.drawMode) {

                    DrawMode.Triangles -> renderer.mode = GL_TRIANGLES
                    DrawMode.TriangleStrip -> renderer.mode = GL_TRIANGLE_STRIP
                    DrawMode.TriangleFan -> renderer.mode = GL_TRIANGLE_FAN

                }

            }


        } else if (`object` is Line) {

            val lineWidth = if (material is MaterialWithLineWidth) material.linewidth else 1f

            state.setLineWidth(lineWidth * getTargetPixelRatio())

            when (`object`) {
                is LineSegments -> renderer.mode = GL_LINES
                is LineLoop -> renderer.mode = GL_LINE_LOOP
                else -> renderer.mode = GL_LINE_STRIP
            }

        } else if (`object` is Points) {

            renderer.mode = GL_POINTS

        } else if (`object` is Sprite) {

            renderer.mode = GL_TRIANGLES

        }

        if (`object` is InstancedMesh) {
            renderer.renderInstances(drawStart, drawCount, `object`.count)
        } /*else if (`object` is InstancedBufferGeometry) { //TODO Implement Object3D in InstancedBufferGeometry?
            renderer.renderInstances(drawStart, drawCount, `object`.maxInstancedCount)
        }*/ else {
            renderer.render(drawStart, drawCount)
        }

    }


    private fun renderObjects(
            renderList: List<GLRenderList.RenderItem>,
            scene: Scene,
            camera: Camera,
            overrideMaterial: Material? = null
    ) {

        renderList.forEach { renderItem ->

            val `object` = renderItem.`object`
            val geometry = renderItem.geometry
            val material = overrideMaterial ?: renderItem.material
            val group = renderItem.group

            renderObject(`object`, scene, camera, geometry, material, group)

        }

    }

    private fun setupVertexAttributes(`object`: Object3D, material: Material, program: GLProgram, geometry: BufferGeometry) {

        state.initAttributes()

        val geometryAttributes = geometry.attributes
        val programAttributes = program.attributes

        for (name in programAttributes.keys) {

            val programAttribute = programAttributes[name] ?: error("")

            if (programAttribute >= 0) {

                val geometryAttribute = geometryAttributes[name]

                if (geometryAttribute != null) {

                    val normalized = geometryAttribute.normalized
                    val size = geometryAttribute.itemSize

                    val attribute = attributes.get(geometryAttribute)

                    val buffer = attribute.buffer
                    val type = attribute.type

                    state.enableAttribute(programAttribute)
                    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer)
                    GL20.glVertexAttribPointer(programAttribute, size, type, normalized, 0, 0)


                } else if (name == "instanceMatrix") {
                    `object` as InstancedMesh
                    val attribute = attributes.get(`object`.instanceMatrix)

                    // TODO Attribute may not be available on context restore

                    if (attribute == null) {
                        continue
                    }

                    val buffer = attribute.buffer
                    val type = attribute.type

                    state.enableAttributeAndDivisor(programAttribute + 0, 1)
                    state.enableAttributeAndDivisor(programAttribute + 1, 1)
                    state.enableAttributeAndDivisor(programAttribute + 2, 1)
                    state.enableAttributeAndDivisor(programAttribute + 3, 1)

                    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer)

                    GL20.glVertexAttribPointer(programAttribute + 0, 4, type, false, 64, 0)
                    GL20.glVertexAttribPointer(programAttribute + 1, 4, type, false, 64, 16)
                    GL20.glVertexAttribPointer(programAttribute + 2, 4, type, false, 64, 32)
                    GL20.glVertexAttribPointer(programAttribute + 3, 4, type, false, 64, 48)
                } else if (name == "instanceColor") {
                    `object` as InstancedMesh
                    val attribute = attributes.get(`object`.instanceColor!!)

                    // TODO Attribute may not be available on context restore

                    if (attribute == null) {
                        continue
                    }

                    val buffer = attribute.buffer
                    val type = attribute.type

                    state.enableAttributeAndDivisor(programAttribute, 1)

                    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer)

                    GL20.glVertexAttribPointer(programAttribute, 3, type, false, 12, 0)
                } else if (material is MaterialWithDefaultAttributeValues) {

                    val value = material.defaultAttributeValues[name] as FloatArray?

                    if (value != null) {

                        when (value.size) {
                            2 -> GL20.glVertexAttrib2fv(programAttribute, value)
                            3 -> GL20.glVertexAttrib3fv(programAttribute, value)
                            4 -> GL20.glVertexAttrib4fv(programAttribute, value)
                            else -> GL20.glVertexAttrib1fv(programAttribute, value)

                        }

                    }

                }

            }

        }

        state.disableUnusedAttributes()

    }

    fun compile(scene: Scene, camera: Camera) {

        this.currentRenderState = renderStates.get(scene, camera)
        val currentRenderState = this.currentRenderState!!
        currentRenderState.init()

        scene.traverse { `object` ->

            if (`object` is Light) {

                currentRenderState.pushLight(`object`)

                if (`object`.castShadow) {

                    currentRenderState.pushShadow(`object`)

                }

            }

        }

        currentRenderState.setupLights(camera)

        scene.traverse { `object` ->

            if (`object` is MaterialObject) {

                if (`object` is MaterialsObject && `object`.isMultiMaterial) {

                    `object`.materials.forEach { material ->

                        initMaterial(material, scene.fog, `object`)

                    }

                } else {

                    initMaterial(`object`.material, scene.fog, `object`)

                }

            }

        }

    }

    fun render(scene: Scene, camera: Camera) {

        currentGeometryProgram.geometry = null
        currentGeometryProgram.program = null
        currentGeometryProgram.wireframe = false
        currentMaterialId = -1
        currentCamera = null

        if (scene.autoUpdate) {
            scene.updateMatrixWorld()
        }

        if (camera.parent == null) {
            camera.updateMatrixWorld()
        }

        this.currentRenderState = renderStates.get(scene, camera)
        val currentRenderState = this.currentRenderState!!
        currentRenderState.init()

        scene.onBeforeRenderScene?.invoke(this, scene, camera, currentRenderTarget)

        projScreenMatrix.multiplyMatrices(camera.projectionMatrix, camera.matrixWorldInverse)
        frustum.setFromMatrix(projScreenMatrix)

//        localClippingEnabled = this.localClippingEnabled
        clippingEnabled = clipping.init(this.clippingPlanes, localClippingEnabled, camera)

        this.currentRenderList = renderLists.get(scene, camera)
        val currentRenderList = this.currentRenderList!!
        currentRenderList.init()

        projectObject(scene, camera, 0, this.sortObjects)

        if (this.sortObjects) {
            currentRenderList.sort()
        }

        if (clippingEnabled) {
            clipping.beginShadows()
        }

        val shadowsArray = currentRenderState.shadowsArray

        shadowMap.render(shadowsArray, scene, camera)

        currentRenderState.setupLights(camera)

        if (clippingEnabled) {
            clipping.endShadows()
        }

        if (this.info.autoReset) {
            this.info.reset()
        }

        background.render(currentRenderList, scene, camera, false)

        val opaqueObjects = currentRenderList.opaque
        val transparentObjects = currentRenderList.transparent

        if (scene.overrideMaterial != null) {

            val overrideMaterial = scene.overrideMaterial

            if (opaqueObjects.isNotEmpty()) renderObjects(opaqueObjects, scene, camera, overrideMaterial)
            if (transparentObjects.isNotEmpty()) renderObjects(transparentObjects, scene, camera, overrideMaterial)

        } else {

            // opaque pass (front-to-back order)
            if (opaqueObjects.isNotEmpty()) renderObjects(opaqueObjects, scene, camera)

            // transparent pass (back-to-front order)
            if (transparentObjects.isNotEmpty()) renderObjects(transparentObjects, scene, camera)

        }

        currentRenderTarget?.also {
            // Generate mipmap if we're using any kind of mipmap filtering
            textures.updateRenderTargetMipmap(it)

            // resolve multisample renderbuffers to a single-sample texture if necessary
            textures.updateMultisampleRenderTarget(it)
        }

        state.depthBuffer.setTest(true)
        state.depthBuffer.setMask(true)
        state.colorBuffer.setMask(true)

        state.setPolygonOffset(false)

        this.currentRenderList = null
        this.currentRenderState = null

    }

    private fun projectObject(`object`: Object3D, camera: Camera, groupOrder: Int, sortObjects: Boolean) {

        if (!`object`.visible) return

        val visible = `object`.layers.test(camera.layers)

        val currentRenderState = this.currentRenderState!!
        val currentRenderList = this.currentRenderList!!

        if (visible) {

            @Suppress("NAME_SHADOWING")
            var groupOrder = groupOrder

            if (`object` is Group) {

                groupOrder = `object`.renderOrder

            } else if (`object` is LOD) {

                if (`object`.autoUpdate) {
                    `object`.update(camera)
                }

            } else if (`object` is Light) {

                currentRenderState.pushLight(`object`)

                if (`object`.castShadow) {

                    currentRenderState.pushShadow(`object`)

                }

            } else if (`object` is Sprite) {

                if (!`object`.frustumCulled || frustum.intersectsSprite(`object`)) {

                    if (sortObjects) {

                        vector3.setFromMatrixPosition(`object`.matrixWorld)
                                .applyMatrix4(projScreenMatrix)

                    }

                    val geometry = objects.update(`object`)
                    val material = `object`.material

                    if (material.visible) {

                        currentRenderList.push(`object`, geometry, material, groupOrder, vector3.z, null)

                    }

                }

            } else if (`object` is Mesh || `object` is Line || `object` is Points) {

                `object` as MaterialObject

                if (!`object`.frustumCulled || frustum.intersectsObject(`object`)) {

                    if (sortObjects) {

                        vector3.setFromMatrixPosition(`object`.matrixWorld)
                                .applyMatrix4(projScreenMatrix)

                    }

                    val geometry = objects.update(`object`)

                    if (`object` is MaterialsObject && `object`.isMultiMaterial) {

                        geometry.groups.forEach { group ->

                            val groupMaterial = `object`.materials.getOrNull(group.materialIndex)

                            if (groupMaterial != null && groupMaterial.visible) {

                                currentRenderList.push(
                                        `object`,
                                        geometry,
                                        groupMaterial,
                                        groupOrder,
                                        vector3.z,
                                        group
                                )

                            }

                        }

                    } else if (`object`.material.visible) {

                        currentRenderList.push(`object`, geometry, `object`.material, groupOrder, vector3.z, null)

                    }

                }

            }

        }

        `object`.children.forEach { child ->

            projectObject(child, camera, groupOrder, sortObjects)

        }

    }

    fun setFrameBuffer(value: Int) {
        if (framebuffer != value) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, value)
        }
        framebuffer = value
    }

    fun getActiveCubeFace(): Int? {
        return currentActiveCubeFace
    }

    fun getActiveMipmapLevel(): Int? {
        return currentActiveMipmapLevel
    }

    fun getRenderTarget(): GLRenderTarget? {
        return currentRenderTarget
    }

    fun setRenderTarget(renderTarget: GLRenderTarget?, activeCubeFace: Int? = null, activeMipMapLevel: Int? = null) {

        currentRenderTarget = renderTarget
        currentActiveCubeFace = activeCubeFace
        currentActiveMipmapLevel = activeMipMapLevel

        if (renderTarget != null && properties[renderTarget]["__webglFramebuffer"] == null) {
            textures.setupRenderTarget(renderTarget)
        }

        var isCube = false
        var framebuffer = this.framebuffer

        if (renderTarget != null) {

            val __webglFramebuffer = properties[renderTarget]["__webglFramebuffer"]!!

            when (renderTarget) {
                is GLRenderTargetCube -> {
                    framebuffer = (__webglFramebuffer as IntArray)[activeCubeFace ?: 0]
                    isCube = true
                }
                is GLMultisampleRenderTarget -> {
                    framebuffer = properties[renderTarget]["__webglMultisampledFramebuffer"] as Int
                }
                else -> {
                    framebuffer = __webglFramebuffer as Int
                }
            }

            currentViewport.copy(renderTarget.viewport)
            currentScissor.copy(renderTarget.scissor)
            currentScissorTest = renderTarget.scissorTest

        } else {

            currentViewport.copy(viewport).multiplyScalar(pixelRatio).floor()
            currentScissor.copy(scissor).multiplyScalar(pixelRatio).floor()
            currentScissorTest = scissorTest

        }

        if (currentFramebuffer != framebuffer) {

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer ?: 0)
            currentFramebuffer = framebuffer

        }

        state.viewport(currentViewport)
        state.scissor(currentScissor)
        state.setScissorTest(currentScissorTest)

        if (isCube) {

            val textureProperties = properties[renderTarget!!.texture]
            GL30.glFramebufferTexture2D(
                    GL30.GL_FRAMEBUFFER,
                    GL30.GL_COLOR_ATTACHMENT0,
                    GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + (activeCubeFace ?: 0),
                    textureProperties["__webglTexture"] as Int, activeMipMapLevel ?: 0
            )

        }

    }

    private fun renderObject(
            `object`: Object3D,
            scene: Scene,
            camera: Camera,
            geometry: BufferGeometry,
            material: Material,
            group: GeometryGroup?
    ) {

        `object`.onBeforeRender?.invoke(this, scene, camera, geometry, material, group)
        currentRenderState = renderStates.get(scene, camera)

        `object`.modelViewMatrix.multiplyMatrices(camera.matrixWorldInverse, `object`.matrixWorld)
        `object`.normalMatrix.getNormalMatrix(`object`.modelViewMatrix)

        if (`object` is ImmediateRenderObject) {

            if (sortObjects) {
                vector3.setFromMatrixPosition(`object`.matrixWorld)
                        .applyMatrix4(projScreenMatrix)
            }

        }

        renderBufferDirect(camera, scene.fog, geometry, material, `object`, group)

        `object`.onAfterRender?.invoke(this, scene, camera, geometry, material, group)
        currentRenderState = renderStates.get(scene, camera)

    }

    private fun initMaterial(material: Material, fog: _Fog?, `object`: Object3D) {

        val materialProperties = properties[material]

        val lights = currentRenderState!!.lights
        val shadowsArray = currentRenderState!!.shadowsArray

        val lightsStateVersion = lights.state.version

        val parameters = programCache.getParameters(
                material, lights.state, shadowsArray, fog, clipping.numPlanes, clipping.numIntersection, `object`
        )

        var code = programCache.getProgramCode(material, parameters)

        var program = materialProperties["program"] as GLProgram?
        var programChange = true

        when {
            program == null -> material.addEventListener("dispose", onMaterialDispose)
            program.code != code -> releaseMaterialProgramReference(material)
            materialProperties["lightsStateVersion"] != lightsStateVersion -> {
                materialProperties["lightsStateVersion"] = lightsStateVersion
                programChange = false
            }
            parameters.shaderID != null -> // same glsl and uniform list
                return
            else -> programChange = false
        }

        if (programChange) {

            if (parameters.shaderID != null) {

                val shader = ShaderLib[parameters.shaderID]

                materialProperties["shader"] = Shader(
                        name = material.type,
                        uniforms = cloneUniforms(shader.uniforms),
                        vertexShader = shader.vertexShader,
                        fragmentShader = shader.fragmentShader
                )

            } else {

                materialProperties["shader"] = Shader(
                        name = material.type,
                        uniforms = material.uniforms,
                        vertexShader = material.vertexShader,
                        fragmentShader = material.fragmentShader
                )

            }

            val shader = materialProperties["shader"] as Shader

            material.onBeforeCompile?.invoke(shader, this).also {

                // Computing code again as onBeforeCompile may have changed the shaders
                code = programCache.getProgramCode(material, parameters)

            }

            program = programCache.acquireProgram(material, shader, parameters, code)

            materialProperties["program"] = program
            material.program = program

        }

        val uniforms = materialProperties.getAs<Shader>("shader")!!.uniforms

        if (material !is ShaderMaterial &&
                material !is RawShaderMaterial ||
                (material is MaterialWithClipping && material.clipping)
        ) {

            materialProperties["numClippingPlanes"] = clipping.numPlanes
            materialProperties["numIntersection"] = clipping.numIntersection
            uniforms["clippingPlanes"] = clipping.uniform

        }

        materialProperties["vertexAlphas"] = parameters.vertexAlphas

        materialProperties["fog"] = fog

        // store the light setup it was created for

        materialProperties["lightsStateVersion"] = lightsStateVersion

        if (material.lights) {

            // wire up the material to this renderer"s lighting state

            uniforms["ambientLightColor"]!!.value = lights.state.ambient
            uniforms["lightProbe"]!!.value = lights.state.probe
            uniforms["directionalLights"]!!.value = lights.state.directional
            uniforms["spotLights"]!!.value = lights.state.spot
            uniforms["rectAreaLights"]!!.value = lights.state.rectArea
            uniforms["pointLights"]!!.value = lights.state.point
            uniforms["hemisphereLights"]!!.value = lights.state.hemi

            uniforms["directionalShadowMap"]!!.value = lights.state.directionalShadowMap
            uniforms["directionalShadowMatrix"]!!.value = lights.state.directionalShadowMatrix
            uniforms["spotShadowMap"]!!.value = lights.state.spotShadowMap
            uniforms["spotShadowMatrix"]!!.value = lights.state.spotShadowMatrix
            uniforms["pointShadowMap"]!!.value = lights.state.pointShadowMap
            uniforms["pointShadowMatrix"]!!.value = lights.state.pointShadowMatrix

        }

        val progUniforms = materialProperties.getAs<GLProgram>("program")!!.uniforms
        val uniformsList = GLUniforms.seqWithValue(progUniforms.seq, uniforms)

        materialProperties["uniformsList"] = uniformsList

    }

    private fun setProgram(camera: Camera, fog: _Fog?, material: Material, `object`: Object3D): GLProgram {

        textures.resetTextureUnits()

        val vertexAlphas = material.vertexColors == Colors.Vertex && `object` is GeometryObject && `object`.geometry.attributes.color?.itemSize == 4

        val materialProperties = properties[material]
        val lights = currentRenderState!!.lights

        if (clippingEnabled) {

            if (localClippingEnabled || camera != currentCamera) {

                val useCache =
                        camera == currentCamera &&
                                material.id == currentMaterialId

                // we might want to call this fun with some ClippingGroup
                // object instead of the material, once it becomes feasible
                // (#8465, #8379)
                clipping.setState(
                        material.clippingPlanes, material.clipIntersection, material.clipShadows,
                        camera, materialProperties, useCache
                )

            }

        }

        if (!material.needsUpdate) {

            if (materialProperties["program"] == null) {

                material.needsUpdate = true

            } else if (material.fog && materialProperties["fog"] != fog) {

                material.needsUpdate = true

            } else if (material.lights && materialProperties["lightsStateVersion"] != lights.state.version) {

                material.needsUpdate = true

            } else if (materialProperties["numClippingPlanes"] != null &&
                    (materialProperties["numClippingPlanes"] != clipping.numPlanes ||
                            materialProperties["numIntersection"] != clipping.numIntersection)
            ) {

                material.needsUpdate = true

            } else if (materialProperties["vertexAlphas"] != vertexAlphas) {
                material.needsUpdate = true
            }

        }

        if (material.needsUpdate) {

            initMaterial(material, fog, `object`)
            material.needsUpdate = false

        }

        var refreshProgram = false
        var refreshMaterial = false
        var refreshLights = false

        val program = materialProperties["program"] as GLProgram
        val p_uniforms = program.uniforms
        val m_uniforms = materialProperties.getAs<Shader>("shader")!!.uniforms

        if (state.useProgram(program.program)) {

            refreshProgram = true
            refreshMaterial = true
            refreshLights = true

        }

        if (material.id != currentMaterialId) {

            currentMaterialId = material.id

            refreshMaterial = true

        }

        if (refreshProgram || currentCamera != camera) {

            p_uniforms.setValue("projectionMatrix", camera.projectionMatrix)

            if (currentCamera != camera) {

                currentCamera = camera

                // lighting uniforms depend on the camera so enforce an update
                // now, in case this material supports lights - or later, when
                // the next material that does gets activated:

                refreshMaterial = true        // set to true on material change
                refreshLights = true        // remains set until update done

            }

            // load material specific uniforms
            // (shader material also gets them for the sake of genericity)

            if (material is ShaderMaterial ||
                    material is MeshPhongMaterial ||
                    material is MeshStandardMaterial ||
                    material.envMap != null
            ) {

                p_uniforms.map["cameraPosition"]?.setValue(
                        vector3.setFromMatrixPosition(camera.matrixWorld)
                )
            }

            if (material is MeshPhongMaterial ||
                    material is MeshLambertMaterial ||
                    material is MeshBasicMaterial ||
                    material is MeshStandardMaterial ||
                    material is ShaderMaterial ||
                    (material is MaterialWithSkinning && material.skinning)
            ) {

                p_uniforms.setValue("viewMatrix", camera.matrixWorldInverse)

            }

        }

        // skinning uniforms must be set even if material didn"t change
        // auto-setting of texture unit for bone texture must go before other textures
        // not sure why, but otherwise weird things happen

        if (material is MaterialWithSkinning && material.skinning) {
            TODO()
        }

        if (refreshMaterial) {

            p_uniforms.setValue("toneMappingExposure", this.toneMappingExposure)
            p_uniforms.setValue("toneMappingWhitePoint", this.toneMappingWhitePoint)

            if (material.lights) {

                // the current material requires lighting info

                // note: all lighting uniforms are always set correctly
                // they simply reference the renderer"s state for their
                // values
                //
                // use the current material"s .needsUpdate flags to set
                // the GL state when required

                markUniformsLightsNeedsUpdate(m_uniforms, refreshLights)

            }

            // refresh uniforms common to several materials

            if (fog != null && material.fog) {

                refreshUniformsFog(m_uniforms, fog)

            }

            if (material is MeshBasicMaterial) {

                refreshUniformsCommon(m_uniforms, material)

            } else if (material is MeshLambertMaterial) {

                refreshUniformsCommon(m_uniforms, material)
                refreshUniformsLambert(m_uniforms, material)

            } else if (material is MeshPhongMaterial) {

                refreshUniformsCommon(m_uniforms, material)

                if (material is MeshToonMaterial) {

                    refreshUniformsToon(m_uniforms, material)

                } else {

                    refreshUniformsPhong(m_uniforms, material)

                }

            } else if (material is MeshStandardMaterial) {

                refreshUniformsCommon(m_uniforms, material)

                if (material is MeshPhysicalMaterial) {

                    refreshUniformsPhysical(m_uniforms, material)

                } else {

                    refreshUniformsStandard(m_uniforms, material)

                }

            } else if (material is MeshMatcapMaterial) {

                refreshUniformsCommon(m_uniforms, material)

                refreshUniformsMatcap(m_uniforms, material)

            } else if (material is MeshDepthMaterial) {

                refreshUniformsCommon(m_uniforms, material)
                refreshUniformsDepth(m_uniforms, material)

            } else if (material is MeshDistanceMaterial) {

                refreshUniformsCommon(m_uniforms, material)
                refreshUniformsDistance(m_uniforms, material)

            } else if (material is MeshNormalMaterial) {

                refreshUniformsCommon(m_uniforms, material)
                refreshUniformsNormal(m_uniforms, material)

            } else if (material is LineBasicMaterial) {

                refreshUniformsLine(m_uniforms, material)

                if (material is LineDashedMaterial) {

                    refreshUniformsDash(m_uniforms, material)

                }

            } else if (material is PointsMaterial) {

                refreshUniformsPoints(m_uniforms, material)

            } else if (material is SpriteMaterial) {

                refreshUniformsSprites(m_uniforms, material)

            } else if (material is ShadowMaterial) {

                m_uniforms["color"]?.value<Color>()?.copy(material.color)
                m_uniforms["opacity"]?.value = material.opacity

            }

            GLUniforms.upload(materialProperties.getAs("uniformsList")!!, m_uniforms, textures)

        }

        if (material is ShaderMaterial && material.uniformsNeedUpdate) {

            GLUniforms.upload(materialProperties.getAs("uniformsList")!!, m_uniforms, textures)
            material.uniformsNeedUpdate = false

        }

        if (material is SpriteMaterial) {

            p_uniforms.setValue("center", (`object` as Sprite).center)

        }

        // common matrices

        p_uniforms.setValue("modelViewMatrix", `object`.modelViewMatrix)
        p_uniforms.setValue("normalMatrix", `object`.normalMatrix)
        p_uniforms.setValue("modelMatrix", `object`.matrixWorld)

        return program

    }

    private fun refreshUniformsCommon(uniforms: Map<String, Uniform>, material: Material) {

        uniforms["opacity"]?.value = material.opacity

        if (material is MaterialWithColor) {

            uniforms["diffuse"]?.value<Color>()?.copy(material.color)

        }

        if (material is MaterialWithEmissive) {

            uniforms["emissive"]?.value<Color>()?.copy(material.emissive)?.multiplyScalar(material.emissiveIntensity)

        }

        if (material.map != null) {

            uniforms["map"]?.value = material.map

        }

        if (material.alphaMap != null) {

            uniforms["alphaMap"]?.value = material.alphaMap

        }

        if (material.specularMap != null) {

            uniforms["specularMap"]?.value = material.specularMap

        }

        material.envMap?.also { envMap ->


            uniforms["envMap"]?.value = envMap

            // don't flip CubeTexture envMaps, flip everything else:
            //  WebGLRenderTargetCube will be flipped for backwards compatibility
            //  WebGLRenderTargetCube.texture will be flipped because it's a Texture and NOT a CubeTexture
            // this check must be handled differently, or removed entirely, if WebGLRenderTargetCube uses a CubeTexture in the future
            uniforms["flipEnvMap"]?.value = if (envMap is CubeTexture) -1 else 1

            if (material is MaterialWithReflectivity) {
                uniforms["reflectivity"]?.value = material.reflectivity
                uniforms["refractionRatio"]?.value = material.refractionRatio
            }

            uniforms["maxMipLevel"]?.value = properties[envMap]["__maxMipLevel"]

        }

        if (material.lightMap != null) {

            uniforms["lightMap"]?.value = material.lightMap
            uniforms["lightMapIntensity"]?.value = material.lightMapIntensity

        }

        if (material.aoMap != null) {

            uniforms["aoMap"]?.value = material.aoMap
            uniforms["aoMapIntensity"]?.value = material.aoMapIntensity

        }

        // uv repeat and offset setting priorities
        // 1. color map
        // 2. specular map
        // 3. normal map
        // 4. bump map
        // 5. alpha map
        // 6. emissive map

//        var uvScaleMap: Texture;

        // backwards compatibility
        val uvScaleMap = when {
            material.map != null -> material.map!!
            material.specularMap != null -> material.specularMap!!
            material.displacementMap != null -> material.displacementMap!!
            material.normalMap != null -> material.normalMap!!
            material.bumpMap != null -> material.bumpMap!!
            material.roughnessMap != null -> material.roughnessMap!!
            material.metalnessMap != null -> material.metalnessMap!!
            material.alphaMap != null -> material.alphaMap!!
            material.emissiveMap != null -> material.emissiveMap!!
            else -> null
        }

        if (uvScaleMap != null) {

            if (uvScaleMap.matrixAutoUpdate) {

                uvScaleMap.updateMatrix()

            }

            uniforms["uvTransform"]?.value<Matrix3>()?.copy(uvScaleMap.matrix)

        }

    }

    private fun refreshUniformsLine(uniforms: Map<String, Uniform>, material: LineBasicMaterial) {

        uniforms["diffuse"]?.value<Color>()?.copy(material.color)
        uniforms["opacity"]?.value = material.opacity

    }

    private fun refreshUniformsDash(uniforms: Map<String, Uniform>, material: LineDashedMaterial) {

        val dashSize = material.dashSize
        uniforms["dashSize"]?.value = dashSize
        uniforms["totalSize"]?.value = dashSize + material.gapSize
        uniforms["scale"]?.value = material.scale

    }

    private fun refreshUniformsPoints(uniforms: Map<String, Uniform>, material: PointsMaterial) {

        (uniforms["diffuse"]?.value as Color).copy(material.color)
        uniforms["opacity"]?.value = material.opacity
        uniforms["size"]?.value = material.size * pixelRatio
        uniforms["scale"]?.value = height * 0.5f

        uniforms["map"]?.value = material.map

        material.map?.also { map ->

            if (map.matrixAutoUpdate) {

                map.updateMatrix()

            }

            uniforms["uvTransform"]?.value<Matrix3>()?.copy(map.matrix)

        }

    }

    private fun refreshUniformsSprites(uniforms: Map<String, Uniform>, material: SpriteMaterial) {

        uniforms["diffuse"]?.value<Color>()?.copy(material.color)
        uniforms["opacity"]?.value = material.opacity
        uniforms["rotation"]?.value = material.rotation
        uniforms["map"]?.value = material.map

        material.map?.also { map ->

            if (map.matrixAutoUpdate) {

                map.updateMatrix()

            }

            uniforms["uvTransform"]?.value<Matrix3>()?.copy(map.matrix)

        }

    }

    private fun refreshUniformsFog(uniforms: Map<String, Uniform>, fog: _Fog) {

        uniforms["fogColor"]?.value<Color>()?.copy(fog.color)

        when (fog) {
            is Fog -> {
                uniforms["fogNear"]?.value = fog.near
                uniforms["fogFar"]?.value = fog.far
            }
            is FogExp2 -> uniforms["fogDensity"]?.value = fog.density
        }

    }

    private fun refreshUniformsLambert(uniforms: Map<String, Uniform>, material: MeshLambertMaterial) {

        if (material.emissiveMap != null) {

            uniforms["emissiveMap"]?.value = material.emissiveMap

        }

    }

    private fun refreshUniformsPhong(uniforms: Map<String, Uniform>, material: MeshPhongMaterial) {

        uniforms["specular"]?.value<Color>()?.copy(material.specular)
        uniforms["shininess"]?.value = max(material.shininess, 1e-4f) // to prevent pow( 0.0, 0.0 )

        if (material.emissiveMap != null) {

            uniforms["emissiveMap"]?.value = material.emissiveMap

        }

        if (material.bumpMap != null) {

            uniforms["bumpMap"]?.value = material.bumpMap
            uniforms["bumpScale"]?.value = material.bumpScale
            if (material.side == Side.Back) {
                val bumpScale = uniforms["bumpScale"]?.value<Float>()!!
                uniforms["bumpScale"]?.value = bumpScale * -1
            }

        }

        if (material.normalMap != null) {

            uniforms["normalMap"]?.value = material.normalMap
            uniforms["normalScale"]?.value<Vector2>()?.copy(material.normalScale)
            if (material.side == Side.Back) uniforms["normalScale"]?.value<Vector2>()?.negate()

        }

        if (material.displacementMap != null) {

            uniforms["displacementMap"]?.value = material.displacementMap
            uniforms["displacementScale"]?.value = material.displacementScale
            uniforms["displacementBias"]?.value = material.displacementBias

        }

    }

    private fun refreshUniformsToon(uniforms: Map<String, Uniform>, material: MeshToonMaterial) {

        refreshUniformsPhong(uniforms, material)

        if (material.gradientMap != null) {

            uniforms["gradientMap"]?.value = material.gradientMap

        }

    }

    private fun refreshUniformsStandard(uniforms: Map<String, Uniform>, material: MeshStandardMaterial) {

        uniforms["roughness"]?.value = material.roughness
        uniforms["metalness"]?.value = material.metalness

        if (material.roughnessMap != null) {

            uniforms["roughnessMap"]?.value = material.roughnessMap

        }

        if (material.metalnessMap != null) {

            uniforms["metalnessMap"]?.value = material.metalnessMap

        }

        if (material.emissiveMap != null) {

            uniforms["emissiveMap"]?.value = material.emissiveMap

        }

        if (material.bumpMap != null) {

            uniforms["bumpMap"]?.value = material.bumpMap
            uniforms["bumpScale"]?.value = material.bumpScale
            if (material.side == Side.Back) {
                val bumpScale = uniforms["bumpScale"]?.value<Float>()!!
                uniforms["bumpScale"]?.value = bumpScale * -1
            }

        }

        if (material.normalMap != null) {

            uniforms["normalMap"]?.value = material.normalMap
            uniforms["normalScale"]?.value<Vector2>()?.copy(material.normalScale)
            if (material.side == Side.Back) {
                uniforms["normalScale"]?.value<Vector2>()?.negate()
            }

        }

        if (material.displacementMap != null) {

            uniforms["displacementMap"]?.value = material.displacementMap
            uniforms["displacementScale"]?.value = material.displacementScale
            uniforms["displacementBias"]?.value = material.displacementBias

        }

        if (material.envMap != null) {

            uniforms["envMapIntensity"]?.value = material.envMapIntensity

        }

    }

    private fun refreshUniformsPhysical(uniforms: Map<String, Uniform>, material: MeshPhysicalMaterial) {

        refreshUniformsStandard(uniforms, material)

        uniforms["reflectivity"]?.value = material.reflectivity // also part of uniforms common

        uniforms["clearCoat"]?.value = material.clearCoat
        uniforms["clearCoatRoughness"]?.value = material.clearCoatRoughness

    }

    private fun refreshUniformsMatcap(uniforms: Map<String, Uniform>, material: MeshMatcapMaterial) {

        if (material.matcap != null) {

            uniforms["matcap"]?.value = material.matcap

        }

        if (material.bumpMap != null) {

            uniforms["bumpMap"]?.value = material.bumpMap
            uniforms["bumpScale"]?.value = material.bumpScale
            if (material.side == Side.Back) {
                val bumpScale = uniforms["bumpScale"]?.value<Float>()!!
                uniforms["bumpScale"]?.value = bumpScale * -1
            }

        }

        if (material.normalMap != null) {

            uniforms["normalMap"]?.value = material.normalMap
            uniforms["normalScale"]?.value<Vector2>()?.copy(material.normalScale)
            if (material.side == Side.Back) {
                uniforms["normalScale"]?.value<Vector2>()?.negate()
            }

        }

        if (material.displacementMap != null) {

            uniforms["displacementMap"]?.value = material.displacementMap
            uniforms["displacementScale"]?.value = material.displacementScale
            uniforms["displacementBias"]?.value = material.displacementBias

        }

    }

    private fun refreshUniformsDepth(uniforms: Map<String, Uniform>, material: Material) {

        if (material.displacementMap != null) {

            uniforms["displacementMap"]?.value = material.displacementMap
            uniforms["displacementScale"]?.value = material.displacementScale
            uniforms["displacementBias"]?.value = material.displacementBias

        }

    }

    private fun refreshUniformsDistance(uniforms: Map<String, Uniform>, material: MeshDistanceMaterial) {

        if (material.displacementMap != null) {

            uniforms["displacementMap"]?.value = material.displacementMap
            uniforms["displacementScale"]?.value = material.displacementScale
            uniforms["displacementBias"]?.value = material.displacementBias

        }

        uniforms["referencePosition"]?.value<Vector3>()?.copy(material.referencePosition)
        uniforms["nearDistance"]?.value = material.nearDistance
        uniforms["farDistance"]?.value = material.farDistance

    }

    private fun refreshUniformsNormal(uniforms: Map<String, Uniform>, material: MeshNormalMaterial) {

        if (material.bumpMap != null) {

            uniforms["bumpMap"]?.value = material.bumpMap
            uniforms["bumpScale"]?.value = material.bumpScale
            if (material.side == Side.Back) {
                uniforms["bumpScale"]?.value<Vector2>()?.negate()
            }

        }

        if (material.normalMap != null) {

            uniforms["normalMap"]?.value = material.normalMap
            uniforms["normalScale"]?.value<Vector2>()?.copy(material.normalScale)
            if (material.side == Side.Back) uniforms["normalScale"]?.value<Vector2>()?.negate()

        }

        if (material.displacementMap != null) {

            uniforms["displacementMap"]?.value = material.displacementMap
            uniforms["displacementScale"]?.value = material.displacementScale
            uniforms["displacementBias"]?.value = material.displacementBias

        }

    }


    private fun markUniformsLightsNeedsUpdate(uniforms: Map<String, Uniform>, value: Boolean) {
        uniforms["ambientLightColor"]?.needsUpdate = value
        uniforms["lightProbe"]?.needsUpdate = value

        uniforms["directionalLights"]?.needsUpdate = value
        uniforms["pointLights"]?.needsUpdate = value
        uniforms["spotLights"]?.needsUpdate = value
        uniforms["rectAreaLights"]?.needsUpdate = value
        uniforms["hemisphereLights"]?.needsUpdate = value
    }

    private inner class OnMaterialDispose : EventLister {
        override fun onEvent(event: Event) {
            val material = event.target as Material
            material.removeEventListener("dispose", this)
            deallocateMaterial(material)
        }
    }

    private inner class GeometryProgram(
            var geometry: Int? = null,
            var program: Int? = null,
            var wireframe: Boolean = false
    )

}
