package info.laht.threekt.renderers

import info.laht.threekt.LinearToneMapping
import info.laht.threekt.Canvas
import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.*
import info.laht.threekt.materials.Material
import info.laht.threekt.math.*
import info.laht.threekt.objects.Group
import info.laht.threekt.renderers.opengl.*
import info.laht.threekt.scenes.Fog
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
    internal val state = GLState()
    private val info: GLInfo = GLInfo()
    private val properties: GLProperties = GLProperties()
    private val textures = GLTextures(state, properties, capabilities, info)
    private val attributes = GLAttributes()
    private val geometries = GLGeometries(attributes, info)
    private val objects = GLObjects(geometries, info)
    private val programCache = GLPrograms(this, capabilities)
    private val renderLists = GLRenderLists()
    private val renderStates = GLRenderStates()

    private val background = GLBackground(this, state, objects)

    private val bufferRenderer = GLBufferRenderer(info, capabilities)
    private val indexedBufferRenderer = GLIndexedBufferRenderer(info, capabilities)

    var currentRenderList: GLRenderList? = null
    var currentRenderState: GLRenderState? = null

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
    var toneMapping = LinearToneMapping
    var toneMappingExposure = 1f
    var toneMappingWhitePoint = 1f

    // morphs
    var maxMorphTargets = 8
    var maxMorphNormals = 4

    private var frameBuffer: Int? = null

    private var currentActiveCubeFace: Int? = 0
    private var currentActiveMipmapLevel: Int? = 0
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

    private val frustrum = Frustum()

    private val clipping = GLClipping()
    private var clippingEnabled = false

    private val projScreenMatrix = Matrix4()
    private val vector3 = Vector3()

    val shadowMap = GLShadowMap(this, objects, capabilities.maxTextureSize)

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
        val programInfo = properties[material]["program"] as GLProgram?

        material.program = null;

        if ( programInfo != null ) {

            programCache.releaseProgram( programInfo );

        }
    }

//    private fun renderBufferImmediate(`object`: Object3D, program: GLProgram) {
//        state.initAttributes()
//
//        val buffers = properties[`object`]
//
//        buffers.position?.also {
//            GL45.glCreateBuffers()
//        }
//        buffers.normal?.also {
//            GL45.glCreateBuffers()
//        }
//        buffers.uv?.also {
//            GL45.glCreateBuffers()
//        }
//        buffers.color?.also {
//            GL45.glCreateBuffers()
//        }
//
////        val programAttributes = program.
//
//    }

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

    fun getActiveMipmapLevel() = currentActiveMipmapLevel

    fun getRenderTarget() = currentRenderTarget

    fun setRenderTarget( renderTarget: GLRenderTarget?, activeCubeFace: Int? = null, activeMipMapLevel: Int? = null ) {

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

            val textureProperties = properties[renderTarget.texture]
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + (activeCubeFace ?: 0), textureProperties["__webglTexture"] as Int, activeMipMapLevel ?: 0)

        }

    }

    fun renderObject( `object`: Object3D, scene: Scene, camera: Camera, geometry: BufferGeometry, material: Material, group: Group ) {

        `object`.onBeforeRender?.invoke( this, scene, camera, geometry, material, group );
        currentRenderState = renderStates.get( scene, camera );

        `object`.modelViewMatrix.multiplyMatrices( camera.matrixWorldInverse, `object`.matrixWorld );
        `object`.normalMatrix.getNormalMatrix( `object`.modelViewMatrix );


//        renderBufferDirect( camera, scene.fog, geometry, material, object, group );

        `object`.onAfterRender?.invoke( this, scene, camera, geometry, material, group );
        currentRenderState = renderStates.get( scene, camera );

    }

