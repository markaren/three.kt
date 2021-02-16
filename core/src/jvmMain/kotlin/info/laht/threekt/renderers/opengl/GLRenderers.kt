package info.laht.threekt.renderers.opengl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL31
import kotlin.properties.Delegates

internal sealed class _GLRenderer {

    var mode by Delegates.notNull<Int>()

    abstract fun render(start: Int, count: Int)

    abstract fun renderInstances(start: Int, count: Int, primCount: Int)

}

internal class GLBufferRenderer(
    private val info: GLInfo
) : _GLRenderer() {

    override fun render(start: Int, count: Int) {
        GL11.glDrawArrays(mode, start, count)
        info.update(count, mode)
    }

    override fun renderInstances(start: Int, count: Int, primCount: Int) {
        if (count == 0) {
            return
        }
        GL31.glDrawArraysInstanced(mode, start, count, primCount)
        info.update(count, mode, primCount)
    }

}

internal class GLIndexedBufferRenderer(
    private val info: GLInfo
) : _GLRenderer() {

    private var type by Delegates.notNull<Int>()
    private var bytesPerElement by Delegates.notNull<Int>()

    fun setIndex(value: GLAttributes.Buffer) {
        type = value.type
        bytesPerElement = value.bytesPerElement
    }

    override fun render(start: Int, count: Int) {
        GL11.glDrawElements(mode, count, type, (start * bytesPerElement).toLong())
        info.update(count, mode)
    }

    override fun renderInstances(start: Int, count: Int, primCount: Int) {
        GL31.glDrawElementsInstanced(mode, count, type, (start * bytesPerElement).toLong(), primCount)
        info.update(count, mode, primCount)
    }

}
