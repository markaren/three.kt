package info.laht.threekt.renderers.opengl

import info.laht.threekt.renderers.GLMultisampleRenderTarget
import info.laht.threekt.renderers.GLRenderTarget
import info.laht.threekt.renderers.GLRenderTargetCube
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import kotlin.math.min

class GLTextures internal constructor(
    private val state: GLState,
    private val properties: GLProperties,
    private val capabilities: GLCapabilities,
    private val info: GLInfo
) {



    fun setupDepthTexture( framebuffer: Int, renderTarget: GLRenderTarget ) {

        if (renderTarget is GLRenderTargetCube) {
            throw IllegalArgumentException("Depth Texture with cube render targets is not supported")
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer)

//        setTexture2D( renderTarget.depthTexture, 0 );

        TODO()
    }

    fun setupDepthRenderbuffer( renderTarget: GLRenderTarget ) {
        TODO()
    }

    fun setupTargetProperties( renderTarget: GLRenderTarget ) {
        TODO()
    }

    fun updateMultisampleRenderTarget ( renderTarget: GLMultisampleRenderTarget ) {
        val renderTargetProperties = properties[renderTarget] as GLRenderTarget

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, renderTargetProperties["__webglMultisampledFramebuffer"]!! )
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, renderTargetProperties["__webglFramebuffer"]!! )

        val width = renderTarget.width
        val height = renderTarget.height
        var mask = GL11.GL_COLOR_BUFFER_BIT

        if ( renderTarget.depthBuffer ) {
            mask = mask or GL11.GL_DEPTH_BUFFER_BIT
        }
        if ( renderTarget.stencilBuffer ) {
            mask = mask or GL11.GL_STENCIL_BUFFER_BIT
        }

        GL30.glBlitFramebuffer( 0, 0, width, height, 0, 0, width, height, mask, GL11.GL_NEAREST );
    }

    fun getRenderTargetSamples( renderTarget: GLRenderTarget ): Int {
        return if (renderTarget is GLMultisampleRenderTarget) {
            min(capabilities.maxSamples, renderTarget.samples)
        } else {
            0
        }
    }

}

