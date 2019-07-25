package info.laht.threekt.renderers.opengl

import info.laht.threekt.core.Uniform
import info.laht.threekt.math.*
import info.laht.threekt.textures.CubeTexture
import info.laht.threekt.textures.Texture
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import kotlin.math.roundToInt

private var emptyTexture = Texture()
private var emptyCubeTexture = CubeTexture()

private val arrayCacheF32 = mutableListOf<FloatArray?>()
private val arrayCacheI32 = mutableListOf<IntArray?>()

private val mat4array = FloatArray(16)
private val mat3array = FloatArray(9)
private val mat2array = FloatArray(4)

internal interface Container {

    val seq: MutableList<UniformObject>
    val map: MutableMap<String, UniformObject>

}

private fun flatten(array: Array<Flattable>, nBlocks: Int, blockSize: Int): FloatArray {
    val firstElem = array[0]

    val n = nBlocks * blockSize
    var r = if (n < arrayCacheF32.size) arrayCacheF32[n] else null

    if (r == null) {

        r = FloatArray(n)
        while (n >= arrayCacheF32.size) {
            arrayCacheF32.add(null)
        }
        arrayCacheF32[n] = r

    }

    if (nBlocks != 0) {

        firstElem.toArray(r, 0)

        var offset = 0
        for (i in 1 until nBlocks) {

            offset += blockSize
            array[i].toArray(r, offset)

        }

    }

    return r
}

private fun allocTexUnits(textures: GLTextures, n: Int): IntArray {

    var r = if (n < arrayCacheI32.size) arrayCacheI32[n] else null

    if (r == null) {

        r = IntArray(n)
        arrayCacheI32[n] = r

    }

    for (i in 0 until n)
        r[i] = textures.allocateTextureUnit()

    return r

}

private fun addUniform(container: Container, uniformObject: UniformObject) {
    container.seq.add(uniformObject)
    container.map[uniformObject.id] = uniformObject
}

private fun parseUniform(activeInfo: ActiveUniformInfo, addr: Int, container: Container) {

    @Suppress("NAME_SHADOWING")
    var container = container

    val path = activeInfo.name
    val pathLength = path.length

    val RePathPart = "([\\w\\d_]+)(\\])?(\\[|\\.)?".toRegex()

    var match = RePathPart.find(path)
    while (match != null) {

        val matchEnd = match.range.last+1

        var id = match.groups[1]!!.value
        val idIsIndex = match.groups[2]?.value == "]"
        val subscript = match.groups[3]?.value

        if (idIsIndex) id = (id.toInt() or 0).toString()

        if (subscript == null || subscript == "[" && matchEnd +2 == pathLength) {

            // bare name or "pure" bottom-level array "[0]" suffix

            val uniform = if (subscript == null) {
                SingleUniform(id, activeInfo, addr)
            } else {
                PureArrayUniform(id, activeInfo, addr)
            }
            addUniform(container, uniform)

            return

        } else {

            var next = container.map[id] as StructuredUniform?

            if (next == null) {

                next = StructuredUniform(id)
                addUniform(container, next)

            }

            container = next
            match = match.next()

        }

    }

}

