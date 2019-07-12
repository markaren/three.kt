package info.laht.threekt.renderers.renderers.gl

import org.lwjgl.opengl.GL20

fun shader(
    type: Int,
    source: String
): Int {

    val shader = GL20.glCreateShader(type)
    GL20.glShaderSource(shader, source)
    GL20.glCompileShader(shader)
    return shader

}