package info.laht.threekt.math

import org.lwjgl.BufferUtils
import java.nio.FloatBuffer


fun Vector2.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(3)
    return buf.put(x).put(y)
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