class GLUniforms internal constructor(
    program: Int
) : Container {

    override val seq = mutableListOf<UniformObject>()
    override val map = mutableMapOf<String, UniformObject>()

    init {

        val n = GL20.glGetProgrami(program, GL20.GL_ACTIVE_UNIFORMS)

        for (i in 0 until n) {
            val info = ActiveUniformInfo(program, i)
            val addr = GL20.glGetUniformLocation(program, info.name)
            parseUniform(info, addr, this)
        }

    }

    fun setValue(name: String, value: Any, textures: GLTextures? = null) {
        map[name]?.setValue(value, textures)
    }

    companion object {

        fun upload(seq: List<UniformObject>, values: Map<String, Uniform>, textures: GLTextures) {

            seq.forEach { u ->

                val v = values[u.id] ?: throw IllegalStateException("No uniform with id ${u.id}!")
                if (v.needsUpdate != false) {
                    v.value?.also { u.setValue(it, textures) }
                }

            }

        }

        fun seqWithValue(seq: List<UniformObject>, values: Map<String, Uniform>): List<UniformObject> {

            return seq.mapNotNull { u ->
                if (u.id in values.keys) u else null
            }

        }

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

    override fun toString(): String {
        return "ActiveUniformInfo(name='$name', size=$size, type=$type)"
    }

}

sealed class UniformObject(
    val id: String
) {

    abstract fun setValue(v: Any, textures: GLTextures? = null)

}

private class SingleUniform(
    id: String,
    activeInfo: ActiveUniformInfo,
    private val addr: Int
) : UniformObject(id) {

    private val vector2Cache by lazy { Vector2() }
    private val vector3Cache by lazy { Vector3() }
    private val colorCache by lazy { Color() }
    private val setValue = getSingularSetter(activeInfo.type)

    override fun setValue(v: Any, textures: GLTextures?) {
        setValue.invoke(v, textures)
    }

    private fun getSingularSetter(type: Int): (Any, GLTextures?) -> Unit {

        return when (type) {
            0x1406 -> { v, _ -> setValueV1f(v) }
            0x8b50 -> { v, _ -> setValueV2f(v) }
            0x8b51 -> { v, _ -> setValueV3f(v) }
            0x8b52 -> { v, _ -> setValueV4f(v) }

            0x8b5a -> throw UnsupportedOperationException() // _MAT2
            0x8b5b -> { v, _ -> setValueM3(v) } // _MAT3
            0x8b5c -> { v, _ -> setValueM4(v) } // _MAT4

            0x8b5e, 0x8d66 -> { v, t -> setValueT1(v, t!!) }; // SAMPLER_2D, SAMPLER_EXTERNAL_OES
//             0x8b5f, return setValueT3D1; // SAMPLER_3D
//             0x8b60, return setValueT6; // SAMPLER_CUBE
//             0x8DC1, return setValueT2DArray1; // SAMPLER_2D_ARRAY
//
            0x1404, 0x8b56 -> { v, _ -> setValueV1i(v) } // INT, BOOL
            0x8b53, 0x8b57 -> { v, _ -> setValueV2i(v) } // _VEC2
            0x8b54, 0x8b58 -> { v, _ -> setValueV3i(v) } // _VEC3
            0x8b55, 0x8b59 -> { v, _ -> setValueV4i(v) } // _VEC4

            else -> throw IllegalArgumentException("Illegal or unsupported type encountered: $type")

        }

    }

    fun setValueT1(v: Any, textures: GLTextures) {
        when (v) {
            is Texture -> {
                val unit = textures.allocateTextureUnit()
                GL20.glUniform1i(addr, unit)
                textures.setTexture2D(v, unit)
            }
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueV1i(v: Any) {
        when (v) {
            is Int -> GL20.glUniform1i(addr, v)
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueV2i(v: Any) {
        when (v) {
            is IntArray -> GL20.glUniform2iv(addr, v)
            is Vector2 -> GL20.glUniform2i(addr, v.x.roundToInt(), v.y.roundToInt())
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueV3i(v: Any) {
        when (v) {
            is IntArray -> GL20.glUniform3iv(addr, v)
            is Vector3 -> GL20.glUniform3i(addr, v.x.roundToInt(), v.y.roundToInt(), v.z.roundToInt())
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueV4i(v: Any) {
        when (v) {
            is IntArray -> GL20.glUniform4iv(addr, v)
            is Vector4 -> GL20.glUniform4i(addr, v.x.roundToInt(), v.y.roundToInt(), v.z.roundToInt(), v.w.roundToInt())
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueV1f(v: Any) {
        when (v) {
            is Float -> GL20.glUniform1f(addr, v)
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueV2f(v: Any) {
        when (v) {
            is FloatArray -> GL20.glUniform2fv(addr, v)
            is Vector2 -> {
                if (vector2Cache == v) return
                GL20.glUniform2f(addr, v.x, v.y)
                vector2Cache.copy(v)
            }
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueV3f(v: Any) {
        when (v) {
            is FloatArray -> GL20.glUniform3fv(addr, v)
            is Vector3 -> {
                if (vector3Cache == v) return
                GL20.glUniform3f(addr, v.x, v.y, v.z)

            }
            is Color -> {
                if (colorCache == v) return
                GL20.glUniform3f(addr, v.r, v.g, v.b)
                colorCache.copy(v)
            }
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueV4f(v: Any) {
        when (v) {
            is FloatArray -> GL20.glUniform4fv(addr, v)
            is Vector4 -> GL20.glUniform4f(addr, v.x, v.y, v.z, v.w)
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueM3(v: Any) {
        when (v) {
            is Matrix3 -> GL20.glUniformMatrix3fv(addr, false, v.toArray(mat3array))
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    fun setValueM4(v: Any) {
        when (v) {
            is Matrix4 -> GL20.glUniformMatrix4fv(addr, false, v.toArray(mat4array))
            else -> throw IllegalArgumentException("Illegal type encountered: $v")
        }
    }

    override fun toString(): String {
        return "SingleUniform(id=$id, addr=$addr)"
    }

}

private class PureArrayUniform(
    id: String,
    activeInfo: ActiveUniformInfo,
    private val addr: Int
) : UniformObject(id) {

    private val setValue = getPureArraySetter(activeInfo.type, addr, activeInfo.size)

    override fun setValue(v: Any, textures: GLTextures?) {
        setValue.invoke(v)
    }

    private fun getPureArraySetter(type: Int, addr: Int, size: Int): (Any) -> Unit {

        return when (type) {

            0x1406 -> { v -> GL20.glUniform1fv(addr, v as FloatArray) } // FLOAT
            0x8b50 -> { v -> GL20.glUniform2fv(addr, flatten(v as Array<Flattable>, size, 2)) } // _VEC2
            0x8b51 -> { v -> GL20.glUniform3fv(addr, flatten(v as Array<Flattable>, size, 3)) } // _VEC3
            0x8b52 -> { v -> GL20.glUniform4fv(addr, flatten(v as Array<Flattable>, size, 4)) } // _VEC4

            0x8b5b -> { v ->
                GL20.glUniformMatrix3fv(
                    addr,
                    false,
                    flatten(v as Array<Flattable>, size, 9)
                )
            } // _MAT3
            0x8b5c -> { v ->
                GL20.glUniformMatrix3fv(
                    addr,
                    false,
                    flatten(v as Array<Flattable>, size, 16)
                )
            } // _MAT4

//            0x8b5e -> { v -> TODO() } // SAMPLER_2D
//            0x8b60 -> { v -> TODO() } // SAMPLER_CUBE

            0x1404, 0x8b56 -> { v -> GL20.glUniform1iv(addr, v as IntArray) } // INT, BOOL
            0x8b53, 0x8b57 -> { v -> GL20.glUniform2iv(addr, v as IntArray) } // _VEC2
            0x8b54, 0x8b58 -> { v -> GL20.glUniform3iv(addr, v as IntArray) } // _VEC3
            0x8b55, 0x8b59 -> { v -> GL20.glUniform4iv(addr, v as IntArray) } // _VEC4

            else -> throw IllegalArgumentException("Unsupported type: $type")

        }

    }

    override fun toString(): String {
        return "PureArrayUniform(id=$id, addr=$addr)"
    }

}

private class StructuredUniform(
    id: String
) : UniformObject(id), Container {

    override val seq = mutableListOf<UniformObject>()
    override val map = mutableMapOf<String, UniformObject>()

    override fun setValue(value: Any, textures: GLTextures?) {

        when (value) {
            is List<*> -> {
                seq.forEach { u ->
                    u.setValue(value[u.id.toInt()]!!, textures)
                }
            }
            is Map<*, *> -> {
                seq.forEach { u ->
                    u.setValue(value[u.id]!!, textures)
                }
            }
            else -> throw IllegalArgumentException()
        }


    }

}
