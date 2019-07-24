package info.laht.threekt.renderers.opengl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*

class GLCapabilities internal constructor(){

    val maxAnisotropy by lazy {
        GL11.glGetInteger(GL46.GL_MAX_TEXTURE_MAX_ANISOTROPY)
    }

    val precision = "highp"

    val logarithmicDepthBuffer = false;

    val maxTextures = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS)
    val maxVertexTextures = GL11.glGetInteger(GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS)
    val maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE)
    val maxCubemapSize = GL11.glGetInteger(GL13.GL_MAX_CUBE_MAP_TEXTURE_SIZE)

    val maxAttributes = GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS)
    val maxVertexUniforms = GL11.glGetInteger(GL41.GL_MAX_VERTEX_UNIFORM_VECTORS)
    val maxVaryings = GL11.glGetInteger(GL41.GL_MAX_VARYING_VECTORS)
    val maxFragmentUniforms = GL11.glGetInteger(GL41.GL_MAX_FRAGMENT_UNIFORM_VECTORS)

    val vertexTextures = maxVertexTextures > 0
    val floatVertexTextures = true

    val maxSamples = GL11.glGetInteger(GL30.GL_MAX_SAMPLES)

}
