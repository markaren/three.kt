package info.laht.threekt.renderers.opengl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20


class GLUniforms(
    program: Int
) {

    val seq = mutableListOf<UniformObject>()
    val map = mutableMapOf<Int, UniformObject>()

    init {

        val n = GL20.glGetProgrami(program, GL20.GL_ACTIVE_UNIFORMS)

        for (i in 0 until n) {
            val info = ActiveUniformInfo(program, i)
            val addr = GL20.glGetUniformLocation(program, info.name)
            parseUniform(info, addr)
        }

    }

    private fun addUniform(uniformObject: UniformObject) {
        seq.add(uniformObject)
        map[uniformObject.id] = uniformObject
    }

    private fun parseUniform(activeInfo: ActiveUniformInfo, addr: Int) {
        val path = activeInfo.name
        val pathLength = path.length

    }

    companion object {

        private val RePathPart = "([\\w\\d_]+)(\\])?(\\[|\\.)?"

//        fun upload(seq: List<UniformObject>, values: Map)

    }

}

private class ActiveUniformInfo(
    program: Int,
    index: Int
) {

    val name: String
    val size: Int
    val type: Int

    init {

        val sizeBuffer = BufferUtils.createIntBuffer(1)
        val typeBuffer = BufferUtils.createIntBuffer(1)
        name = GL20.glGetActiveUniform(program, index, sizeBuffer, typeBuffer)
        size = sizeBuffer.get()
        type = typeBuffer.get()

    }

}

sealed class UniformObject(
    val id: Int,
    val addr: String
)

private class SingleUniform(
    id: Int,
    addr: String
) : UniformObject(id, addr) {

}

private class PureArrayUniform(
    id: Int,
    addr: String
) : UniformObject(id, addr) {

}
