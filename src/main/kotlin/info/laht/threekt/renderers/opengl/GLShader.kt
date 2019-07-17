package info.laht.threekt.renderers.opengl

import org.lwjgl.opengl.GL20


class GLShader(
    val name: String,
    val uniforms: GLUniforms,
    val vertexShader: String,
    val fragmentShader: String
) {

    companion object {

        fun create(
            type: Int,
            source: String
        ): Int {

            val shader = GL20.glCreateShader(type)
            GL20.glShaderSource(shader, source)
            GL20.glCompileShader(shader)
            return shader

        }

    }

}
