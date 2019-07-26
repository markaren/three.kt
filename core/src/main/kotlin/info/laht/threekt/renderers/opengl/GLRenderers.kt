package info.laht.threekt.renderers.opengl

import info.laht.threekt.core.InstancedBufferGeometry
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL31
import kotlin.properties.Delegates

internal sealed class _GLRenderer {

    var mode by Delegates.notNull<Int>()

    abstract fun render(start: Int, count: Int)

}

internal class GLBufferRenderer(
    private val info: GLInfo
) : _GLRenderer() {

    override fun render(start: Int, count: Int) {
        GL11.glDrawArrays(mode, start, count)
        info.update(count, mode)
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

    fun renderInstances(geometry: InstancedBufferGeometry, start: Int, count: Int) {
        GL31.glDrawElementsInstanced(mode, count, type, (start * bytesPerElement).toLong(), geometry.maxInstancedCount)
    }

}
