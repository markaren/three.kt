package info.laht.threekt.renderers.opengl

import org.lwjgl.opengl.GL20


fun createShader(
    type: Int,
    source: String
): Int {

    val shader = GL20.glCreateShader(type)
    GL20.glShaderSource(shader, source)
    GL20.glCompileShader(shader)

//    println(source)
//    println(GL20.glGetShaderInfoLog(shader))

    return shader

}
