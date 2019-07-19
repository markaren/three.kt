package info.laht.threekt.renderers.opengl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*

class GLCapabilities {

    val maxAnisotropy by lazy {
        GL11.glGetInteger(GL46.GL_MAX_TEXTURE_MAX_ANISOTROPY)
    }

    val precision = "highp"

//    var logarithmicDepthBuffer = parameters.logarithmicDepthBuffer == true;

    var maxTextures = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS)
    var maxVertexTextures = GL11.glGetInteger(GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS)
    var maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE)
    var maxCubemapSize = GL11.glGetInteger(GL13.GL_MAX_CUBE_MAP_TEXTURE_SIZE)

    var maxAttributes = GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS)
    var maxVertexUniforms = GL11.glGetInteger(GL41.GL_MAX_VERTEX_UNIFORM_VECTORS)
    var maxVaryings = GL11.glGetInteger(GL41.GL_MAX_VARYING_VECTORS)
    var maxFragmentUniforms = GL11.glGetInteger(GL41.GL_MAX_FRAGMENT_UNIFORM_VECTORS)

    var vertexTextures = maxVertexTextures > 0
    var floatVertexTextures = true

    var maxSamples = GL11.glGetInteger(GL30.GL_MAX_SAMPLES)

    fun getMaxPrecision(precision: String): String {

        @Suppress("NAME_SHADOWING")
        var precision = precision

        if (precision == "highp") {
            if (GL41.glGetShaderPrecisionFormat(
                    GL20.GL_VERTEX_SHADER,
                    GL41.GL_HIGH_FLOAT,
                    BufferUtils.createIntBuffer(2)
                ) > 0 &&
                GL41.glGetShaderPrecisionFormat(
                    GL20.GL_VERTEX_SHADER,
                    GL41.GL_HIGH_FLOAT,
                    BufferUtils.createIntBuffer(2)
                ) > 0
            ) {
                return "highp"
            }
            precision = "mediump"
        }

        if (precision == "mediump") {
            if (GL41.glGetShaderPrecisionFormat(
                    GL20.GL_VERTEX_SHADER,
                    GL41.GL_MEDIUM_FLOAT,
                    BufferUtils.createIntBuffer(2)
                ) > 0 &&
                GL41.glGetShaderPrecisionFormat(
                    GL20.GL_VERTEX_SHADER,
                    GL41.GL_MEDIUM_FLOAT,
                    BufferUtils.createIntBuffer(2)
                ) > 0
            ) {
                return "mediump"
            }
        }

        return "lowp"

    }

}