package info.laht.threekt.renderers.opengl

import info.laht.threekt.textures.CubeTexture
import info.laht.threekt.textures.Texture
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20


class GLUniforms internal constructor(
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
        TODO()
    }

    private companion object {

        var emptyTexture = Texture()
        var emptyCubeTexture = CubeTexture()

        val arrayCacheF32 = mutableListOf<FloatArray>()
        val arrayCacheI32 = mutableListOf<IntArray>()

        val mat4array = FloatArray(16)
        val mat3array = FloatArray(9)
        val mat2array = FloatArray(4)

        fun flatten(array: FloatArray, nBlocks: Int, blockSize: Int): FloatArray {
            val firstElem = array[0];

            if (firstElem <= 0 || firstElem > 0) return array;
            // unoptimized: ! isNaN( firstElem )
            // see http://jacksondunstan.com/articles/983

            val n = nBlocks * blockSize
            var r = if (n < arrayCacheF32.size) arrayCacheF32[n] else null

            if (r == null) {

                r = FloatArray(n)
                arrayCacheF32[n] = r

            }

            if (nBlocks != 0) {

                System.arraycopy(firstElem, 0, r, 0, n)

                var offset = 0
                for (i in 0..nBlocks) {

                    offset += blockSize;
                    System.arraycopy(array[i], 0, r, offset, n)

                }

            }

            return r;
        }

        fun allocTexUnits(textures: GLTextures, n: Int): IntArray {

            var r = if (n < arrayCacheI32.size) arrayCacheI32[n] else null

            if (r == null) {

                r = IntArray(n)
                arrayCacheI32[n] = r

            }

            for (i in 0..n)
                r[i] = textures.allocateTextureUnit();

            return r

        }


        val RePathPart = "([\\w\\d_]+)(\\])?(\\[|\\.)?"

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