//    fun  initMaterial( material: Material, fog: Fog, `object`: Object3D ) {
//
//        val materialProperties = properties[material];
//
//        val lights = currentRenderState?.lights;
//        val shadowsArray = currentRenderState?.shadowsArray;
//
//        val lightsStateVersion = lights?.state?.version;
//
//        var parameters = programCache.getParameters(
//            material, lights.state, shadowsArray, fog, _clipping.numPlanes, _clipping.numIntersection, object );
//
//        var code = programCache.getProgramCode( material, parameters );
//
//        var program = materialProperties.program;
//        var programChange = true;
//
//        if ( program == null ) {
//
//            // new material
//            material.addEventListener( 'dispose', onMaterialDispose );
//
//        } else if ( program.code !== code ) {
//
//            // changed glsl or parameters
//            releaseMaterialProgramReference( material );
//
//        } else if ( materialProperties.lightsStateVersion != lightsStateVersion ) {
//
//            materialProperties.lightsStateVersion = lightsStateVersion;
//
//            programChange = false;
//
//        } else if ( parameters.shaderID != null ) {
//
//            // same glsl and uniform list
//            return;
//
//        } else {
//
//            // only rebuild uniform list
//            programChange = false;
//
//        }
//
//        if ( programChange ) {
//
//            if ( parameters.shaderID ) {
//
//                var shader = ShaderLib[ parameters.shaderID ];
//
//                materialProperties.shader = {
//                    name: material.type,
//                    uniforms: cloneUniforms( shader.uniforms ),
//                    vertexShader: shader.vertexShader,
//                    fragmentShader: shader.fragmentShader
//                };
//
//            } else {
//
//                materialProperties.shader = {
//                        name: material.type,
//                        uniforms: material.uniforms,
//                        vertexShader: material.vertexShader,
//                        fragmentShader: material.fragmentShader
//                };
//
//            }
//
//            material.onBeforeCompile( materialProperties.shader, _this );
//
//            // Computing code again as onBeforeCompile may have changed the shaders
//            code = programCache.getProgramCode( material, parameters );
//
//            program = programCache.acquireProgram( material, materialProperties.shader, parameters, code );
//
//            materialProperties.program = program;
//            material.program = program;
//
//        }
//
//        var programAttributes = program.getAttributes();
//
//        if ( material.morphTargets ) {
//
//            material.numSupportedMorphTargets = 0;
//
//            for ( var i = 0; i < _this.maxMorphTargets; i ++ ) {
//
//                if ( programAttributes[ 'morphTarget' + i ] >= 0 ) {
//
//                    material.numSupportedMorphTargets ++;
//
//                }
//
//            }
//
//        }
//
//        if ( material.morphNormals ) {
//
//            material.numSupportedMorphNormals = 0;
//
//            for ( var i = 0; i < _this.maxMorphNormals; i ++ ) {
//
//                if ( programAttributes[ 'morphNormal' + i ] >= 0 ) {
//
//                    material.numSupportedMorphNormals ++;
//
//                }
//
//            }
//
//        }
//
//        var uniforms = materialProperties.shader.uniforms;
//
//        if ( ! material.isShaderMaterial &&
//        ! material.isRawShaderMaterial ||
//        material.clipping === true ) {
//
//            materialProperties.numClippingPlanes = _clipping.numPlanes;
//            materialProperties.numIntersection = _clipping.numIntersection;
//            uniforms.clippingPlanes = _clipping.uniform;
//
//        }
//
//        materialProperties.fog = fog;
//
//        // store the light setup it was created for
//
//        materialProperties.lightsStateVersion = lightsStateVersion;
//
//        if ( material.lights ) {
//
//            // wire up the material to this renderer's lighting state
//
//            uniforms.ambientLightColor.value = lights.state.ambient;
//            uniforms.lightProbe.value = lights.state.probe;
//            uniforms.directionalLights.value = lights.state.directional;
//            uniforms.spotLights.value = lights.state.spot;
//            uniforms.rectAreaLights.value = lights.state.rectArea;
//            uniforms.pointLights.value = lights.state.point;
//            uniforms.hemisphereLights.value = lights.state.hemi;
//
//            uniforms.directionalShadowMap.value = lights.state.directionalShadowMap;
//            uniforms.directionalShadowMatrix.value = lights.state.directionalShadowMatrix;
//            uniforms.spotShadowMap.value = lights.state.spotShadowMap;
//            uniforms.spotShadowMatrix.value = lights.state.spotShadowMatrix;
//            uniforms.pointShadowMap.value = lights.state.pointShadowMap;
//            uniforms.pointShadowMatrix.value = lights.state.pointShadowMatrix;
//            // TODO (abelnation): add area lights shadow info to uniforms
//
//        }
//
//        var progUniforms = materialProperties.program.getUniforms(),
//        uniformsList =
//        WebGLUniforms.seqWithValue( progUniforms.seq, uniforms );
//
//        materialProperties.uniformsList = uniformsList;
//
//    }
//
//    function setProgram( camera, fog, material, object ) {
//
//        textures.resetTextureUnits();
//
//        var materialProperties = properties.get( material );
//        var lights = currentRenderState.state.lights;
//
//        if ( _clippingEnabled ) {
//
//            if ( _localClippingEnabled || camera !== _currentCamera ) {
//
//                var useCache =
//                    camera === _currentCamera &&
//                            material.id === _currentMaterialId;
//
//                // we might want to call this function with some ClippingGroup
//                // object instead of the material, once it becomes feasible
//                // (#8465, #8379)
//                _clipping.setState(
//                    material.clippingPlanes, material.clipIntersection, material.clipShadows,
//                    camera, materialProperties, useCache );
//
//            }
//
//        }
//
//        if ( material.needsUpdate === false ) {
//
//            if ( materialProperties.program === null ) {
//
//                material.needsUpdate = true;
//
//            } else if ( material.fog && materialProperties.fog !== fog ) {
//
//                material.needsUpdate = true;
//
//            } else if ( material.lights && materialProperties.lightsStateVersion !== lights.state.version ) {
//
//                material.needsUpdate = true;
//
//            } else if ( materialProperties.numClippingPlanes !== null &&
//                ( materialProperties.numClippingPlanes !== _clipping.numPlanes ||
//                        materialProperties.numIntersection !== _clipping.numIntersection ) ) {
//
//                material.needsUpdate = true;
//
//            }
//
//        }
//
//        if ( material.needsUpdate ) {
//
//            initMaterial( material, fog, object );
//            material.needsUpdate = false;
//
//        }
//
//        var refreshProgram = false;
//        var refreshMaterial = false;
//        var refreshLights = false;
//
//        var program = materialProperties.program,
//        p_uniforms = program.getUniforms(),
//        m_uniforms = materialProperties.shader.uniforms;
//
//        if ( state.useProgram( program.program ) ) {
//
//            refreshProgram = true;
//            refreshMaterial = true;
//            refreshLights = true;
//
//        }
//
//        if ( material.id !== _currentMaterialId ) {
//
//            _currentMaterialId = material.id;
//
//            refreshMaterial = true;
//
//        }
//
//        if ( refreshProgram || _currentCamera !== camera ) {
//
//            p_uniforms.setValue( _gl, 'projectionMatrix', camera.projectionMatrix );
//
//            if ( capabilities.logarithmicDepthBuffer ) {
//
//                p_uniforms.setValue( _gl, 'logDepthBufFC',
//                    2.0 / ( Math.log( camera.far + 1.0 ) / Math.LN2 ) );
//
//            }
//
//            if ( _currentCamera !== camera ) {
//
//                _currentCamera = camera;
//
//                // lighting uniforms depend on the camera so enforce an update
//                // now, in case this material supports lights - or later, when
//                // the next material that does gets activated:
//
//                refreshMaterial = true;		// set to true on material change
//                refreshLights = true;		// remains set until update done
//
//            }
//
//            // load material specific uniforms
//            // (shader material also gets them for the sake of genericity)
//
//            if ( material.isShaderMaterial ||
//                material.isMeshPhongMaterial ||
//                material.isMeshStandardMaterial ||
//                material.envMap ) {
//
//                var uCamPos = p_uniforms.map.cameraPosition;
//
//                if ( uCamPos !== null ) {
//
//                    uCamPos.setValue( _gl,
//                        _vector3.setFromMatrixPosition( camera.matrixWorld ) );
//
//                }
//
//            }
//
//            if ( material.isMeshPhongMaterial ||
//                material.isMeshLambertMaterial ||
//                material.isMeshBasicMaterial ||
//                material.isMeshStandardMaterial ||
//                material.isShaderMaterial ||
//                material.skinning ) {
//
//                p_uniforms.setValue( _gl, 'viewMatrix', camera.matrixWorldInverse );
//
//            }
//
//        }
//
//        // skinning uniforms must be set even if material didn't change
//        // auto-setting of texture unit for bone texture must go before other textures
//        // not sure why, but otherwise weird things happen
//
//        if ( material.skinning ) {
//
//            p_uniforms.setOptional( _gl, object, 'bindMatrix' );
//            p_uniforms.setOptional( _gl, object, 'bindMatrixInverse' );
//
//            var skeleton = object.skeleton;
//
//            if ( skeleton ) {
//
//                var bones = skeleton.bones;
//
//                if ( capabilities.floatVertexTextures ) {
//
//                    if ( skeleton.boneTexture === null ) {
//
//                        // layout (1 matrix = 4 pixels)
//                        //      RGBA RGBA RGBA RGBA (=> column1, column2, column3, column4)
//                        //  with  8x8  pixel texture max   16 bones * 4 pixels =  (8 * 8)
//                        //       16x16 pixel texture max   64 bones * 4 pixels = (16 * 16)
//                        //       32x32 pixel texture max  256 bones * 4 pixels = (32 * 32)
//                        //       64x64 pixel texture max 1024 bones * 4 pixels = (64 * 64)
//
//
//                        var size = Math.sqrt( bones.length * 4 ); // 4 pixels needed for 1 matrix
//                        size = _Math.ceilPowerOfTwo( size );
//                        size = Math.max( size, 4 );
//
//                        var boneMatrices = new Float32Array( size * size * 4 ); // 4 floats per RGBA pixel
//                        boneMatrices.set( skeleton.boneMatrices ); // copy current values
//
//                        var boneTexture = new DataTexture( boneMatrices, size, size, RGBAFormat, FloatType );
//                        boneTexture.needsUpdate = true;
//
//                        skeleton.boneMatrices = boneMatrices;
//                        skeleton.boneTexture = boneTexture;
//                        skeleton.boneTextureSize = size;
//
//                    }
//
//                    p_uniforms.setValue( _gl, 'boneTexture', skeleton.boneTexture, textures );
//                    p_uniforms.setValue( _gl, 'boneTextureSize', skeleton.boneTextureSize );
//
//                } else {
//
//                    p_uniforms.setOptional( _gl, skeleton, 'boneMatrices' );
//
//                }
//
//            }
//
//        }
//
//        if ( refreshMaterial ) {
//
//            p_uniforms.setValue( _gl, 'toneMappingExposure', _this.toneMappingExposure );
//            p_uniforms.setValue( _gl, 'toneMappingWhitePoint', _this.toneMappingWhitePoint );
//
//            if ( material.lights ) {
//
//                // the current material requires lighting info
//
//                // note: all lighting uniforms are always set correctly
//                // they simply reference the renderer's state for their
//                // values
//                //
//                // use the current material's .needsUpdate flags to set
//                // the GL state when required
//
//                markUniformsLightsNeedsUpdate( m_uniforms, refreshLights );
//
//            }
//
//            // refresh uniforms common to several materials
//
//            if ( fog && material.fog ) {
//
//                refreshUniformsFog( m_uniforms, fog );
//
//            }
//
//            if ( material.isMeshBasicMaterial ) {
//
//                refreshUniformsCommon( m_uniforms, material );
//
//            } else if ( material.isMeshLambertMaterial ) {
//
//                refreshUniformsCommon( m_uniforms, material );
//                refreshUniformsLambert( m_uniforms, material );
//
//            } else if ( material.isMeshPhongMaterial ) {
//
//                refreshUniformsCommon( m_uniforms, material );
//
//                if ( material.isMeshToonMaterial ) {
//
//                    refreshUniformsToon( m_uniforms, material );
//
//                } else {
//
//                    refreshUniformsPhong( m_uniforms, material );
//
//                }
//
//            } else if ( material.isMeshStandardMaterial ) {
//
//                refreshUniformsCommon( m_uniforms, material );
//
//                if ( material.isMeshPhysicalMaterial ) {
//
//                    refreshUniformsPhysical( m_uniforms, material );
//
//                } else {
//
//                    refreshUniformsStandard( m_uniforms, material );
//
//                }
//
//            } else if ( material.isMeshMatcapMaterial ) {
//
//                refreshUniformsCommon( m_uniforms, material );
//
//                refreshUniformsMatcap( m_uniforms, material );
//
//            } else if ( material.isMeshDepthMaterial ) {
//
//                refreshUniformsCommon( m_uniforms, material );
//                refreshUniformsDepth( m_uniforms, material );
//
//            } else if ( material.isMeshDistanceMaterial ) {
//
//                refreshUniformsCommon( m_uniforms, material );
//                refreshUniformsDistance( m_uniforms, material );
//
//            } else if ( material.isMeshNormalMaterial ) {
//
//                refreshUniformsCommon( m_uniforms, material );
//                refreshUniformsNormal( m_uniforms, material );
//
//            } else if ( material.isLineBasicMaterial ) {
//
//                refreshUniformsLine( m_uniforms, material );
//
//                if ( material.isLineDashedMaterial ) {
//
//                    refreshUniformsDash( m_uniforms, material );
//
//                }
//
//            } else if ( material.isPointsMaterial ) {
//
//                refreshUniformsPoints( m_uniforms, material );
//
//            } else if ( material.isSpriteMaterial ) {
//
//                refreshUniformsSprites( m_uniforms, material );
//
//            } else if ( material.isShadowMaterial ) {
//
//                m_uniforms.color.value.copy( material.color );
//                m_uniforms.opacity.value = material.opacity;
//
//            }
//
//            // RectAreaLight Texture
//            // TODO (mrdoob): Find a nicer implementation
//
//            if ( m_uniforms.ltc_1 !== null ) m_uniforms.ltc_1.value = UniformsLib.LTC_1;
//            if ( m_uniforms.ltc_2 !== null ) m_uniforms.ltc_2.value = UniformsLib.LTC_2;
//
//            WebGLUniforms.upload( _gl, materialProperties.uniformsList, m_uniforms, textures );
//
//        }
//
//        if ( material is ShaderMaterial && material.uniformsNeedUpdate == true ) {
//
//            GLUniforms.upload( _materialProperties.uniformsList, m_uniforms, textures );
//            material.uniformsNeedUpdate = false;
//
//        }
//
//        if ( material is SpriteMaterial ) {
//
//            p_uniforms.setValue( _gl, 'center', object.center );
//
//        }
//
//        // common matrices
//
//        p_uniforms.setValue( _gl, 'modelViewMatrix', object.modelViewMatrix );
//        p_uniforms.setValue( _gl, 'normalMatrix', object.normalMatrix );
//        p_uniforms.setValue( _gl, 'modelMatrix', object.matrixWorld );
//
//        return program;
//
//    }

    private inner class OnMaterialDispose: EventLister {
        override fun onEvent(event: Event) {
            val material = event.target as Material
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
