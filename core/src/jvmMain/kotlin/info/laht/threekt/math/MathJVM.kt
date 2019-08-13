package info.laht.threekt.math

import kotlinx.io.core.ByteOrder
import kotlinx.io.core.IoBuffer
import kotlinx.io.core.writeFloat
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer


fun Vector2.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(3)
    return buf.put(x).put(y)
}

fun Vector2.toBuffer(buffer: IoBuffer, offset: Int): IoBuffer {
    buffer.writeFloat(x, ByteOrder.nativeOrder())
    buffer.writeFloat(y, ByteOrder.nativeOrder())
    return buffer
}

fun Vector3.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(3)
    return buf.put(x).put(y).put(z)
}

fun Vector4.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(2)
    return buf.put(x).put(y).put(z).put(w)
}

fun Matrix3.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {

    val buf = buffer ?: BufferUtils.createFloatBuffer(size)
    elements.forEachIndexed { i, v ->
        buf.put(i + offset, v)
    }

    return buf
}

fun Matrix4.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {

    val buf = buffer ?: BufferUtils.createFloatBuffer(size)
    elements.forEachIndexed { i, v ->
        buf.put(i + offset, v)
    }

    return buf
}
