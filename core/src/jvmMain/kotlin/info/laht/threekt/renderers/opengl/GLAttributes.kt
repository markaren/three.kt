package info.laht.threekt.renderers.opengl

import info.laht.threekt.core.BufferAttribute
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.IntBufferAttribute
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import java.util.*

internal class GLAttributes {

    private val buffers = WeakHashMap<BufferAttribute, Buffer>()

    private fun createBuffer(attribute: BufferAttribute, bufferType: Int): Buffer {

        val usage = if (attribute.dynamic) GL15.GL_DYNAMIC_DRAW else GL15.GL_STATIC_DRAW

        val buffer = GL15.glGenBuffers()
        GL15.glBindBuffer(bufferType, buffer)

        val (type, bytesPerElement) = when (attribute) {
            is IntBufferAttribute -> {
                GL15.glBufferData(bufferType, attribute.backingBuffer, usage)
                GL11.GL_UNSIGNED_INT to 3
            }
            is FloatBufferAttribute -> {
                GL15.glBufferData(bufferType, attribute.backingBuffer, usage)
                GL11.GL_FLOAT to 4
            }
        }

        return Buffer(buffer, type, bytesPerElement, attribute.version)

    }

    private fun updateBuffer(buffer: Int, attribute: BufferAttribute, bufferType: Int, bytesPerElement: Int) {

        val updateRange = attribute.updateRange

        if (updateRange.count == 0) {
            println("GLObjects.updateBuffer: dynamic BufferAttribute marked as needsUpdate but updateRange.count is 0, ensure you are using set methods or updating manually.")
            return
        }

        GL15.glBindBuffer(bufferType, buffer)

        when (attribute) {
            is IntBufferAttribute -> {
                if (!attribute.dynamic) GL15.glBufferData(bufferType, attribute.backingBuffer, GL15.GL_STATIC_DRAW)
                else if (updateRange.count == -1) GL15.glBufferSubData(bufferType, 0, attribute.backingBuffer)
                else if (updateRange.count == 0) println("GLObjects.updateBuffer: dynamic THREE.BufferAttribute marked as needsUpdate but updateRange.count is 0, ensure you are using set methods or updating manually.")
                else {
                    TODO()
//                    val sub = attribute.array.copyOfRange(updateRange.offset, updateRange.offset + updateRange.count)
//                    GL15.glBufferSubData(bufferType, (updateRange.offset + bytesPerElement).toLong(), sub)
//                    updateRange.count = -1
                }
            }
            is FloatBufferAttribute -> {
                if (!attribute.dynamic) GL15.glBufferData(bufferType, attribute.backingBuffer, GL15.GL_STATIC_DRAW)
                else if (updateRange.count == -1) GL15.glBufferSubData(bufferType, 0, attribute.backingBuffer)
                else if (updateRange.count == 0) println("GLObjects.updateBuffer: dynamic THREE.BufferAttribute marked as needsUpdate but updateRange.count is 0, ensure you are using set methods or updating manually.")
                else {
                    TODO()
//                    val sub = attribute.array.copyOfRange(updateRange.offset, updateRange.offset + updateRange.count)
//                    GL15.glBufferSubData(bufferType, (updateRange.offset + bytesPerElement).toLong(), sub)
//                    updateRange.count = -1
                }
            }
        }

    }

    fun get(attribute: BufferAttribute): Buffer {
        return buffers[attribute] ?: throw IllegalStateException("")
    }

    fun remove(attribute: BufferAttribute) {
        buffers.remove(attribute)?.also {
            GL15.glDeleteBuffers(it.buffer)
        }
    }

    fun update(attribute: BufferAttribute, bufferType: Int) {

        val data = buffers[attribute]

        if (data == null) {
            buffers[attribute] = createBuffer(attribute, bufferType)
        } else if (data.version < attribute.version) {
            updateBuffer(data.buffer, attribute, bufferType, data.bytesPerElement)
            data.version = attribute.version
        }

    }

    class Buffer(
        val buffer: Int,
        val type: Int,
        val bytesPerElement: Int,
        var version: Int
    )

}