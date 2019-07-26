package info.laht.threekt.renderers.opengl

import info.laht.threekt.*
import info.laht.threekt.core.Event
import info.laht.threekt.core.EventLister
import info.laht.threekt.renderers.GLMultisampleRenderTarget
import info.laht.threekt.renderers.GLRenderTarget
import info.laht.threekt.renderers.GLRenderTargetCube
import info.laht.threekt.textures.DepthTexture
import info.laht.threekt.textures.Texture
import org.lwjgl.opengl.*
import kotlin.math.*

internal class GLTextures(
    private val state: GLState,
    private val properties: GLProperties,
    private val capabilities: GLCapabilities,
    private val info: GLInfo
) {

    private val onTextureDispose = OnTextureDispose()
    private val onRenderTargetDispose = OnRenderTargetDispose()

    private fun textureNeedsGenerateMipmaps(texture: Texture, supportsMips: Boolean): Boolean {

        return texture.generateMipmaps && supportsMips &&
                texture.minFilter != NearestFilter && texture.minFilter != LinearFilter

    }

    private fun generateMipmap(target: Int, texture: Texture, width: Int, height: Int) {

        GL30.glGenerateMipmap(target)

        val textureProperties = properties.get(texture)

        // Note: Math.log( x ) * Math.LOG2E used instead of Math.log2( x ) which is not supported by IE11
        textureProperties["__maxMipLevel"] = ln(max(width, height).toDouble()) * log2(E)

    }


    private fun getInternalFormat(glFormat: Int, glType: Int): Int {

        var internalFormat = glFormat

        if (glFormat == GL11.GL_RED) {

            if (glType == GL11.GL_FLOAT) internalFormat = GL30.GL_R32F
            if (glType == GL30.GL_HALF_FLOAT) internalFormat = GL30.GL_R16F
            if (glType == GL11.GL_UNSIGNED_BYTE) internalFormat = GL30.GL_R8

        }

        if (glFormat == GL11.GL_RGB) {

            if (glType == GL11.GL_FLOAT) internalFormat = GL30.GL_RGB32F
            if (glType == GL30.GL_HALF_FLOAT) internalFormat = GL30.GL_RGB16F
            if (glType == GL11.GL_UNSIGNED_BYTE) internalFormat = GL11.GL_RGB8

        }

        if (glFormat == GL11.GL_RGBA) {

            if (glType == GL11.GL_FLOAT) internalFormat = GL30.GL_RGBA32F
            if (glType == GL30.GL_HALF_FLOAT) internalFormat = GL30.GL_RGBA16F
            if (glType == GL11.GL_UNSIGNED_BYTE) internalFormat = GL11.GL_RGBA8

        }

        if (internalFormat == GL30.GL_R16F || internalFormat == GL30.GL_R32F ||
            internalFormat == GL30.GL_RGBA16F || internalFormat == GL30.GL_RGBA32F
        ) {

        } else if (internalFormat == GL30.GL_RGB16F || internalFormat == GL30.GL_RGB32F) {

            println("GLRenderer: Floating point textures with RGB format not supported. Please use RGBA instead.")

        }

        return internalFormat

    }

    // Fallback filters for non-power-of-2 textures
    private fun filterFallback(f: Int): Int {

        if (f == NearestFilter || f == NearestMipMapNearestFilter || f == NearestMipMapLinearFilter) {
            return GL11.GL_NEAREST
        }

        return GL11.GL_LINEAR

    }


    private fun deallocateTexture(texture: Texture) {
        val textureProperties = properties[texture]

        if (textureProperties["__webglInit"] == null) return
        GL11.glDeleteTextures(textureProperties["__webglTexture"] as Int)

        properties.remove(texture)

    }

    private fun deallocateRenderTarget(renderTarget: GLRenderTarget) {
        val renderTargetProperties = properties[renderTarget]
        val textureProperties = properties[renderTarget.texture]

        if (textureProperties["__webglTexture"] != null) {

            GL11.glDeleteTextures(textureProperties["__webglTexture"] as Int)

        }

        renderTarget.depthTexture?.also {
            it.dispose()
        }

        if (renderTarget is GLRenderTargetCube) {

            for (i in 0 until 6) {

                GL30.glDeleteFramebuffers((renderTargetProperties["__webglFramebuffer"] as IntArray)[i])
                if (renderTargetProperties["__webglDepthbuffer"] != null) {
                    GL30.glDeleteRenderbuffers((renderTargetProperties["__webglDepthbuffer"] as IntArray)[i])
                }

            }

        } else {

            GL30.glDeleteFramebuffers(renderTargetProperties["__webglFramebuffer"] as IntArray)
            if (renderTargetProperties["__webglDepthbuffer"] != null) {
                GL30.glDeleteRenderbuffers(renderTargetProperties["__webglDepthbuffer"] as IntArray)
            }

        }

        properties.remove(renderTarget.texture)
        properties.remove(renderTarget)
    }

    private var textureUnits = 0

    fun resetTextureUnits() {
        textureUnits = 0
    }

    fun allocateTextureUnit(): Int {
        val textureUnit = textureUnits

        if (textureUnit >= capabilities.maxTextures) {

            println("GLTextures: Trying to use " + textureUnit + " renderTargetCube units while this GPU supports only " + capabilities.maxTextures)

        }

        textureUnits += 1

        return textureUnit
    }

    fun setTexture2D(texture: Texture, slot: Int) {

        val textureProperties = properties[texture]

        if (texture.version > 0 && textureProperties["__version"] != texture.version) {

            val image = texture.image

            if (image == null) {
                println("GLRenderer: Texture marked for update but image is null")
            } else {
                uploadTexture(textureProperties, texture, slot)
                return
            }

        }

        state.activeTexture(GL13.GL_TEXTURE0 + slot)
        state.bindTexture(GL11.GL_TEXTURE_2D, textureProperties["__webglTexture"] as Int)

    }

    fun setTextureCubeDynamic(texture: Texture, slot: Int) {

        state.activeTexture(GL13.GL_TEXTURE0 + slot)
        state.bindTexture(GL13.GL_TEXTURE_CUBE_MAP, properties[texture]["__webglTexture"] as Int)

    }

    private fun setTextureParameters(textureType: Int, texture: Texture, supportsMips: Boolean) {

        if (supportsMips) {

            GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_WRAP_S, GLUtils.convert(texture.wrapS))
            GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_WRAP_T, GLUtils.convert(texture.wrapT))

            if (textureType == GL12.GL_TEXTURE_3D || textureType == GL30.GL_TEXTURE_2D_ARRAY) {
                TODO()
                //GL11.glTexParameteri( textureType, GL12.GL_TEXTURE_WRAP_R, utils.convert( ( renderTargetCube.wrapR) ) );

            }

            GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MAG_FILTER, GLUtils.convert(texture.magFilter))
            GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MIN_FILTER, GLUtils.convert(texture.minFilter))

        } else {

            GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
            GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)

            if (textureType == GL12.GL_TEXTURE_3D || textureType == GL30.GL_TEXTURE_2D_ARRAY) {

                GL11.glTexParameteri(textureType, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE)

            }

            if (texture.wrapS != ClampToEdgeWrapping || texture.wrapT != ClampToEdgeWrapping) {

                println("GLRenderer: Texture is not power of two. Texture.wrapS and Texture.wrapT should be set to THREE.ClampToEdgeWrapping.")

            }

            GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MAG_FILTER, filterFallback(texture.magFilter))
            GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MIN_FILTER, filterFallback(texture.minFilter))

            if (texture.minFilter != NearestFilter && texture.minFilter != LinearFilter) {

                println("GLRenderer: Texture is not power of two. Texture.minFilter should be set to THREE.NearestFilter or THREE.LinearFilter.")

            }

        }

        if (texture.anisotropy > 1 || properties[texture]["__currentAnisotropy"] != null) {

            GL11.glTexParameteri(
                textureType,
                GL46.GL_MAX_TEXTURE_MAX_ANISOTROPY,
                min(texture.anisotropy, capabilities.maxAnisotropy)
            )
            properties[texture]["__currentAnisotropy"] = texture.anisotropy

        }

    }

    private fun initTexture(textureProperties: Properties, texture: Texture) {

        if (textureProperties["__webglInit"] == null) {

            textureProperties["__webglInit"] = true
            texture.addEventListener("dispose", onTextureDispose)
            textureProperties["__webglTexture"] = GL11.glGenTextures()

            info.memory.textures++

        }

    }

    private fun uploadTexture(textureProperties: Properties, texture: Texture, slot: Int) {

        val image = texture.image ?: return

        val textureType = GL11.GL_TEXTURE_2D

        initTexture(textureProperties, texture)

        state.activeTexture(GL13.GL_TEXTURE0 + slot)
        state.bindTexture(textureType, textureProperties["__webglTexture"] as Int)

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, texture.unpackAlignment)

        val supportsMips = true
        val glFormat = GLUtils.convert(texture.format)
        var glType = GLUtils.convert(texture.type)
        var glInternalFormat = getInternalFormat(glFormat, glType)

        setTextureParameters(textureType, texture, supportsMips)

        val mipmaps = texture.mipmaps

        if (texture is DepthTexture) {

            // populate depth renderTargetCube with dummy data

            glInternalFormat = if (texture.type == FloatType) {

                GL30.GL_DEPTH_COMPONENT32F

            } else {

                GL14.GL_DEPTH_COMPONENT16

            }

            if (texture.format == DepthFormat && glInternalFormat == GL11.GL_DEPTH_COMPONENT) {

                // The error INVALID_OPERATION is generated by texImage2D if format and internalformat are
                // DEPTH_COMPONENT and type is not UNSIGNED_SHORT or UNSIGNED_INT
                // (https://www.khronos.org/registry/webgl/extensions/WEBGL_depth_texture/)
                if (texture.type != UnsignedShortType && texture.type != UnsignedIntType) {

                    println("THREE.WebGLRenderer: Use UnsignedShortType or UnsignedIntType for DepthFormat DepthTexture.")

                    texture.type = UnsignedShortType
                    glType = GLUtils.convert(texture.type)

                }

            }

            // Depth stencil textures need the DEPTH_STENCIL internal format
            // (https://www.khronos.org/registry/webgl/extensions/WEBGL_depth_texture/)
            if (texture.format == DepthStencilFormat) {

                glInternalFormat = GL30.GL_DEPTH_STENCIL

                // The error INVALID_OPERATION is generated by texImage2D if format and internalformat are
                // DEPTH_STENCIL and type is not UNSIGNED_INT_24_8_WEBGL.
                // (https://www.khronos.org/registry/webgl/extensions/WEBGL_depth_texture/)
                if (texture.type != UnsignedInt248Type) {

                    println("THREE.WebGLRenderer: Use UnsignedInt248Type for DepthStencilFormat DepthTexture.")

                    texture.type = UnsignedInt248Type
                    glType = GLUtils.convert(texture.type)

                }

            }

            state.texImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                glInternalFormat,
                image.width,
                image.height,
                glFormat,
                glType,
                null
            )

        } else {

            // regular Texture (image, video, canvas)

            // use manually created mipmaps if available
            // if there are no manual mipmaps
            // set 0 level mipmap and then use GL to generate other mipmap levels

            if (mipmaps.size > 0 && supportsMips) {

                mipmaps.forEachIndexed { i, mipmap ->

                    state.texImage2D(
                        GL11.GL_TEXTURE_2D,
                        i,
                        glInternalFormat,
                        glFormat,
                        glType,
                        mipmap.width,
                        mipmap.height,
                        mipmap.data
                    )

                }

                texture.generateMipmaps = false
                textureProperties["__maxMipLevel"] = mipmaps.size - 1

            } else {

                state.texImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    glInternalFormat,
                    glFormat,
                    glType,
                    image.width,
                    image.height,
                    image.data
                )
                textureProperties["__maxMipLevel"] = 0

            }

        }

        if (textureNeedsGenerateMipmaps(texture, supportsMips)) {

            generateMipmap(GL11.GL_TEXTURE_2D, texture, image.width, image.height)

        }

        textureProperties["__version"] = texture.version

        texture.onUpdate?.apply {
            invoke(texture)
        }

    }

    private fun setupFrameBufferTexture(
        framebuffer: Int,
        renderTarget: GLRenderTarget,
        attachment: Int,
        textureTarget: Int
    ) {

        val glFormat = GLUtils.convert(renderTarget.texture.format)
        val glType = GLUtils.convert(renderTarget.texture.type)
        val glInternalFormat = getInternalFormat(glFormat, glType)
        state.texImage2D(
            textureTarget,
            0,
            glInternalFormat,
            renderTarget.width,
            renderTarget.height,
            glFormat,
            glType,
            null
        )
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer)
        GL30.glFramebufferTexture2D(
            GL30.GL_FRAMEBUFFER,
            attachment,
            textureTarget,
            properties[renderTarget.texture]["__webglTexture"] as Int,
            0
        )
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)

    }

    private fun setupRenderBufferStorage(
        renderbuffer: Int,
        renderTarget: GLRenderTarget,
        isMultisample: Boolean = false
    ) {

        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer)

        if (renderTarget.depthBuffer && !renderTarget.stencilBuffer) {

            if (isMultisample) {

                val samples = getRenderTargetSamples(renderTarget)

                GL30.glRenderbufferStorageMultisample(
                    GL30.GL_RENDERBUFFER,
                    samples,
                    GL14.GL_DEPTH_COMPONENT16,
                    renderTarget.width,
                    renderTarget.height
                )

            } else {

                GL30.glRenderbufferStorage(
                    GL30.GL_RENDERBUFFER,
                    GL14.GL_DEPTH_COMPONENT16,
                    renderTarget.width,
                    renderTarget.height
                )

            }

            GL30.glFramebufferRenderbuffer(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_RENDERBUFFER,
                renderbuffer
            )

        } else if (renderTarget.depthBuffer && renderTarget.stencilBuffer) {

            if (isMultisample) {

                val samples = getRenderTargetSamples(renderTarget)

                GL30.glRenderbufferStorageMultisample(
                    GL30.GL_RENDERBUFFER,
                    samples,
                    GL30.GL_DEPTH24_STENCIL8,
                    renderTarget.width,
                    renderTarget.height
                )

            } else {

                GL30.glRenderbufferStorage(
                    GL30.GL_RENDERBUFFER,
                    GL30C.GL_DEPTH_STENCIL,
                    renderTarget.width,
                    renderTarget.height
                )

            }


            GL30.glFramebufferRenderbuffer(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                GL30.GL_RENDERBUFFER,
                renderbuffer
            )

        } else {

            val glFormat = GLUtils.convert(renderTarget.texture.format)
            val glType = GLUtils.convert(renderTarget.texture.type)
            val glInternalFormat = getInternalFormat(glFormat, glType)

            if (isMultisample) {

                val samples = getRenderTargetSamples(renderTarget)

                GL30.glRenderbufferStorageMultisample(
                    GL30.GL_RENDERBUFFER,
                    samples,
                    glInternalFormat,
                    renderTarget.width,
                    renderTarget.height
                )

            } else {

                GL30.glRenderbufferStorage(
                    GL30.GL_RENDERBUFFER,
                    glInternalFormat,
                    renderTarget.width,
                    renderTarget.height
                )

            }

        }

        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0)

    }

    private fun setupDepthTexture(framebuffer: Int, renderTarget: GLRenderTarget) {


        val depthTexture = renderTarget.depthTexture ?: throw IllegalArgumentException("Depth Texture is null")
        val image = depthTexture.image ?: throw IllegalArgumentException("Depth Texture Image is null")

        if (renderTarget is GLRenderTargetCube) {
            throw IllegalArgumentException("Depth Texture with cube render targets is not supported")
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer)

        // upload an empty depth renderTargetCube with framebuffer size
        if (properties[depthTexture]["__webglTexture"] != null ||
            image.width != renderTarget.width ||
            image.height != renderTarget.height
        ) {

            image.width = renderTarget.width
            image.height = renderTarget.height
            depthTexture.needsUpdate = true

        }

        setTexture2D(depthTexture, 0)

    }

    fun setupDepthRenderbuffer(renderTarget: GLRenderTarget) {
        val renderTargetProperties = properties.get(renderTarget)

        val isCube = (renderTarget is GLRenderTargetCube)

        if (renderTarget.depthTexture != null) {

            if (isCube) throw Error("target.depthTexture not supported in Cube render targets")

            setupDepthTexture(renderTargetProperties["__webglFramebuffer"] as Int, renderTarget)

        } else {

            if (isCube) {

                renderTargetProperties["__webglDepthbuffer"] = IntArray(6)

                for (i in 0 until 6) {

                    GL30.glBindFramebuffer(
                        GL30.GL_FRAMEBUFFER,
                        renderTargetProperties.getAs<IntArray>("__webglFramebuffer")!![i]
                    )
                    renderTargetProperties.getAs<IntArray>("__webglDepthbuffer")!![i] = GL45.glCreateRenderbuffers()
                    setupRenderBufferStorage(
                        renderTargetProperties.getAs<IntArray>("__webglDepthbuffer")!![i],
                        renderTarget
                    )

                }

            } else {

                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, renderTargetProperties["__webglFramebuffer"] as Int)
                renderTargetProperties["__webglDepthbuffer"] = GL45.glCreateRenderbuffers()
                setupRenderBufferStorage(renderTargetProperties["__webglDepthbuffer"] as Int, renderTarget)

            }

        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
    }

    fun setupRenderTarget(renderTarget: GLRenderTarget) {

        val renderTargetProperties = properties[renderTarget]
        val textureProperties = properties[renderTarget.texture]

        renderTarget.addEventListener("dispose", onRenderTargetDispose)

        textureProperties["__webglTexture"] = GL11.glGenTextures()

        info.memory.textures++

        val isCube = (renderTarget is GLRenderTargetCube)
        val isMultisample = (renderTarget is GLMultisampleRenderTarget)
        val supportsMips = true

        if (isCube) {

            renderTargetProperties["__webglFramebuffer"] = IntArray(6)

            for (i in 0 until 6) {

                renderTargetProperties.getAs<IntArray>("__webglFramebuffer")!![i] = (GL45.glCreateFramebuffers())

            }

        } else {

            renderTargetProperties["__webglFramebuffer"] = GL45.glCreateFramebuffers()

            if (isMultisample) {

                TODO()
//                    renderTargetProperties["__webglMultisampledFramebuffer"] = GL45.glCreateFramebuffers();
//                    renderTargetProperties["__webglColorRenderbuffer"] = GL45.glCreateFramebuffers();
//
//                    GL30.glBindRenderbuffer( GL30.GL_RENDERBUFFER, renderTargetProperties.__webglColorRenderbuffer );
//                    var glFormat = utils.convert( renderTarget.texture.format );
//                    var glType = utils.convert( renderTarget.texture.type );
//                    var glInternalFormat = getInternalFormat( glFormat, glType );
//                    var samples = getRenderTargetSamples( renderTarget );
//                    GL30.GL_RENDERBUFFERStorageMultisample( GL30.GL_RENDERBUFFER, samples, glInternalFormat, renderTarget.width, renderTarget.height );
//
//                    _gl.bindFramebuffer( GL30.GL_FRAMEBUFFER, renderTargetProperties.__webglMultisampledFramebuffer );
//                    _gl.framebufferRenderbuffer( GL30.GL_FRAMEBUFFER, _gl.COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, renderTargetProperties.__webglColorRenderbuffer );
//                    _gl.bindRenderbuffer( GL30.GL_FRAMEBUFFER, null );
//
//                    if ( renderTarget.depthBuffer ) {
//
//                        renderTargetProperties["__webglDepthRenderbuffer"] = GL45.glCreateRenderbuffers();
//                        setupRenderBufferStorage( renderTargetProperties.__webglDepthRenderbuffer, renderTarget, true );
//
//                    }
//
//                    GL30.glBindFramebuffer( GL30.GL_FRAMEBUFFER, null );


            }

        }

        // Setup color buffer

        if (isCube) {

            state.bindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureProperties["__webglTexture"] as Int)
            setTextureParameters(GL13.GL_TEXTURE_CUBE_MAP, renderTarget.texture, supportsMips)

            for (i in 0 until 6) {

                setupFrameBufferTexture(
                    renderTargetProperties.getAs<IntArray>("__webglFramebuffer")!![i],
                    renderTarget,
                    GL30.GL_COLOR_ATTACHMENT0,
                    GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i
                )

            }

            if (textureNeedsGenerateMipmaps(renderTarget.texture, supportsMips)) {

                generateMipmap(GL13.GL_TEXTURE_CUBE_MAP, renderTarget.texture, renderTarget.width, renderTarget.height)

            }

            state.bindTexture(GL13.GL_TEXTURE_CUBE_MAP, null)

        } else {

            state.bindTexture(GL11.GL_TEXTURE_2D, textureProperties["__webglTexture"] as Int)
            setTextureParameters(GL11.GL_TEXTURE_2D, renderTarget.texture, supportsMips)
            setupFrameBufferTexture(
                renderTargetProperties["__webglFramebuffer"] as Int,
                renderTarget,
                GL30.GL_COLOR_ATTACHMENT0,
                GL11.GL_TEXTURE_2D
            )

            if (textureNeedsGenerateMipmaps(renderTarget.texture, supportsMips)) {

                generateMipmap(GL11.GL_TEXTURE_2D, renderTarget.texture, renderTarget.width, renderTarget.height)

            }

            state.bindTexture(GL11.GL_TEXTURE_2D, null)

        }

        // Setup depth and stencil buffers

        if (renderTarget.depthBuffer) {

            setupDepthRenderbuffer(renderTarget)

        }
    }


    fun updateMultisampleRenderTarget(renderTarget: GLMultisampleRenderTarget) {
        val renderTargetProperties = properties[renderTarget]

        GL30.glBindFramebuffer(
            GL30.GL_READ_FRAMEBUFFER,
            renderTargetProperties["__webglMultisampledFramebuffer"] as Int
        )
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, renderTargetProperties["__webglFramebuffer"] as Int)

        val width = renderTarget.width
        val height = renderTarget.height
        var mask = GL11.GL_COLOR_BUFFER_BIT

        if (renderTarget.depthBuffer) {
            mask = mask or GL11.GL_DEPTH_BUFFER_BIT
        }
        if (renderTarget.stencilBuffer) {
            mask = mask or GL11.GL_STENCIL_BUFFER_BIT
        }

        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, mask, GL11.GL_NEAREST)
    }


    private fun getRenderTargetSamples(renderTarget: GLRenderTarget): Int {
        return if (renderTarget !is GLMultisampleRenderTarget) 0 else min(capabilities.maxSamples, renderTarget.samples)
    }

    private inner class OnTextureDispose : EventLister {

        override fun onEvent(event: Event) {
            val texture = event.target as Texture

            texture.removeEventListener("dispose", this)
            deallocateTexture(texture)

            info.memory.textures--
        }
    }

    private inner class OnRenderTargetDispose : EventLister {

        override fun onEvent(event: Event) {
            val renderTarget = event.target as GLRenderTarget

            renderTarget.removeEventListener("dispose", this)
            deallocateRenderTarget(renderTarget)

            info.memory.textures--
        }
    }

}
